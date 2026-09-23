package com.udistrital.entrevistas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.udistrital.entrevistas.data.local.AppDatabase
import com.udistrital.entrevistas.data.repository.CasoRepository
import com.udistrital.entrevistas.data.repository.EntrevistaRepository
import com.udistrital.entrevistas.ui.theme.InterviewsTheme
import com.udistrital.entrevistas.ui.theme.NotaVivaNavegacion

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.obtener(this)
        val repositorio = CasoRepository(database)
        val entrevistaRepositorio = EntrevistaRepository(database)

        setContent {
            InterviewsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NotaVivaNavegacion(repositorio = repositorio,
                        entrevistaRepositorio = entrevistaRepositorio)
                }
            }
        }
    }
}
