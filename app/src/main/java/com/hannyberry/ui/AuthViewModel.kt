package com.hannyberry.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hannyberry.data.AuthRepository
import com.hannyberry.data.LoginResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val loggedIn: Boolean = false,
    val error: String? = null,
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(AuthUiState(loggedIn = repository.hasSession()))
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun emailChanged(value: String) { _state.value = _state.value.copy(email = value, error = null) }
    fun passwordChanged(value: String) { _state.value = _state.value.copy(password = value, error = null) }

    fun login() {
        val current = _state.value
        if (current.loading) return
        _state.value = current.copy(loading = true, error = null)
        viewModelScope.launch {
            when (val result = repository.login(current.email, current.password)) {
                LoginResult.Success -> _state.value = _state.value.copy(loading = false, loggedIn = true, password = "")
                is LoginResult.Failure -> _state.value = _state.value.copy(loading = false, error = result.message)
            }
        }
    }

    fun logout() {
        repository.logout()
        _state.value = AuthUiState()
    }
}

class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(AuthViewModel::class.java))
        return AuthViewModel(repository) as T
    }
}
