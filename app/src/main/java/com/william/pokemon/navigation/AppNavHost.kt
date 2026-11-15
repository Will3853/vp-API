package com.william.pokemon.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.william.pokemon.ui.screen.CardDetailScreen
import com.william.pokemon.ui.screen.CardScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = CardRoute) {
        composable<CardRoute> {
            CardScreen(
                onCardClick = { cardId -> navController.navigate(CardDetailRoute(cardId)) },
            )
        }
        composable<CardDetailRoute> {
            CardDetailScreen(
                onDone = { navController.popBackStack() },
            )
        }
    }
}