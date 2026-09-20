package com.hannyberry.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.hannyberry.data.remote.ApiConfig

@Composable
fun HannyBerryApp(viewModel: AuthViewModel) {
    val state by viewModel.state.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("HannyBerry", style = MaterialTheme.typography.headlineMedium)
        if (state.loggedIn) {
            SignedInScreen(onLogout = viewModel::logout)
        } else {
            LoginScreen(state, viewModel)
        }
    }
}

@Composable
private fun LoginScreen(state: AuthUiState, viewModel: AuthViewModel) {
    Text("Masuk ke usaha stroberi Anda")
    OutlinedTextField(
        value = state.email,
        onValueChange = viewModel::emailChanged,
        label = { Text("Email") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = state.password,
        onValueChange = viewModel::passwordChanged,
        label = { Text("Password") },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
    )
    state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    Button(
        onClick = viewModel::login,
        enabled = !state.loading,
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (state.loading) CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
        Text(if (state.loading) "Memproses…" else "Masuk")
    }
    Text("Server: ${ApiConfig.BASE_URL}", style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun SignedInScreen(onLogout: () -> Unit) {
    Text("Mode offline-first siap")
    Text("Data transaksi disimpan di perangkat ini dan disinkronkan ketika online.")
    OutlinedButton(onClick = onLogout) { Text("Keluar") }
}
