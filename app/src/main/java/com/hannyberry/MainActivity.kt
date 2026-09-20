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
import com.hannyberry.data.TransactionRepository
import com.hannyberry.data.local.AppDatabase
import com.hannyberry.ui.AuthViewModel
import com.hannyberry.ui.AuthViewModelFactory
import com.hannyberry.ui.HannyBerryApp
import com.hannyberry.ui.TransactionViewModel
import com.hannyberry.ui.TransactionViewModelFactory
import com.hannyberry.ui.theme.HannyBerryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokens = TokenStore(applicationContext)
        val database = AppDatabase.get(applicationContext)
        val authRepository = AuthRepository(tokens)
        val transactionRepository = TransactionRepository(database, tokens)

        setContent {
            HannyBerryTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val authViewModel: AuthViewModel = viewModel(
                        factory = AuthViewModelFactory(authRepository)
                    )
                    val transactionViewModel: TransactionViewModel = viewModel(
                        factory = TransactionViewModelFactory(transactionRepository)
                    )
                    HannyBerryApp(authViewModel, transactionViewModel)
                }
            }
        }
    }
}
