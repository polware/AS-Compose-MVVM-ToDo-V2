package com.polware.todov2compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.polware.todov2compose.navigation.AppNavigation
import com.polware.todov2compose.ui.ToDoViewModel
import com.polware.todov2compose.ui.theme.ToDoV2ComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navHostController: NavHostController
    private val toDoViewModel: ToDoViewModel by viewModels()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoV2ComposeTheme {
                navHostController = rememberAnimatedNavController()
                AppNavigation(
                    navHostController = navHostController,
                    toDoViewModel = toDoViewModel
                )
            }
        }
    }
}
