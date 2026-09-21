package com.udistrital.entrevistas.data.local
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
data class ConteoEstado(val estado: EstadoCaso, val total: Int)

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
}

@Dao
interface EntrevistaDao {
    @Insert suspend fun insertar(entrevista: Entrevista)
    @Update suspend fun actualizar(entrevista: Entrevista)
    @Delete suspend fun eliminar(entrevista: Entrevista)

    @Query("SELECT * FROM entrevista WHERE id_caso = :idCaso ORDER BY fecha DESC")
    fun observarPorCaso(idCaso: Long): Flow<List<Entrevista>>
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
}

@Dao
interface PeriodistaDao {
    @Insert suspend fun insertar(periodista: Periodista): Long
    @Query("SELECT * FROM periodista LIMIT 1") suspend fun obtenerUnico(): Periodista?
}

@Dao
interface PersonaDao {
    @Insert suspend fun insertar(persona: Persona): Long
    @Query("SELECT * FROM persona ORDER BY nombre") fun observarTodas(): Flow<List<Persona>>
}