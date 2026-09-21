package com.udistrital.entrevistas.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Periodista::class, Persona::class, Caso::class, Entrevista::class, Evidencia::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun periodistaDao(): PeriodistaDao
    abstract fun personaDao(): PersonaDao
    abstract fun casoDao(): CasoDao
    abstract fun entrevistaDao(): EntrevistaDao
    abstract fun evidenciaDao(): EvidenciaDao

    companion object {
        @Volatile private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase = instancia ?: synchronized(this) {
            instancia ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "entrevistas.db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL("INSERT INTO periodista (nombre, correo) VALUES ('Periodista', '')")
                    }
                })
                .build().also { instancia = it }
        }
    }
}