package com.hannyberry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.hannyberry.data.AuthRepository
import com.hannyberry.data.TokenStore
import com.hannyberry.ui.AuthViewModel
import com.hannyberry.ui.AuthViewModelFactory
import com.hannyberry.ui.HannyBerryApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = AuthRepository(TokenStore(applicationContext))
        setContent {
            MaterialTheme {
                Surface {
                    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(repository))
                    HannyBerryApp(authViewModel)
                }
            }
        }
    }
}
