package com.udistrital.entrevistas.ui.resumen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udistrital.entrevistas.data.local.EstadoCaso
import com.udistrital.entrevistas.data.repository.CasoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ResumenUi(
    val total: Int = 0,
    val porEstado: Map<EstadoCaso, Int> = emptyMap()
)

class ResumenViewModel(repositorio: CasoRepository) : ViewModel() {
    val estado: StateFlow<ResumenUi> = repositorio.resumenPorEstado()
        .map { lista ->
            ResumenUi(
                total = lista.sumOf { it.total },
                porEstado = lista.associate { it.estado to it.total }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ResumenUi())

    class ResumenViewModelFactory(private val repositorio: CasoRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ResumenViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ResumenViewModel(repositorio) as T
            }
            throw IllegalArgumentException("ViewModel no reconocido")
        }
    }
}