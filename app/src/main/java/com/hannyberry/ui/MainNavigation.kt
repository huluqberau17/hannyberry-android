package com.hannyberry.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hannyberry.ui.components.ActionButton
import com.hannyberry.ui.theme.HannyBerryTheme

@Composable
fun HannyBerryApp() {
    var currentTab by remember { mutableStateOf(0) }
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = com.hannyberry.ui.theme.Cream
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text("Beranda") },
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Tambah Transaksi") },
                    label = { Text("Transaksi") },
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.PieChart, contentDescription = "Laporan") },
                    label = { Text("Laporan") },
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 }
                )
            }
        }
    ) { paddingValues ->
        when (currentTab) {
            0 -> HomeScreen(paddingValues)
            1 -> TransactionScreen(paddingValues)
            2 -> ReportScreen(paddingValues)
        }
    }
}

@Composable
fun HomeScreen(paddingValues: PaddingValues) {
    Box(modifier = Modifier.padding(paddingValues)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "HannyBerry",
                style = com.hannyberry.ui.theme.Typography.headlineLarge,
                color = com.hannyberry.ui.theme.Ink
            )
            
            Text(
                text = "Kelola keuangan usaha stroberimu dengan mudah",
                style = com.hannyberry.ui.theme.Typography.bodyMedium,
                color = com.hannyberry.ui.theme.Ink.copy(alpha = 0.7f)
            )
            
            ActionButton(
                text = "+ Tambah Transaksi Baru",
                onClick = {},
                fullWidth = true
            )
        }
    }
}

@Composable
fun TransactionScreen(paddingValues: PaddingValues) {
    Box(modifier = Modifier.padding(paddingValues)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Riwayat Transaksi",
                style = com.hannyberry.ui.theme.Typography.headlineMedium
            )
            
            Text(
                text = "Belum ada transaksi.",
                style = com.hannyberry.ui.theme.Typography.bodyLarge
            )
        }
    }
}

@Composable
fun ReportScreen(paddingValues: PaddingValues) {
    Box(modifier = Modifier.padding(paddingValues)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Laporan Keuangan",
                style = com.hannyberry.ui.theme.Typography.headlineMedium
            )
            
            Text(
                text = "Data laporan akan tersedia setelah ada transaksi.",
                style = com.hannyberry.ui.theme.Typography.bodyLarge
            )
        }
    }
}
