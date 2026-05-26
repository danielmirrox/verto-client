package com.samsung.verto.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.samsung.verto.R
import com.samsung.verto.presentation.main.MainViewModel
import com.samsung.verto.ui.theme.PulseGradientEnd
import com.samsung.verto.ui.theme.PulseGradientStart

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val state by viewModel.uiState.collectAsState()
    val topupAmount = state.topupAmount.toLongOrNull()
    val topupValid = topupAmount != null && topupAmount > 0
    val showTopupError = state.topupAmount.isNotBlank() && !topupValid

    val transferUserId = state.transferUserId.toLongOrNull()
    val transferAmount = state.transferAmount.toLongOrNull()
    val transferValid = transferUserId != null && transferAmount != null && transferAmount > 0
    val showTransferUserError = state.transferUserId.isNotBlank() && transferUserId == null
    val showTransferAmountError = state.transferAmount.isNotBlank() && (transferAmount == null || transferAmount <= 0)
    val hasSearchQuery = state.searchQuery.trim().length >= 2
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
            .padding(16.dp)
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Text(text = stringResource(R.string.title_wallet), style = MaterialTheme.typography.titleLarge)
                state.user?.let { user ->
                    Text(text = user.email, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            item {
                AnimatedVisibility(
                    visible = showError,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = errorText.orEmpty(),
                                color = MaterialTheme.colorScheme.onError,
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(text = stringResource(R.string.label_available), style = MaterialTheme.typography.labelLarge)
                        Text(
                            text = state.balance?.let { "${it.amount} ${it.currency}" }
                                ?: stringResource(R.string.placeholder_balance),
                            style = MaterialTheme.typography.displayLarge,
                        )
                        if (state.isLoading) {
                            Text(
                                text = stringResource(R.string.status_syncing),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(text = stringResource(R.string.section_topup), style = MaterialTheme.typography.titleMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = state.topupAmount,
                                onValueChange = viewModel::updateTopupAmount,
                                label = { Text(stringResource(R.string.label_amount)) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                isError = showTopupError,
                                supportingText = {
                                    if (showTopupError) {
                                        Text(stringResource(R.string.error_amount_positive))
                                    } else {
                                        Text(stringResource(R.string.helper_minimum_one))
                                    }
                                },
                            )
                            Button(onClick = viewModel::topup, enabled = topupValid) {
                                Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.action_topup))
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(text = stringResource(R.string.section_transfer), style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = state.transferUserId,
                            onValueChange = viewModel::updateTransferUserId,
                            label = { Text(stringResource(R.string.label_recipient_id)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = showTransferUserError,
                            supportingText = {
                                if (showTransferUserError) {
                                    Text(stringResource(R.string.error_user_id_numeric))
                                } else {
                                    Text(stringResource(R.string.helper_user_id_from_search))
                                }
                            },
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = state.transferAmount,
                                onValueChange = viewModel::updateTransferAmount,
                                label = { Text(stringResource(R.string.label_amount)) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                isError = showTransferAmountError,
                                supportingText = {
                                    if (showTransferAmountError) {
                                        Text(stringResource(R.string.error_amount_positive))
                                    } else {
                                        Text(stringResource(R.string.helper_minimum_one))
                                    }
                                },
                            )
                            Button(onClick = viewModel::transfer, enabled = transferValid) {
                                Icon(imageVector = Icons.AutoMirrored.Rounded.Send, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.action_send))
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(text = stringResource(R.string.section_search_users), style = MaterialTheme.typography.titleMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = state.searchQuery,
                                onValueChange = viewModel::updateSearchQuery,
                                label = { Text(stringResource(R.string.label_query)) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                            )
                            Button(onClick = viewModel::search) {
                                Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.action_search))
                            }
                        }
                        if (state.searchQuery.isNotBlank() && state.searchQuery.trim().length < 2) {
                            Text(
                                text = stringResource(R.string.helper_search_min_chars),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        if (hasSearchQuery && state.searchResults.isEmpty()) {
                            Text(
                                text = stringResource(R.string.helper_search_empty),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        } else {
                            state.searchResults.forEach { user ->
                                Text(text = stringResource(R.string.user_row, user.id, user.email))
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(text = stringResource(R.string.section_transactions), style = MaterialTheme.typography.titleMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = state.transactionFilter == "ALL",
                                onClick = { viewModel.updateTransactionFilter("ALL") },
                                label = { Text(stringResource(R.string.filter_all)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                )
                            )
                            FilterChip(
                                selected = state.transactionFilter == "TOPUP",
                                onClick = { viewModel.updateTransactionFilter("TOPUP") },
                                label = { Text(stringResource(R.string.filter_topups)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                )
                            )
                            FilterChip(
                                selected = state.transactionFilter == "TRANSFER",
                                onClick = { viewModel.updateTransactionFilter("TRANSFER") },
                                label = { Text(stringResource(R.string.filter_transfers)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                )
                            )
                        }
                        val filtered = when (state.transactionFilter) {
                            "TOPUP" -> state.transactions.filter { it.type == "TOPUP" }
                            "TRANSFER" -> state.transactions.filter { it.type == "TRANSFER" }
                            else -> state.transactions
                        }
                        if (filtered.isEmpty()) {
                            Text(
                                text = stringResource(R.string.empty_transactions),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            filtered.forEach { tx ->
                                val typeColor = when (tx.type) {
                                    "TOPUP" -> MaterialTheme.colorScheme.primary
                                    "TRANSFER" -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                                val labelColor = when (tx.type) {
                                    "TRANSFER" -> MaterialTheme.colorScheme.onSecondary
                                    else -> MaterialTheme.colorScheme.onPrimary
                                }
                                val typeLabel = when (tx.type) {
                                    "TOPUP" -> stringResource(R.string.transaction_label_topup)
                                    "TRANSFER" -> stringResource(R.string.transaction_label_transfer)
                                    else -> tx.type
                                }
                                val fromValue = tx.fromUserId?.toString()
                                    ?: stringResource(R.string.placeholder_dash)
                                val toValue = tx.toUserId?.toString()
                                    ?: stringResource(R.string.placeholder_dash)
                                Column {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .background(typeColor, shape = MaterialTheme.shapes.small)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = typeLabel,
                                                style = MaterialTheme.typography.labelLarge,
                                                color = labelColor,
                                            )
                                        }
                                        Text(text = "${tx.amount}")
                                    }
                                    Text(
                                        text = stringResource(R.string.transaction_participants, fromValue, toValue),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Text(
                                        text = tx.createdAt,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
