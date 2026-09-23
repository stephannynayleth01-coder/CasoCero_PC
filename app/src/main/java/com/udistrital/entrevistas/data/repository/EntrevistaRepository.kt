package com.udistrital.entrevistas.data.repository

import com.udistrital.entrevistas.data.local.AppDatabase
import com.udistrital.entrevistas.data.local.Caso
import com.udistrital.entrevistas.data.local.Entrevista
import com.udistrital.entrevistas.data.local.Modalidad
import com.udistrital.entrevistas.data.local.Persona
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import com.udistrital.entrevistas.data.local.EntrevistaConPersona

class EntrevistaRepository(private val db: AppDatabase) {

    // Reutilizamos buscar("") del CasoDao: con texto vacío el LIKE '%%' trae todos los casos
    fun observarCasos(): Flow<List<Caso>> = db.casoDao().buscar("")

    fun observarEntrevistasDeCaso(idCaso: Long): Flow<List<EntrevistaConPersona>> =
        db.entrevistaDao().observarPorCasoConPersona(idCaso)

    suspend fun crearEntrevista(
        idCaso: Long,
        nombrePersona: String,
        fecha: LocalDate,
        modalidad: Modalidad,
        hallazgos: String
    ) {
        require(idCaso > 0) { "Debes seleccionar un caso" }
        require(nombrePersona.isNotBlank()) { "El nombre de la persona entrevistada es obligatorio" }
        require(hallazgos.isNotBlank()) { "Los hallazgos son obligatorios" }

        val idPersona = db.personaDao().insertar(Persona(nombre = nombrePersona.trim()))
        val periodista = db.periodistaDao().obtenerUnico()
            ?: error("No hay periodista registrado")

        db.entrevistaDao().insertar(
            Entrevista(
                fecha = fecha,
                modalidad = modalidad,
                hallazgos = hallazgos.trim(),
                idCaso = idCaso,
                idPeriodista = periodista.idPeriodista,
                idPersona = idPersona
            )
        )
    }
}