package com.hannyberry.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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

/** Label pendek untuk chip; teks panjang bikin chip terpotong di layar sempit. */
private val CHIP_LABELS = mapOf(
    "INCOME" to "Masuk",
    "COST_OF_GOODS" to "Modal",
    "OPERATING_EXPENSE" to "Keluar",
)

@Composable
fun MainScreen(
    transactionViewModel: TransactionViewModel,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
) {
    var currentTab by remember { mutableStateOf(0) }
    var profileOpen by remember { mutableStateOf(false) }
    var editOpen by remember { mutableStateOf(false) }
    val authState by authViewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Header sendiri, tanpa TopAppBar eksperimental, supaya stabil di semua versi Compose.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "HannyBerry",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Box {
                TextButton(onClick = { profileOpen = true }) {
                    val initial = authState.profile?.name?.trim()?.takeIf { it.isNotEmpty() }?.first()?.uppercase() ?: "?"
                    Text("[ $initial ]  ${authState.profile?.name ?: "Profil"}")
                }
                DropdownMenu(expanded = profileOpen, onDismissRequest = { profileOpen = false }) {
                    DropdownMenuItem(
                        text = { Text(authState.profile?.name ?: "Profil") },
                        onClick = { profileOpen = false },
                    )
                    DropdownMenuItem(
                        text = { Text(authState.profile?.email ?: "-") },
                        onClick = { profileOpen = false },
                    )
                    DropdownMenuItem(
                        text = { Text(if (authState.profile?.role == "OWNER") "Administrator" else "Member") },
                        onClick = { profileOpen = false },
                    )
                    DropdownMenuItem(
                        text = { Text("Edit Profil") },
                        onClick = { profileOpen = false; editOpen = true },
                    )
                    DropdownMenuItem(
                        text = { Text("Keluar") },
                        onClick = { profileOpen = false; onLogout() },
                    )
                }
            }
        }

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
                0 -> OverviewTab(padding, transactionViewModel)
                1 -> AddTransactionTab(padding, transactionViewModel)
                else -> HistoryTab(padding, transactionViewModel)
            }
        }
    }

    if (editOpen) {
        EditProfileDialog(
            state = authState,
            onDismiss = { editOpen = false },
            onSave = { name -> authViewModel.renameProfile(name); editOpen = false },
        )
    }
}

@Composable
private fun EditProfileDialog(
    state: AuthUiState,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var name by remember(state.profile?.name) { mutableStateOf(state.profile?.name ?: "") }
    val isAdmin = state.profile?.role == "OWNER"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profil") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                InputField(
                    label = "Nama",
                    value = name,
                    onValueChange = { name = it },
                )
                Text("Email: ${state.profile?.email ?: "-"}", style = MaterialTheme.typography.bodySmall)
                Text(
                    "Role: ${if (isAdmin) "Administrator" else "Member"}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(name) }, enabled = !state.profileLoading) {
                Text(if (state.profileLoading) "Menyimpan…" else "Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        },
    )
}

@Composable
private fun ScreenColumn(padding: PaddingValues, content: @Composable () -> Unit) {
    // Layar landscape di HP rendah, jadi isi tab harus bisa digeser dan form
    // tetap terlihat saat keyboard muncul.
    Box(modifier = Modifier.padding(padding).fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            content()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OverviewTab(
    padding: PaddingValues,
    viewModel: TransactionViewModel,
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
    }
}

@Composable
private fun AddTransactionTab(padding: PaddingValues, viewModel: TransactionViewModel) {
    val form by viewModel.form.collectAsState()

    ScreenColumn(padding) {
        Text("Tambah Transaksi", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Tersimpan di perangkat dulu, lalu dikirim saat sinkron.", style = MaterialTheme.typography.bodyMedium)

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TransactionFormState.TYPES.forEach { type ->
                FilterChip(
                    selected = form.type == type,
                    onClick = { viewModel.typeChanged(type) },
                    label = { Text(CHIP_LABELS[type] ?: type) },
                )
            }
        }

        if (form.categories.isNotEmpty()) {
            Text("Kategori", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                form.categories.forEach { category ->
                    FilterChip(
                        selected = form.categoryId == category.id,
                        onClick = { viewModel.categoryChanged(category) },
                        label = { Text(category.name) },
                    )
                }
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
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    TYPE_LABELS[item.transactionType] ?: item.transactionType,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(item.transactionDate, style = MaterialTheme.typography.bodySmall)
                item.notes?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Column(
                modifier = Modifier.padding(start = 10.dp),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = formatRupiah(item.amount),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip,
                    color = if (item.transactionType == "INCOME") MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error,
                )
                if (item.dirty) {
                    Text(
                        "belum tersinkron",
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        softWrap = false,
                    )
                }
            }
        }
    }
}
