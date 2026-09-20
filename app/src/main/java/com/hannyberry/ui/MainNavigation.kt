package com.hannyberry.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hannyberry.ui.components.ActionButton
import com.hannyberry.ui.components.StatCard

@Composable
fun MainScreen(onLogout: () -> Unit) {
    var currentTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Beranda") },
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    label = { Text("Transaksi") },
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Laporan") },
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                )
            }
        }
    ) { padding ->
        when (currentTab) {
            0 -> HomeTab(padding, onLogout)
            1 -> TransactionsTab(padding)
            else -> ReportsTab(padding)
        }
    }
}

@Composable
private fun ScreenColumn(padding: PaddingValues, content: @Composable () -> Unit) {
    Box(modifier = Modifier.padding(padding)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun HomeTab(padding: PaddingValues, onLogout: () -> Unit) {
    ScreenColumn(padding) {
        Text("HannyBerry", style = MaterialTheme.typography.headlineMedium)
        Text("Ringkasan usaha stroberi", style = MaterialTheme.typography.bodyMedium)

        StatCard(title = "Laba Bersih", value = "Rp 0", subtitle = "Belum ada transaksi")
        StatCard(title = "Pendapatan", value = "Rp 0", subtitle = "Bulan ini")
        StatCard(title = "Pengeluaran", value = "Rp 0", subtitle = "Bulan ini", isPositive = false)

        ActionButton(text = "+ Tambah Transaksi", onClick = {}, fullWidth = true)
        ActionButton(text = "Keluar", onClick = onLogout, fullWidth = true)
    }
}

@Composable
private fun TransactionsTab(padding: PaddingValues) {
    ScreenColumn(padding) {
        Text("Riwayat Transaksi", style = MaterialTheme.typography.headlineMedium)
        Text("Belum ada transaksi tersimpan.", style = MaterialTheme.typography.bodyMedium)
        ActionButton(text = "+ Tambah Transaksi", onClick = {}, fullWidth = true)
    }
}

@Composable
private fun ReportsTab(padding: PaddingValues) {
    ScreenColumn(padding) {
        Text("Laporan", style = MaterialTheme.typography.headlineMedium)
        Text("Laporan akan tersedia setelah ada transaksi.", style = MaterialTheme.typography.bodyMedium)
    }
}
