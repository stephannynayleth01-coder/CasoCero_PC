package com.udistrital.entrevistas.data.repository

import com.udistrital.entrevistas.data.local.AppDatabase
import com.udistrital.entrevistas.data.local.Caso
import com.udistrital.entrevistas.data.local.EstadoCaso
import com.udistrital.entrevistas.domain.ReglasCaso
import java.time.LocalDate

class CasoRepository(private val db: AppDatabase) {
    fun observarCaso(id: Long) = db.casoDao().observar(id)

    suspend fun crearCaso(titulo: String, descripcion: String, fecha: LocalDate): Long {
        require(ReglasCaso.tituloValido(titulo)) { "El título es obligatorio" }
        return db.casoDao().insertar(Caso(titulo = titulo.trim(), descripcion = descripcion.trim(), fecha = fecha))
    }

    suspend fun editarCaso(caso: Caso) {
        require(ReglasCaso.tituloValido(caso.titulo)) { "El título es obligatorio" }
        db.casoDao().actualizar(caso)
    }

    suspend fun cerrarCaso(caso: Caso, conclusion: String) {
        require(ReglasCaso.puedeCerrarse(conclusion)) { "Para cerrar el caso debes escribir una conclusión" }
        db.casoDao().actualizar(caso.copy(estado = EstadoCaso.CERRADO, conclusion = conclusion.trim()))
    }

    suspend fun eliminarCasoPorId(idCaso: Long) = db.casoDao().eliminarCasoPorId(idCaso)
}
