package com.udistrital.entrevistas.data.local
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.UUID
@Entity(tableName = "periodista")
data class Periodista(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_periodista") val idPeriodista: Long = 0,
    val nombre: String,
    val correo: String
)

@Entity(tableName = "persona", indices = [Index(value = ["identificacion"], unique = true)])
data class Persona(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_persona") val idPersona: Long = 0,
    val identificacion: String? = null,
    val nombre: String,
    val telefono: String? = null,
    val correo: String? = null,
    val ciudad: String? = null,
    val rol: String? = null
)

@Entity(tableName = "caso")
data class Caso(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_caso") val idCaso: Long = 0,
    val titulo: String,
    val descripcion: String,
    val fecha: LocalDate= LocalDate.now(),
    val estado: EstadoCaso = EstadoCaso.ABIERTO,
    val conclusion: String? = null
)

@Entity(
    tableName = "entrevista",
    foreignKeys = [
        ForeignKey(Caso::class, ["id_caso"], ["id_caso"], onDelete = ForeignKey.CASCADE),
        ForeignKey(
            Periodista::class,
            ["id_periodista"],
            ["id_periodista"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(Persona::class, ["id_persona"], ["id_persona"], onDelete = ForeignKey.RESTRICT)
    ],
    indices = [Index("id_caso"), Index("id_periodista"), Index("id_persona")]
)
data class Entrevista(
    @PrimaryKey @ColumnInfo(name = "id_entrevista") val idEntrevista: String = UUID.randomUUID()
        .toString(),
    val fecha: LocalDate,
    val modalidad: Modalidad,
    val hallazgos: String,
    @ColumnInfo(name = "id_caso") val idCaso: Long,
    @ColumnInfo(name = "id_periodista") val idPeriodista: Long,
    @ColumnInfo(name = "id_persona") val idPersona: Long
)

@Entity(
    tableName = "evidencia",
    primaryKeys = ["id_entrevista", "num_evidencia"],
    foreignKeys = [ForeignKey(
        Entrevista::class,
        ["id_entrevista"],
        ["id_entrevista"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Evidencia(
    @ColumnInfo(name = "id_entrevista") val idEntrevista: String,
    @ColumnInfo(name = "num_evidencia") val numEvidencia: Int,
    val tipo: TipoEvidencia,
    @ColumnInfo(name = "fecha_carga") val fechaCarga: LocalDate,
    val url: String
)