package com.udistrital.entrevistas.ui.entrevista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udistrital.entrevistas.data.local.Caso
import com.udistrital.entrevistas.data.local.Modalidad
import com.udistrital.entrevistas.data.repository.EntrevistaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class EntrevistaUiState(
    val casoSeleccionado: Caso? = null,
    val nombrePersona: String = "",
    val fecha: LocalDate = LocalDate.now(),
    val modalidad: Modalidad = Modalidad.PRESENCIAL,
    val hallazgos: String = "",
    val guardadoOk: Boolean = false,
    val error: String? = null
)

class FormularioEntrevistaViewModel(
    private val repositorio: EntrevistaRepository
) : ViewModel() {

    val casosDisponibles: StateFlow<List<Caso>> = repositorio.observarCasos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(EntrevistaUiState())
    val uiState: StateFlow<EntrevistaUiState> = _uiState.asStateFlow()

    fun seleccionarCaso(caso: Caso) {
        _uiState.update { it.copy(casoSeleccionado = caso, error = null) }
    }

    // Para cuando se entra desde el detalle de un caso ya identificado
    fun precargarCaso(idCaso: Long) {
        casosDisponibles.value.firstOrNull { it.idCaso == idCaso }?.let { seleccionarCaso(it) }
    }

    fun actualizarCampo(
        nombrePersona: String? = null,
        modalidad: Modalidad? = null,
        hallazgos: String? = null,
        fecha: LocalDate? = null
    ) {
        _uiState.update {
            it.copy(
                nombrePersona = nombrePersona ?: it.nombrePersona,
                modalidad = modalidad ?: it.modalidad,
                hallazgos = hallazgos ?: it.hallazgos,
                fecha = fecha ?: it.fecha
            )
        }
    }

    fun guardarEntrevista() {
        val estado = _uiState.value
        val caso = estado.casoSeleccionado
        if (caso == null) {
            _uiState.update { it.copy(error = "Selecciona un caso") }
            return
        }
        viewModelScope.launch {
            try {
                repositorio.crearEntrevista(
                    idCaso = caso.idCaso,
                    nombrePersona = estado.nombrePersona,
                    fecha = estado.fecha,
                    modalidad = estado.modalidad,
                    hallazgos = estado.hallazgos
                )
                _uiState.update { it.copy(guardadoOk = true, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    class Factory(private val repositorio: EntrevistaRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FormularioEntrevistaViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FormularioEntrevistaViewModel(repositorio) as T
            }
            throw IllegalArgumentException("ViewModel no reconocido")
        }
    }
}