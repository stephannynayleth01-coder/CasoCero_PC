package com.udistrital.entrevistas.data.local
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Embedded
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
data class ConteoEstado(val estado: EstadoCaso, val total: Int)

data class EntrevistaConPersona(
    @Embedded val entrevista: Entrevista,
    val nombrePersona: String
)

@Dao
interface CasoDao {
    @Insert suspend fun insertar(caso: Caso): Long
    @Update suspend fun actualizar(caso: Caso)
    @Delete suspend fun eliminar(caso: Caso)

    @Query("SELECT * FROM caso WHERE id_caso = :idCaso")
    fun observar(idCaso: Long): Flow<Caso?>

    @Query("SELECT * FROM caso WHERE titulo LIKE '%' || :texto || '%' ORDER BY fecha DESC")
    fun buscar(texto: String): Flow<List<Caso>>

    @Query("SELECT estado, COUNT(*) AS total FROM caso GROUP BY estado")
    fun contarPorEstado(): Flow<List<ConteoEstado>>

    @Query("DELETE FROM caso WHERE id_caso = :idCaso")
    suspend fun eliminarCasoPorId(idCaso: Long): Int

    @Query("UPDATE caso SET titulo = :titulo, descripcion = :descripcion, fecha = :fecha, estado = :estado, conclusion = :conclusion WHERE id_caso = :idCaso")
    suspend fun actualizarCasoPorId(idCaso: Long, titulo: String, descripcion: String, fecha: LocalDate, estado: EstadoCaso, conclusion: String?): Int
}

@Dao
interface EntrevistaDao {
    @Insert suspend fun insertar(entrevista: Entrevista)
    @Update suspend fun actualizar(entrevista: Entrevista)
    @Delete suspend fun eliminar(entrevista: Entrevista)

    @Query("SELECT * FROM entrevista WHERE id_caso = :idCaso ORDER BY fecha DESC")
    fun observarPorCaso(idCaso: Long): Flow<List<Entrevista>>

    @Query("""
        SELECT entrevista.*, persona.nombre AS nombrePersona
        FROM entrevista
        INNER JOIN persona ON entrevista.id_persona = persona.id_persona
        WHERE entrevista.id_caso = :idCaso
        ORDER BY entrevista.fecha DESC
    """)
    fun observarPorCasoConPersona(idCaso: Long): Flow<List<EntrevistaConPersona>>

    @Query("DELETE FROM entrevista WHERE id_entrevista = :idEntrevista")
    suspend fun eliminarEntrevistaPorId(idEntrevista: String): Int

    @Query("UPDATE entrevista SET fecha = :fecha, modalidad = :modalidad, hallazgos = :hallazgos, id_caso = :idCaso, id_periodista = :idPeriodista, id_persona = :idPersona WHERE id_entrevista = :idEntrevista")
    suspend fun actualizarEntrevistaPorId(idEntrevista: String, fecha: LocalDate, modalidad: Modalidad, hallazgos: String, idCaso: Long, idPeriodista: Long, idPersona: Long): Int
}

@Dao
abstract class EvidenciaDao {
    @Insert abstract suspend fun insertar(evidencia: Evidencia)
    @Delete abstract suspend fun eliminar(evidencia: Evidencia)

    @Query("SELECT * FROM evidencia WHERE id_entrevista = :idEntrevista ORDER BY num_evidencia")
    abstract fun observarPorEntrevista(idEntrevista: String): Flow<List<Evidencia>>

    @Query("SELECT COALESCE(MAX(num_evidencia), 0) + 1 FROM evidencia WHERE id_entrevista = :idEntrevista")
    abstract suspend fun siguienteNumero(idEntrevista: String): Int

    // El discriminante se calcula dentro de una transacción para evitar duplicados
    @Transaction
    open suspend fun agregar(idEntrevista: String, tipo: TipoEvidencia, fechaCarga: LocalDate, url: String) {
        insertar(Evidencia(idEntrevista, siguienteNumero(idEntrevista), tipo, fechaCarga, url))
    }

    @Query("DELETE FROM evidencia WHERE id_entrevista = :idEntrevista AND num_evidencia = :numEvidencia")
    abstract suspend fun eliminarEvidenciaPorId(idEntrevista: String, numEvidencia: Int): Int

    @Query("UPDATE evidencia SET tipo = :tipo, fecha_carga = :fechaCarga, url = :url WHERE id_entrevista = :idEntrevista AND num_evidencia = :numEvidencia")
    abstract suspend fun actualizarEvidenciaPorId(idEntrevista: String, numEvidencia: Int, tipo: TipoEvidencia, fechaCarga: LocalDate, url: String): Int
}

@Dao
interface PeriodistaDao {
    @Insert suspend fun insertar(periodista: Periodista): Long
    @Query("SELECT * FROM periodista LIMIT 1") suspend fun obtenerUnico(): Periodista?

    @Query("DELETE FROM periodista WHERE id_periodista = :idPeriodista")
    suspend fun eliminarPeriodistaPorId(idPeriodista: Long): Int

    @Query("UPDATE periodista SET nombre = :nombre, correo = :correo WHERE id_periodista = :idPeriodista")
    suspend fun actualizarPeriodistaPorId(idPeriodista: Long, nombre: String, correo: String): Int
}

@Dao
interface PersonaDao {
    @Insert suspend fun insertar(persona: Persona): Long
    @Query("SELECT * FROM persona ORDER BY nombre") fun observarTodas(): Flow<List<Persona>>

    @Query("DELETE FROM persona WHERE id_persona = :idPersona")
    suspend fun eliminarPersonaPorId(idPersona: Long): Int

    @Query("UPDATE persona SET identificacion = :identificacion, nombre = :nombre, telefono = :telefono, correo = :correo, ciudad = :ciudad, rol = :rol WHERE id_persona = :idPersona")
    suspend fun actualizarPersonaPorId(idPersona: Long, identificacion: String?, nombre: String, telefono: String?, correo: String?, ciudad: String?, rol: String?): Int
}
