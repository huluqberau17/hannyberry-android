package com.hannyberry.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hannyberry.data.AuthRepository
import com.hannyberry.data.LoginResult
import com.hannyberry.data.TokenStore
import com.hannyberry.data.remote.ApiConfig
import com.hannyberry.data.local.CategoryDao
import com.hannyberry.data.local.TransactionDao
import com.hannyberry.ui.components.ActionButton
import com.hannyberry.ui.components.InputField
import com.hannyberry.ui.theme.HannyBerryTheme

@Composable
fun HannyBerryApp(authViewModel: AuthViewModel) {
    val state by authViewModel.state.collectAsState()
    
    if (state.loggedIn) {
        MainNavigation()
    } else {
        LoginScreen(state, authViewModel)
    }
}

@Composable
private fun LoginScreen(state: AuthUiState, viewModel: AuthViewModel) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "HannyBerry",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Kelola keuangan usaha stroberimu dengan mudah dan efisien",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            InputField(
                label = "Email",
                value = state.email,
                onValueChange = viewModel::emailChanged,
                placeholder = "masukkan@email.com",
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            InputField(
                label = "Password",
                value = state.password,
                onValueChange = viewModel::passwordChanged,
                placeholder = "********",
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            state.error?.let { errorText ->
                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            ActionButton(
                text = if (state.loading) "Memproses..." else "Masuk",
                onClick = viewModel::login,
                fullWidth = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Server: ${ApiConfig.BASE_URL}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}
