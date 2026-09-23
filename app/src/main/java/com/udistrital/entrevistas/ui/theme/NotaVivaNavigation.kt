package com.udistrital.entrevistas.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.udistrital.entrevistas.data.repository.CasoRepository
import com.udistrital.entrevistas.ui.theme.ui.caso.DetalleCasoScreen
import com.udistrital.entrevistas.ui.theme.formulario.FormularioCasoScreen
import com.udistrital.entrevistas.ui.theme.ui.caso.CasoDetalleViewModel
import com.udistrital.entrevistas.ui.theme.ui.caso.CasoDetalleViewModel.CasoViewModelFactory
import com.udistrital.entrevistas.ui.lista.ListaScreen
import com.udistrital.entrevistas.ui.lista.ListaViewModel
import com.udistrital.entrevistas.ui.resumen.ResumenScreen
import com.udistrital.entrevistas.ui.resumen.ResumenViewModel
import com.udistrital.entrevistas.data.repository.EntrevistaRepository
import com.udistrital.entrevistas.data.repository.EvidenciaRepository
import com.udistrital.entrevistas.ui.entrevista.FormularioEntrevistaScreen
import com.udistrital.entrevistas.ui.entrevista.FormularioEntrevistaViewModel



@Composable
fun NotaVivaNavegacion(
    repositorio: CasoRepository,
    entrevistaRepositorio: EntrevistaRepository,
    evidenciaRepositorio: EvidenciaRepository
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "lista_casos") {

        composable("lista_casos") {
            val viewModel: ListaViewModel = viewModel(
                factory = ListaViewModel.ListaViewModelFactory(repositorio)
            )
            ListaScreen(
                viewModel = viewModel,
                onVerCaso = { id -> navController.navigate("detalle_caso/$id") },
                onCrearCaso = { navController.navigate("formulario_caso/-1") },
                onVerResumen = { navController.navigate("resumen") },
                onCrearEntrevista = { navController.navigate("formulario_entrevista/-1") }
            )
        }

        composable("resumen") {
            val viewModel: ResumenViewModel = viewModel(
                factory = ResumenViewModel.ResumenViewModelFactory(repositorio)
            )
            ResumenScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "formulario_caso/{casoId}",
            arguments = listOf(navArgument("casoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val casoId = backStackEntry.arguments?.getLong("casoId") ?: -1L

            val viewModel: CasoDetalleViewModel = viewModel(
                factory = CasoViewModelFactory(repositorio, entrevistaRepositorio,  evidenciaRepositorio)
            )

            LaunchedEffect(casoId) {
                if (casoId != -1L) {
                    viewModel.cargarCaso(casoId)
                }
            }

            FormularioCasoScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "detalle_caso/{casoId}",
            arguments = listOf(navArgument("casoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val casoId = backStackEntry.arguments?.getLong("casoId") ?: return@composable

            val viewModel: CasoDetalleViewModel = viewModel(
                factory = CasoViewModelFactory(repositorio, entrevistaRepositorio, evidenciaRepositorio)
            )

            LaunchedEffect(casoId) {
                viewModel.cargarCaso(casoId)
            }

            DetalleCasoScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onAgregarEntrevista = { id -> navController.navigate("formulario_entrevista/$id") }
            )
        }

        composable(
            route = "formulario_entrevista/{casoId}",
            arguments = listOf(navArgument("casoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val casoId = backStackEntry.arguments?.getLong("casoId") ?: -1L

            val viewModel: FormularioEntrevistaViewModel = viewModel(
                factory = FormularioEntrevistaViewModel.Factory(entrevistaRepositorio)
            )

            LaunchedEffect(casoId) {
                if (casoId != -1L) {
                    viewModel.casosDisponibles.collect { lista ->
                        if (lista.isNotEmpty()) {
                            viewModel.precargarCaso(casoId)
                            return@collect
                        }
                    }
                }
            }

            FormularioEntrevistaScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        }
    }
