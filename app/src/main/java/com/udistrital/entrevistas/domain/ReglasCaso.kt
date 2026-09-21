package com.udistrital.entrevistas.domain

object ReglasCaso {
    fun tituloValido(titulo: String) = titulo.isNotBlank()
    fun puedeCerrarse(conclusion: String?) = !conclusion.isNullOrBlank()
}