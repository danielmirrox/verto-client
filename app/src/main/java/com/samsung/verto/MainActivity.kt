package com.samsung.verto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import com.samsung.verto.data.local.PreferencesRecipientHistory
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.samsung.verto.presentation.AppViewModelFactory
import com.samsung.verto.presentation.auth.AuthViewModel
import com.samsung.verto.presentation.main.MainViewModel
import com.samsung.verto.presentation.screens.AuthScreen
import com.samsung.verto.presentation.screens.DashboardScreen
import com.samsung.verto.ui.theme.VertoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkTheme by rememberSaveable { mutableStateOf(false) }
            SideEffect {
                WindowInsetsControllerCompat(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
            VertoTheme(darkTheme = darkTheme) {
                val container = remember { AppContainer() }
                val factory = remember { AppViewModelFactory(container.useCases, container.session, PreferencesRecipientHistory(applicationContext)) }
                val authViewModel: AuthViewModel = viewModel(factory = factory)
                val mainViewModel: MainViewModel = viewModel(factory = factory)
                val authState by authViewModel.uiState.collectAsState()

                val isAuthenticated = authState.isAuthenticated
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AnimatedContent(
                        targetState = isAuthenticated,
                        transitionSpec = {
                            fadeIn() + slideInVertically { it / 8 } togetherWith
                                fadeOut() + slideOutVertically { it / 8 }
                        },
                        label = "auth-switch",
                        modifier = Modifier.padding(innerPadding),
                    ) { authed ->
                        if (authed) {
                            LaunchedEffect(Unit) {
                                mainViewModel.loadInitial()
                            }
                            DashboardScreen(mainViewModel, darkTheme, { darkTheme = it }, { mainViewModel.reset(); authViewModel.logout() })
                        } else {
                            AuthScreen(authViewModel)
                        }
                    }
                }
            }
        }
    }
}
