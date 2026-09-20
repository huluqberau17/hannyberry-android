package com.hannyberry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hannyberry.data.AuthRepository
import com.hannyberry.data.TokenStore
import com.hannyberry.ui.AuthViewModel
import com.hannyberry.ui.AuthViewModelFactory
import com.hannyberry.ui.HannyBerryApp
import com.hannyberry.ui.theme.HannyBerryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = AuthRepository(TokenStore(applicationContext))
        setContent {
            HannyBerryTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val authViewModel: AuthViewModel = viewModel(
                        factory = AuthViewModelFactory(repository)
                    )
                    HannyBerryApp(authViewModel)
                }
            }
        }
    }
}
