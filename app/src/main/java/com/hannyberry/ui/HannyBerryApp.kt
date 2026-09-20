package com.hannyberry.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hannyberry.data.remote.ApiConfig
import com.hannyberry.ui.components.ActionButton
import com.hannyberry.ui.components.InputField

@Composable
fun HannyBerryApp(
    authViewModel: AuthViewModel,
    transactionViewModel: TransactionViewModel,
) {
    val state by authViewModel.state.collectAsState()

    if (state.loggedIn) {
        MainScreen(
            transactionViewModel = transactionViewModel,
            onLogout = authViewModel::logout,
        )
    } else {
        LoginScreen(state, authViewModel)
    }
}

@Composable
private fun LoginScreen(state: AuthUiState, viewModel: AuthViewModel) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "HannyBerry",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = "Kelola keuangan usaha stroberimu dengan mudah",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
            )

            InputField(
                label = "Email",
                value = state.email,
                onValueChange = viewModel::emailChanged,
                placeholder = "nama@email.com",
            )

            InputField(
                label = "Password",
                value = state.password,
                onValueChange = viewModel::passwordChanged,
                placeholder = "••••••••",
                isPassword = true,
            )

            state.error?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            ActionButton(
                text = if (state.loading) "Memproses…" else "Masuk",
                onClick = viewModel::login,
                fullWidth = true,
                enabled = !state.loading,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Server: ${ApiConfig.BASE_URL}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
        }
    }
}
