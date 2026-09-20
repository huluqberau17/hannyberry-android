package com.hannyberry.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HannyBerryApp() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loggedIn by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("HannyBerry", style = MaterialTheme.typography.headlineMedium)
        if (!loggedIn) {
            Text("Masuk ke usaha stroberi Anda")
            TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
            TextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
            Button(onClick = { loggedIn = email.isNotBlank() && password.isNotBlank() }) {
                Text("Masuk")
            }
        } else {
            Text("Mode offline-first siap")
            Text("Data transaksi akan disimpan di perangkat dan disinkronkan saat online.")
            Button(onClick = { loggedIn = false }) { Text("Keluar") }
        }
    }
}
