package com.udistrital.entrevistas.ui.entrevista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.udistrital.entrevistas.data.local.Modalidad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioEntrevistaScreen(
    viewModel: FormularioEntrevistaViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val casos by viewModel.casosDisponibles.collectAsState()
    var menuCasoExpandido by remember { mutableStateOf(false) }
    var menuModalidadExpandido by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva entrevista") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Selector de caso (lo que pediste) ---
            ExposedDropdownMenuBox(
                expanded = menuCasoExpandido,
                onExpandedChange = { menuCasoExpandido = it }
            ) {
                OutlinedTextField(
                    value = uiState.casoSeleccionado?.titulo ?: "Selecciona un caso",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Caso") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuCasoExpandido) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    isError = uiState.error != null && uiState.casoSeleccionado == null
                )
                ExposedDropdownMenu(
                    expanded = menuCasoExpandido,
                    onDismissRequest = { menuCasoExpandido = false }
                ) {
                    if (casos.isEmpty()) {
                        DropdownMenuItem(text = { Text("No hay casos creados aún") }, onClick = {})
                    }
                    casos.forEach { caso ->
                        DropdownMenuItem(
                            text = { Text(caso.titulo) },
                            onClick = {
                                viewModel.seleccionarCaso(caso)
                                menuCasoExpandido = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.nombrePersona,
                onValueChange = { viewModel.actualizarCampo(nombrePersona = it) },
                label = { Text("Persona entrevistada") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = menuModalidadExpandido,
                onExpandedChange = { menuModalidadExpandido = it }
            ) {
                OutlinedTextField(
                    value = uiState.modalidad.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Modalidad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuModalidadExpandido) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = menuModalidadExpandido,
                    onDismissRequest = { menuModalidadExpandido = false }
                ) {
                    Modalidad.entries.forEach { modalidad ->
                        DropdownMenuItem(
                            text = { Text(modalidad.name) },
                            onClick = {
                                viewModel.actualizarCampo(modalidad = modalidad)
                                menuModalidadExpandido = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.hallazgos,
                onValueChange = { viewModel.actualizarCampo(hallazgos = it) },
                label = { Text("Hallazgos") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    viewModel.guardarEntrevista()
                    if (uiState.casoSeleccionado != null &&
                        uiState.hallazgos.isNotBlank() &&
                        uiState.nombrePersona.isNotBlank()
                    ) {
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Guardar entrevista")
            }
        }
    }
}
