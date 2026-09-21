package com.hannyberry.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hannyberry.R
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
            authViewModel = authViewModel,
            onLogout = authViewModel::logout,
        )
    } else {
        AuthScreen(state, authViewModel)
    }
}

@Composable
private fun AuthScreen(state: AuthUiState, viewModel: AuthViewModel) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.hannyberry_logo),
                contentDescription = "Logo HannyBerry",
                modifier = Modifier.size(120.dp),
            )

            Text(
                text = if (state.showSignup) "Buat akun baru" else "Masuk ke akun Anda",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )

            if (!state.showSignup) {
                Text(
                    text = "Kelola keuangan usaha stroberimu dengan mudah",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                )
            }

            if (state.showSignup) {
                InputField(
                    label = "Nama",
                    value = state.name,
                    onValueChange = viewModel::nameChanged,
                    placeholder = "Nama Anda",
                )
            }

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
                placeholder = if (state.showSignup) "Minimal 10 karakter" else "••••••••",
                isPassword = true,
            )

            state.error?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                )
            }

            state.message?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                )
            }

            ActionButton(
                text = when {
                    state.loading -> "Memproses…"
                    state.showSignup -> "Daftar"
                    else -> "Masuk"
                },
                onClick = { if (state.showSignup) viewModel.signup() else viewModel.login() },
                fullWidth = true,
                enabled = !state.loading,
            )

            TextButton(onClick = { if (state.showSignup) viewModel.showLogin() else viewModel.showSignup() }) {
                Text(if (state.showSignup) "Sudah punya akun? Masuk" else "Belum punya akun? Daftar")
            }
        }
    }
}
