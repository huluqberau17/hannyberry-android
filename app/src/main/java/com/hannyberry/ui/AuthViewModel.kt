package com.hannyberry.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hannyberry.data.AuthRepository
import com.hannyberry.data.LoginResult
import com.hannyberry.data.ProfileResult
import com.hannyberry.data.SignupResult
import com.hannyberry.data.remote.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val loading: Boolean = false,
    val loggedIn: Boolean = false,
    val showSignup: Boolean = false,
    val profile: UserDto? = null,
    val profileLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(AuthUiState(loggedIn = repository.hasSession()))
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        if (_state.value.loggedIn) loadProfile()
    }

    fun emailChanged(value: String) { _state.value = _state.value.copy(email = value, error = null) }
    fun passwordChanged(value: String) { _state.value = _state.value.copy(password = value, error = null) }
    fun nameChanged(value: String) { _state.value = _state.value.copy(name = value, error = null) }
    fun showSignup() { _state.value = _state.value.copy(showSignup = true, error = null, message = null) }
    fun showLogin() { _state.value = _state.value.copy(showSignup = false, error = null, message = null) }

    fun login() {
        val current = _state.value
        if (current.loading) return
        _state.value = current.copy(loading = true, error = null)
        viewModelScope.launch {
            when (val result = repository.login(current.email, current.password)) {
                LoginResult.Success -> {
                    _state.value = _state.value.copy(loading = false, loggedIn = true, password = "")
                    loadProfile()
                }
                is LoginResult.Failure -> _state.value = _state.value.copy(loading = false, error = result.message)
            }
        }
    }

    fun signup() {
        val current = _state.value
        if (current.loading) return
        _state.value = current.copy(loading = true, error = null)
        viewModelScope.launch {
            when (val result = repository.signup(current.name, current.email, current.password)) {
                SignupResult.Success -> _state.value = _state.value.copy(loading = false, showSignup = false, password = "", message = "Pendaftaran berhasil. Silakan login.")
                is SignupResult.Failure -> _state.value = _state.value.copy(loading = false, error = result.message)
            }
        }
    }

    fun loadProfile() {
        if (_state.value.profileLoading) return
        _state.value = _state.value.copy(profileLoading = true)
        viewModelScope.launch {
            when (val result = repository.profile()) {
                is ProfileResult.Success -> _state.value = _state.value.copy(profile = result.profile, profileLoading = false)
                is ProfileResult.Failure -> _state.value = _state.value.copy(profileLoading = false, error = result.message)
            }
        }
    }

    fun renameProfile(name: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(profileLoading = true, error = null)
            when (val result = repository.rename(name)) {
                is ProfileResult.Success -> _state.value = _state.value.copy(profile = result.profile, profileLoading = false, message = "Profil berhasil diperbarui.")
                is ProfileResult.Failure -> _state.value = _state.value.copy(profileLoading = false, error = result.message)
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
