package com.vn.note.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vn.note.ext.composableWithAnimation
import com.vn.note.ui.screen.DetailScreen
import com.vn.note.ui.screen.NotesScreen
import com.vn.note.utils.ArgumentNav
import com.vn.note.utils.RouteNav
import com.vn.note.viewmodel.NoteViewModel

@Composable
fun NoteNav(viewModel: NoteViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = RouteNav.NOTES) {
        composable(RouteNav.NOTES) { NotesScreen(navController, viewModel) }
        composableWithAnimation(
            RouteNav.DETAIL_NOTE,
            arguments = listOf(navArgument(ArgumentNav.NOTE_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt(ArgumentNav.NOTE_ID)
            DetailScreen(navController, viewModel, noteId)
        }
        composableWithAnimation(RouteNav.CREATE_NOTE) { DetailScreen(navController, viewModel, null) }
    }
}
