package com.udistrital.entrevistas.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udistrital.entrevistas.data.local.Caso
import com.udistrital.entrevistas.data.repository.CasoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class ListaViewModel(private val repositorio: CasoRepository) : ViewModel() {
    private val _texto = MutableStateFlow("")
    val texto: StateFlow<String> = _texto

    val casos: StateFlow<List<Caso>> = _texto
        .flatMapLatest { repositorio.buscarCasos(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun buscar(nuevoTexto: String) {
        _texto.value = nuevoTexto
    }

    class ListaViewModelFactory(private val repositorio: CasoRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ListaViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ListaViewModel(repositorio) as T
            }
            throw IllegalArgumentException("ViewModel no reconocido")
        }
    }
}