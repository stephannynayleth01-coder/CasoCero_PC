package com.udistrital.entrevistas.ui.lista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.udistrital.entrevistas.data.local.Caso
import androidx.compose.material3.ExperimentalMaterial3Api


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun ListaScreen(
    viewModel: ListaViewModel,
    onVerCaso: (Long) -> Unit,
    onCrearCaso: () -> Unit,
    onVerResumen: () -> Unit
) {
    val texto by viewModel.texto.collectAsState()
    val casos by viewModel.casos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Casos") },
                actions = {
                    IconButton(onClick = onVerResumen) {
                        Icon(Icons.Default.BarChart, contentDescription = "Ver resumen")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCrearCaso) {
                Icon(Icons.Default.Add, contentDescription = "Crear caso")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = texto,
                onValueChange = viewModel::buscar,
                label = { Text("Buscar por título") },
                modifier = Modifier.fillMaxWidth()
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(casos) { caso -> TarjetaCaso(caso, alTocar = { onVerCaso(caso.idCaso) }) }
            }
        }
    }
}

@Composable
private fun TarjetaCaso(caso: Caso, alTocar: () -> Unit) {
    Card(onClick = alTocar, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(caso.titulo, style = MaterialTheme.typography.titleMedium)
            Text(caso.fecha.toString(), style = MaterialTheme.typography.bodySmall)
            Text(caso.estado.name.replace("_", " "), style = MaterialTheme.typography.bodyMedium)
        }
    }
}