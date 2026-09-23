package com.udistrital.entrevistas.data.repository

import com.udistrital.entrevistas.data.local.AppDatabase
import com.udistrital.entrevistas.data.local.Evidencia
import com.udistrital.entrevistas.data.local.TipoEvidencia
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class EvidenciaRepository(private val db: AppDatabase) {

    fun observarPorEntrevista(idEntrevista: String): Flow<List<Evidencia>> =
        db.evidenciaDao().observarPorEntrevista(idEntrevista)

    suspend fun agregarEvidencia(idEntrevista: String, tipo: TipoEvidencia, url: String) {
        require(url.isNotBlank()) { "La URL o ruta de la evidencia es obligatoria" }
        db.evidenciaDao().agregar(idEntrevista, tipo, LocalDate.now(), url.trim())
    }

    suspend fun eliminarEvidencia(idEntrevista: String, numEvidencia: Int) {
        db.evidenciaDao().eliminarEvidenciaPorId(idEntrevista, numEvidencia)
    }
}