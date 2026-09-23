package com.udistrital.entrevistas.ui.theme.ui.caso

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udistrital.entrevistas.data.local.Caso
import com.udistrital.entrevistas.data.local.EstadoCaso
import com.udistrital.entrevistas.data.repository.CasoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.udistrital.entrevistas.data.local.Entrevista
import com.udistrital.entrevistas.data.repository.EntrevistaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import com.udistrital.entrevistas.data.local.EntrevistaConPersona

data class CasoUiState(
    val id: Long? = null,
    val titulo: String = "",
    val descripcion: String = "",
    val estado: EstadoCaso = EstadoCaso.EN_INVESTIGACION,
    val conclusion: String = "",
    val fecha: LocalDate = LocalDate.now(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class CasoDetalleViewModel(
    private val repositorio: CasoRepository,
    private val entrevistaRepositorio: EntrevistaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CasoUiState())
    val uiState: StateFlow<CasoUiState> = _uiState.asStateFlow()

    private val _idCasoActual = MutableStateFlow<Long?>(null)
    val entrevistas: StateFlow<List<EntrevistaConPersona>> = _idCasoActual
        .flatMapLatest { id ->
            if (id != null) entrevistaRepositorio.observarEntrevistasDeCaso(id)
            else kotlinx.coroutines.flow.flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cargarCaso(casoId: Long) {
        _idCasoActual.value = casoId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repositorio.observarCaso(casoId).collect { c ->
                    if (c != null) {
                        _uiState.update {
                            it.copy(
                                id = c.idCaso,
                                titulo = c.titulo,
                                descripcion = c.descripcion,
                                estado = c.estado,
                                conclusion = c.conclusion ?: "",
                                fecha = c.fecha,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update { it.copy(error = "Caso no encontrado", isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun actualizarCampo(
        titulo: String? = null,
        descripcion: String? = null,
        conclusion: String? = null,
        estado: EstadoCaso? = null,
        fecha: LocalDate? = null
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                titulo = titulo ?: currentState.titulo,
                descripcion = descripcion ?: currentState.descripcion,
                conclusion = conclusion ?: currentState.conclusion,
                estado = estado ?: currentState.estado,
                fecha = fecha ?: currentState.fecha
            )
        }
    }

    fun guardarCaso() {
        viewModelScope.launch {
            val estadoActual = _uiState.value
            if (estadoActual.titulo.isBlank() || estadoActual.descripcion.isBlank()) {
                _uiState.update { it.copy(error = "El título y la descripción son obligatorios") }
                return@launch
            }

            val casoEntidad = Caso(
                idCaso = estadoActual.id ?: 0L,
                titulo = estadoActual.titulo,
                descripcion = estadoActual.descripcion,
                estado = estadoActual.estado,
                conclusion = estadoActual.conclusion.ifBlank { null },
                fecha = estadoActual.fecha
            )

            try {
                if (estadoActual.id == null || estadoActual.id == 0L) {
                    repositorio.crearCaso(casoEntidad.titulo, casoEntidad.descripcion, casoEntidad.fecha)
                } else {
                    repositorio.editarCaso(casoEntidad)
                }
                _uiState.update { it.copy(error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun eliminarCaso() {
        val casoId = _uiState.value.id ?: return
        viewModelScope.launch {
            try {
                repositorio.eliminarCasoPorId(casoId)
                _uiState.update { it.copy(error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun cerrarCaso(conclusionFinal: String) {
        viewModelScope.launch {
            val idCaso = _uiState.value.id ?: return@launch
            val casoActual = Caso(
                idCaso = idCaso,
                titulo = _uiState.value.titulo,
                descripcion = _uiState.value.descripcion,
                fecha = _uiState.value.fecha,
                estado = _uiState.value.estado,
                conclusion = _uiState.value.conclusion
            )
            try {
                repositorio.cerrarCaso(casoActual, conclusionFinal)
                _uiState.update {
                    it.copy(
                        estado = EstadoCaso.CERRADO,
                        conclusion = conclusionFinal,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    class CasoViewModelFactory(private val repositorio: CasoRepository,
                               private val entrevistaRepositorio: EntrevistaRepository   ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CasoDetalleViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CasoDetalleViewModel(repositorio, entrevistaRepositorio) as T
            }
            throw IllegalArgumentException("ViewModel no reconocido")
        }
    }
    fun actualizarEstado(nuevoEstado: EstadoCaso) { // Ajusta el nombre del enum según Entidades.kt
        _uiState.update { it.copy(estado = nuevoEstado) }
    }
}
