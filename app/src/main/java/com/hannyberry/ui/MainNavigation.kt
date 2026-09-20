package com.hannyberry.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hannyberry.data.local.TransactionEntity
import com.hannyberry.ui.components.ActionButton
import com.hannyberry.ui.components.InputField
import com.hannyberry.ui.components.StatCard
import java.text.NumberFormat
import java.util.Locale

private val rupiahFormat: NumberFormat =
    NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }

fun formatRupiah(value: Long): String = rupiahFormat.format(value)

@Composable
fun MainScreen(
    transactionViewModel: TransactionViewModel,
    onLogout: () -> Unit,
) {
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
                    label = { Text("Tambah") },
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Riwayat") },
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                )
            }
        }
    ) { padding ->
        when (currentTab) {
            0 -> OverviewTab(padding, transactionViewModel, onLogout)
            1 -> AddTransactionTab(padding, transactionViewModel)
            else -> HistoryTab(padding, transactionViewModel)
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
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun OverviewTab(
    padding: PaddingValues,
    viewModel: TransactionViewModel,
    onLogout: () -> Unit,
) {
    val transactions by viewModel.transactions.collectAsState()
    val syncing by viewModel.syncing.collectAsState()
    val message by viewModel.syncMessage.collectAsState()

    val income = transactions.filter { it.transactionType == "INCOME" }.sumOf { it.amount }
    val goods = transactions.filter { it.transactionType == "COST_OF_GOODS" }.sumOf { it.amount }
    val expense = transactions.filter { it.transactionType == "OPERATING_EXPENSE" }.sumOf { it.amount }
    val net = income - goods - expense

    ScreenColumn(padding) {
        Text("HannyBerry", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Ringkasan usaha stroberi", style = MaterialTheme.typography.bodyMedium)

        StatCard(
            title = "Laba Bersih",
            value = formatRupiah(net),
            subtitle = if (transactions.isEmpty()) "Belum ada transaksi" else "${transactions.size} transaksi",
            isPositive = net >= 0,
        )
        StatCard(title = "Pendapatan", value = formatRupiah(income))
        StatCard(title = "Modal / Belanja", value = formatRupiah(goods), isPositive = false)
        StatCard(title = "Pengeluaran", value = formatRupiah(expense), isPositive = false)

        ActionButton(
            text = if (syncing) "Menyinkronkan…" else "Sinkronkan ke server",
            onClick = viewModel::sync,
            fullWidth = true,
            enabled = !syncing,
        )
        message?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
        ActionButton(text = "Keluar", onClick = onLogout, fullWidth = true)
    }
}

@Composable
private fun AddTransactionTab(padding: PaddingValues, viewModel: TransactionViewModel) {
    val form by viewModel.form.collectAsState()

    ScreenColumn(padding) {
        Text("Tambah Transaksi", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Tersimpan di perangkat dulu, lalu dikirim saat sinkron.", style = MaterialTheme.typography.bodyMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TransactionFormState.TYPES.forEach { type ->
                FilterChip(
                    selected = form.type == type,
                    onClick = { viewModel.typeChanged(type) },
                    label = { Text(TYPE_LABELS[type] ?: type) },
                )
            }
        }

        InputField(
            label = "Jumlah (Rp)",
            value = form.amountText,
            onValueChange = viewModel::amountChanged,
            placeholder = "50000",
        )
        InputField(
            label = "Tanggal",
            value = form.dateText,
            onValueChange = viewModel::dateChanged,
            placeholder = "2026-09-20",
        )
        InputField(
            label = "Catatan (opsional)",
            value = form.notes,
            onValueChange = viewModel::notesChanged,
            placeholder = "Penjualan pasar pagi",
        )

        form.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        ActionButton(
            text = if (form.saving) "Menyimpan…" else "Simpan Transaksi",
            onClick = viewModel::save,
            fullWidth = true,
            enabled = !form.saving,
        )
    }
}

@Composable
private fun HistoryTab(padding: PaddingValues, viewModel: TransactionViewModel) {
    val transactions by viewModel.transactions.collectAsState()

    Box(modifier = Modifier.padding(padding)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            item {
                Column {
                    Text(
                        "Riwayat Transaksi",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "${transactions.size} transaksi tersimpan di perangkat",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
            if (transactions.isEmpty()) {
                item {
                    Text(
                        "Belum ada transaksi. Tambahkan lewat tab Tambah.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            items(transactions, key = { it.id }) { item -> TransactionRow(item) }
        }
    }
}

@Composable
private fun TransactionRow(item: TransactionEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    TYPE_LABELS[item.transactionType] ?: item.transactionType,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(item.transactionDate, style = MaterialTheme.typography.bodySmall)
                item.notes?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatRupiah(item.amount),
                    fontWeight = FontWeight.Bold,
                    color = if (item.transactionType == "INCOME") MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error,
                )
                if (item.dirty) {
                    Text("belum tersinkron", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
