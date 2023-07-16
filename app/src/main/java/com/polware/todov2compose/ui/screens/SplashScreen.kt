package com.polware.todov2compose.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.polware.todov2compose.R
import com.polware.todov2compose.ui.theme.MEDIUM_PADDING
import com.polware.todov2compose.ui.theme.ToDoV2ComposeTheme
import com.polware.todov2compose.ui.theme.splashScreenBackground
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navigateToTaskListScreen: () -> Unit
) {
    var startAnimation by remember {
        mutableStateOf(false)
    }
    val offsetState by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 100.dp,
        animationSpec = tween(durationMillis = 1500)
    )
    val alphaState by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500)
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(3000)
        navigateToTaskListScreen()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.splashScreenBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .size(120.dp)
                .offset(y = offsetState) // Animate Logo from the bottom to the center of screen
                .alpha(alpha = alphaState),
            painter = painterResource(id = setLogo()),
            contentDescription = stringResource(id = R.string.app_logo)
        )
        Divider(
            modifier = Modifier.height(MEDIUM_PADDING),
            color = MaterialTheme.colors.splashScreenBackground
        )
        Text(
            modifier = Modifier.offset(y = offsetState), // Animate text from bottom to center of screen
            text = stringResource(id = R.string.app_name),
            color = Color.White,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun setLogo(): Int {
    return if (isSystemInDarkTheme()) {
        R.drawable.todo_icon_dark
    } else {
        R.drawable.todo_icon_light
    }
}


@Composable
@Preview
private fun SplashScreenLightPreview() {
    SplashScreen(navigateToTaskListScreen = {})
}

@Composable
@Preview
private fun SplashScreenDarkPreview() {
    ToDoV2ComposeTheme(darkTheme = true) {
        SplashScreen(navigateToTaskListScreen = {})
    }
}
