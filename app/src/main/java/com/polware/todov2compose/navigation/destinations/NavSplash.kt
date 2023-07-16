package com.polware.todov2compose.navigation.destinations

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.polware.todov2compose.ui.screens.SplashScreen
import com.polware.todov2compose.utils.Constants.SPLASH_SCREEN

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.splashComposable(
    navigateToTaskListScreen: () -> Unit
) {
    composable(
        route = SPLASH_SCREEN,
        exitTransition = {
            slideOutVertically(
                targetOffsetY = { fullHeight -> -fullHeight }, // Animate screen from bottom to top (positive value -> top-bottom)
                animationSpec = tween(400)
            )
        }
    ) {
        SplashScreen(navigateToTaskListScreen = navigateToTaskListScreen)
    }

}
