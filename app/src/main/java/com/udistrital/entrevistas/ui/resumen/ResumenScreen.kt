package com.udistrital.entrevistas.ui.resumen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.udistrital.entrevistas.data.local.EstadoCaso
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenScreen(viewModel: ResumenViewModel, onNavigateBack: () -> Unit) {
    val ui by viewModel.estado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen de casos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Total de casos: ${ui.total}", style = MaterialTheme.typography.titleLarge)

            EstadoCaso.entries.forEach { estado ->
                val cantidad = ui.porEstado[estado] ?: 0
                Card {
                    Column(Modifier.padding(16.dp)) {
                        Text(estado.name.replace("_", " "), style = MaterialTheme.typography.titleMedium)
                        Text("$cantidad casos", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}