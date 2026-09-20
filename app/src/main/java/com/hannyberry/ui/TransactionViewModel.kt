package com.hannyberry.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hannyberry.data.SyncOutcome
import com.hannyberry.data.TransactionRepository
import com.hannyberry.data.local.TransactionEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class TransactionFormState(
    val amountText: String = "",
    val type: String = TYPES.first(),
    val dateText: String = LocalDate.now().toString(),
    val notes: String = "",
    val saving: Boolean = false,
    val error: String? = null,
) {
    companion object {
        val TYPES = listOf("INCOME", "COST_OF_GOODS", "OPERATING_EXPENSE")
    }
}

val TYPE_LABELS = mapOf(
    "INCOME" to "Pendapatan",
    "COST_OF_GOODS" to "Modal / Belanja",
    "OPERATING_EXPENSE" to "Pengeluaran",
)

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {
    val transactions: StateFlow<List<TransactionEntity>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _form = MutableStateFlow(TransactionFormState())
    val form: StateFlow<TransactionFormState> = _form.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    private val _syncing = MutableStateFlow(false)
    val syncing: StateFlow<Boolean> = _syncing.asStateFlow()

    fun amountChanged(value: String) = update { copy(amountText = value.filter(Char::isDigit), error = null) }
    fun typeChanged(value: String) = update { copy(type = value, error = null) }
    fun dateChanged(value: String) = update { copy(dateText = value, error = null) }
    fun notesChanged(value: String) = update { copy(notes = value, error = null) }

    private fun update(block: TransactionFormState.() -> TransactionFormState) {
        _form.value = _form.value.block()
    }

    fun save() {
        val current = _form.value
        if (current.saving) return
        val amount = current.amountText.toLongOrNull()
        if (amount == null || amount <= 0) {
            update { copy(error = "Jumlah harus angka lebih dari nol.") }
            return
        }
        val date = runCatching { LocalDate.parse(current.dateText) }.getOrNull()
        if (date == null) {
            update { copy(error = "Tanggal harus format YYYY-MM-DD.") }
            return
        }
        update { copy(saving = true, error = null) }
        viewModelScope.launch {
            repository.add(
                amount = amount,
                transactionType = current.type,
                transactionDate = date,
                notes = current.notes,
            )
            _form.value = TransactionFormState()
            _syncMessage.value = "Transaksi disimpan di perangkat."
        }
    }

    fun sync() {
        if (_syncing.value) return
        _syncing.value = true
        viewModelScope.launch {
            val outcome: SyncOutcome = repository.sync()
            _syncMessage.value = outcome.message
            _syncing.value = false
        }
    }

    fun clearMessage() {
        _syncMessage.value = null
    }
}

class TransactionViewModelFactory(
    private val repository: TransactionRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(TransactionViewModel::class.java))
        return TransactionViewModel(repository) as T
    }
}
