package com.udistrital.entrevistas.data.local
import androidx.room.TypeConverter
import java.time.LocalDate
import java.util.UUID
enum class EstadoCaso { ABIERTO, EN_INVESTIGACION, CERRADO }
enum class Modalidad { PRESENCIAL, TELEFONICA, VIRTUAL }
enum class TipoEvidencia { FOTO, AUDIO, DOCUMENTO }

class Converters {
    @TypeConverter
    fun fechaATexto(f: LocalDate?): String? = f?.toString()
    @TypeConverter
    fun textoAFecha(s: String?): LocalDate? = s?.let(LocalDate::parse)
}