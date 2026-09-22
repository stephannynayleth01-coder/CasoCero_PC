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

@Composable
fun NotaVivaNavegacion(
    repositorio: CasoRepository
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "lista_casos") {

        composable("lista_casos") {
        }

        composable(
            route = "formulario_caso/{casoId}",
            arguments = listOf(navArgument("casoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val casoId = backStackEntry.arguments?.getLong("casoId") ?: -1L

            val viewModel: CasoDetalleViewModel = viewModel(
                factory = CasoViewModelFactory(repositorio)
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
                factory = CasoViewModelFactory(repositorio)
            )

            LaunchedEffect(casoId) {
                viewModel.cargarCaso(casoId)
            }

            DetalleCasoScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("formulario_entrevista/{casoId}") {
        }
    }
}
