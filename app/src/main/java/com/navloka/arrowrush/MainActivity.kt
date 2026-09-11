package com.navloka.arrowrush

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.navloka.arrowrush.model.GameMode
import com.navloka.arrowrush.ui.screens.GameScreen
import com.navloka.arrowrush.ui.screens.LevelSelectScreen
import com.navloka.arrowrush.ui.screens.MainMenuScreen
import com.navloka.arrowrush.ui.theme.ArrowRushTheme
import com.navloka.arrowrush.viewmodel.PuzzleGameViewModel
import com.navloka.arrowrush.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: PuzzleGameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val gameState by viewModel.gameState.collectAsState()

            ArrowRushTheme(themeMode = themeMode) {
                // Back press handling
                BackHandler(enabled = currentScreen != Screen.MAIN_MENU) {
                    when (currentScreen) {
                        Screen.LEVEL_SELECT -> viewModel.navigateTo(Screen.MAIN_MENU)
                        Screen.GAMEPLAY -> {
                            if (gameState.mode == GameMode.RUSH) {
                                viewModel.navigateTo(Screen.MAIN_MENU)
                            } else {
                                viewModel.navigateTo(Screen.LEVEL_SELECT)
                            }
                        }
                        Screen.MAIN_MENU -> Unit
                    }
                }

                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    modifier = Modifier.fillMaxSize(),
                    label = "screenTransition"
                ) { screen ->
                    when (screen) {
                        Screen.MAIN_MENU -> MainMenuScreen(viewModel = viewModel)
                        Screen.LEVEL_SELECT -> LevelSelectScreen(viewModel = viewModel)
                        Screen.GAMEPLAY -> GameScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
