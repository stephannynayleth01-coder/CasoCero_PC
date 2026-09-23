package com.udistrital.entrevistas.ui.theme.ui.caso

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.udistrital.entrevistas.data.local.EstadoCaso
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import com.udistrital.entrevistas.data.local.TipoEvidencia
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.size


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCasoScreen(
    viewModel: CasoDetalleViewModel,
    onNavigateBack: () -> Unit,
    onAgregarEntrevista: (Long) -> Unit
) {
    var expandirMenuEstado by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    val formatoFecha = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
    val fechaLegible = uiState.fecha.format(formatoFecha)

    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    val entrevistas by viewModel.entrevistas.collectAsState()

    var mostrarDialogoCierre by remember { mutableStateOf(false) }
    var textoConclusionCierre by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de la Investigación") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarDialogoEliminar = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar caso",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )

            {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = uiState.titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Fecha: $fechaLegible", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Estado: ${uiState.estado}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = uiState.descripcion, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Text(text = "Entrevistas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            if (entrevistas.isEmpty()) {
                Text(
                    text = "Aún no hay entrevistas registradas para este caso.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                entrevistas.forEach { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(item.nombrePersona, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(item.entrevista.fecha.toString(), style = MaterialTheme.typography.bodySmall)
                            Text(item.entrevista.modalidad.name, style = MaterialTheme.typography.bodySmall)
                            Text(item.entrevista.hallazgos, style = MaterialTheme.typography.bodyMedium)

                            Spacer(Modifier.height(8.dp))
                            SeccionEvidencias(idEntrevista = item.entrevista.idEntrevista, viewModel = viewModel)
                        }
                    }
                }
            }

            Button(
                onClick = { onAgregarEntrevista(uiState.id ?: return@Button) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text(" Agregar entrevista")
            }



            Text(text = "Gestión del Caso", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            ExposedDropdownMenuBox(
                expanded = expandirMenuEstado,
                onExpandedChange = { expandirMenuEstado = !expandirMenuEstado }
            ) {
                OutlinedTextField(
                    value = uiState.estado.name, // Muestra el texto del enum
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado actual") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirMenuEstado) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandirMenuEstado,
                    onDismissRequest = { expandirMenuEstado = false }
                ) {
                    // Itera sobre todas las opciones del enum EstadoCaso
                    EstadoCaso.entries.forEach { estadoOpcion ->
                        DropdownMenuItem(
                            text = { Text(estadoOpcion.name) },
                            onClick = {
                                expandirMenuEstado = false
                                if (estadoOpcion == EstadoCaso.CERRADO) {
                                    textoConclusionCierre = uiState.conclusion
                                    mostrarDialogoCierre = true
                                } else {
                                    viewModel.actualizarEstado(estadoOpcion)
                                }
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.conclusion,
                onValueChange = { viewModel.actualizarCampo(conclusion = it) },
                label = { Text("Escribe los hallazgos finales...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Button(
                onClick = {
                    viewModel.guardarCaso()
                    onNavigateBack() // Opcional: regresar a la lista tras guardar
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Guardar Cambios del Caso")
            }
        }
    }

    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("¿Eliminar caso?") },
            text = { Text("Esta acción borrará el caso y todas sus entrevistas asociadas. No se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoEliminar = false
                    viewModel.eliminarCaso()
                    onNavigateBack()
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (mostrarDialogoCierre) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCierre = false },
            title = { Text("Cerrar caso") },
            text = {
                Column {
                    Text("Para cerrar este caso debes escribir la conclusión final.")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = textoConclusionCierre,
                        onValueChange = { textoConclusionCierre = it },
                        label = { Text("Conclusión") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (textoConclusionCierre.isNotBlank()) {
                            viewModel.cerrarCaso(textoConclusionCierre)
                            mostrarDialogoCierre = false
                        }
                    }
                ) { Text("Cerrar caso") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCierre = false }) { Text("Cancelar") }
            }
        )
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeccionEvidencias(idEntrevista: String, viewModel: CasoDetalleViewModel) {
    val evidenciasFlow = remember(idEntrevista) { viewModel.observarEvidencias(idEntrevista) }
    val evidencias by evidenciasFlow.collectAsState(initial = emptyList())
    var mostrarDialogo by remember { mutableStateOf(false) }

    Column {
        if (evidencias.isNotEmpty()) {
            Text("Evidencias:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            evidencias.forEach { ev ->
                Text("• ${ev.tipo.name}: ${ev.url}", style = MaterialTheme.typography.bodySmall)
            }
        }
        TextButton(onClick = { mostrarDialogo = true }) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Text(" Agregar evidencia")
        }
    }

    if (mostrarDialogo) {
        var tipoSeleccionado by remember { mutableStateOf(TipoEvidencia.FOTO) }
        var url by remember { mutableStateOf("") }
        var menuTipoExpandido by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Nueva evidencia") },
            text = {
                Column {
                    ExposedDropdownMenuBox(
                        expanded = menuTipoExpandido,
                        onExpandedChange = { menuTipoExpandido = it }
                    ) {
                        OutlinedTextField(
                            value = tipoSeleccionado.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuTipoExpandido) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = menuTipoExpandido,
                            onDismissRequest = { menuTipoExpandido = false }
                        ) {
                            TipoEvidencia.entries.forEach { tipo ->
                                DropdownMenuItem(
                                    text = { Text(tipo.name) },
                                    onClick = {
                                        tipoSeleccionado = tipo
                                        menuTipoExpandido = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        label = { Text("URL o ruta del archivo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (url.isNotBlank()) {
                        viewModel.agregarEvidencia(idEntrevista, tipoSeleccionado, url)
                        mostrarDialogo = false
                    }
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") }
            }
        )
    }
}
