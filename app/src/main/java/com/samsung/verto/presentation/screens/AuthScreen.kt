package com.samsung.verto.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.samsung.verto.R
import com.samsung.verto.presentation.auth.AuthViewModel
import com.samsung.verto.ui.theme.PulseGradientEnd
import com.samsung.verto.ui.theme.PulseGradientStart

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val state by viewModel.uiState.collectAsState()
    val emailValid = state.email.contains("@") && state.email.contains(".")
    val tokenValid = state.firebaseToken.isNotBlank()
    val showEmailError = state.email.isNotBlank() && !emailValid
    val showTokenError = state.firebaseToken.isNotBlank() && !tokenValid
    val canSubmit = emailValid && tokenValid && !state.isLoading
    val errorText = state.error ?: state.errorResId?.let { stringResource(it) }
    val showError = errorText != null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PulseGradientStart, PulseGradientEnd)
                )
            )
            .padding(20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountBalanceWallet,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Column {
                    Text(text = stringResource(R.string.app_title), style = MaterialTheme.typography.displayLarge)
                    Text(
                        text = stringResource(R.string.auth_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(text = stringResource(R.string.auth_title), style = MaterialTheme.typography.titleLarge)

                    OutlinedTextField(
                        value = state.email,
                        onValueChange = viewModel::updateEmail,
                        label = { Text(stringResource(R.string.label_email)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = showEmailError,
                        supportingText = {
                            if (showEmailError) {
                                Text(stringResource(R.string.error_email_invalid))
                            } else {
                                Text(stringResource(R.string.helper_email_demo))
                            }
                        },
                    )
                    OutlinedTextField(
                        value = state.displayName,
                        onValueChange = viewModel::updateDisplayName,
                        label = { Text(stringResource(R.string.label_display_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        supportingText = { Text(stringResource(R.string.helper_optional)) },
                    )
                    OutlinedTextField(
                        value = state.firebaseToken,
                        onValueChange = viewModel::updateToken,
                        label = { Text(stringResource(R.string.label_firebase_token)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = showTokenError,
                        supportingText = {
                            if (showTokenError) {
                                Text(stringResource(R.string.error_token_required))
                            } else {
                                Text(stringResource(R.string.helper_token_demo))
                            }
                        },
                    )

                    Button(
                        onClick = viewModel::login,
                        enabled = canSubmit,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            if (state.isLoading) {
                                stringResource(R.string.action_sign_in_loading)
                            } else {
                                stringResource(R.string.action_sign_in)
                            }
                        )
                    }

                    AnimatedVisibility(
                        visible = showError,
                        enter = fadeIn() + slideInVertically { it / 4 },
                        exit = fadeOut() + slideOutVertically { it / 4 },
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = errorText.orEmpty(),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }
        }
    }
}
