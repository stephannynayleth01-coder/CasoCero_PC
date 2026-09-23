package com.udistrital.entrevistas.ui.lista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.udistrital.entrevistas.data.local.Caso
import com.udistrital.entrevistas.data.local.EstadoCaso
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.Description

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    viewModel: ListaViewModel,
    onVerCaso: (Long) -> Unit,
    onCrearCaso: () -> Unit,
    onVerResumen: () -> Unit,
    onCrearEntrevista: () -> Unit
) {
    val texto by viewModel.texto.collectAsState()
    val casos by viewModel.casos.collectAsState()

    val abiertos = casos.count { it.estado == EstadoCaso.ABIERTO }
    val enInvestigacion = casos.count { it.estado == EstadoCaso.EN_INVESTIGACION }
    val cerrados = casos.count { it.estado == EstadoCaso.CERRADO }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Casos", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onCrearEntrevista) {
                        Icon(Icons.Default.Description, contentDescription = "Agregar entrevista")
                    }
                    IconButton(onClick = onVerResumen) {
                        Icon(Icons.Default.BarChart, contentDescription = "Ver resumen")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EstadisticaCard("Abiertos", abiertos, Color(0xFF2196F3), Modifier.weight(1f))
                EstadisticaCard("En curso", enInvestigacion, Color(0xFFFF9800), Modifier.weight(1f))
                EstadisticaCard("Cerrados", cerrados, Color(0xFF4CAF50), Modifier.weight(1f))
            }

            OutlinedTextField(
                value = texto,
                onValueChange = viewModel::buscar,
                label = { Text("Buscar por título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (casos.isEmpty()) {
                EstadoVacio()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(casos) { caso -> TarjetaCaso(caso, alTocar = { onVerCaso(caso.idCaso) }) }
                }
            }
        }
    }
}

@Composable
private fun EstadisticaCard(etiqueta: String, valor: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valor.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
            Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TarjetaCaso(caso: Caso, alTocar: () -> Unit) {
    val colorEstado = when (caso.estado) {
        EstadoCaso.ABIERTO -> Color(0xFF2196F3)
        EstadoCaso.EN_INVESTIGACION -> Color(0xFFFF9800)
        EstadoCaso.CERRADO -> Color(0xFF4CAF50)
    }

    Card(
        onClick = alTocar,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(colorEstado.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Folder, contentDescription = null, tint = colorEstado)
            }

            Spacer(modifier = Modifier.padding(start = 6.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(caso.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.padding(start = 4.dp))
                    Text(caso.fecha.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(colorEstado.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = caso.estado.name.replace("_", " "),
                    style = MaterialTheme.typography.labelSmall,
                    color = colorEstado,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun EstadoVacio() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "No hay casos todavía",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "Toca el botón + para crear tu primer caso",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}