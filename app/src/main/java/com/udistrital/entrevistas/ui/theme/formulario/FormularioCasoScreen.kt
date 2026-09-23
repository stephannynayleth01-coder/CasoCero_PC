package com.udistrital.entrevistas.ui.theme.formulario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.udistrital.entrevistas.ui.theme.ui.caso.CasoDetalleViewModel
import androidx.compose.foundation.background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioCasoScreen(
    viewModel: CasoDetalleViewModel,
    onNavigateBack: () -> Unit // Callback para volver al listado tras guardar
) {
    // Observamos el estado reactivo desde el ViewModel
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                // El título de la barra cambia dinámicamente si estamos creando o editando
                title = { Text(if (uiState.id == null) "Nuevo Caso" else "Editar Caso") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar al listado")
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo para el Título
            OutlinedTextField(
                value = uiState.titulo,
                onValueChange = { viewModel.actualizarCampo(titulo = it) },
                label = { Text("Título de la investigación") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.error != null && uiState.titulo.isBlank()
            )

            // Campo para la Descripción
            OutlinedTextField(
                value = uiState.descripcion,
                onValueChange = { viewModel.actualizarCampo(descripcion = it) },
                label = { Text("Descripción o contexto inicial") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Se expande para tomar el espacio restante en pantalla
                isError = uiState.error != null && uiState.descripcion.isBlank()
            )

            // Mensaje de error (si el usuario intenta guardar con campos vacíos)
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Botón Guardar
            Button(
                onClick = {
                    viewModel.guardarCaso()
                    // Si pasa la validación y no hay campos en blanco, regresamos a la pantalla anterior
                    if (uiState.titulo.isNotBlank() && uiState.descripcion.isNotBlank()) {
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(if (uiState.id == null) "Crear caso" else "Guardar cambios")
            }
        }
    }
}