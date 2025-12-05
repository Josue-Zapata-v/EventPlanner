package com.tecsup.eventplanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.eventplanner.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)

            val result = repository.signIn(email, password)
            result.fold(
                onSuccess = {
                    _uiState.value = AuthUiState(isSuccess = true)
                },
                onFailure = { exception ->
                    _uiState.value = AuthUiState(
                        errorMessage = getErrorMessage(exception)
                    )
                }
            )
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)

            val result = repository.signUp(email, password)
            result.fold(
                onSuccess = {
                    _uiState.value = AuthUiState(isSuccess = true)
                },
                onFailure = { exception ->
                    _uiState.value = AuthUiState(
                        errorMessage = getErrorMessage(exception)
                    )
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }

    fun isUserLoggedIn(): Boolean {
        return repository.isUserLoggedIn()
    }

    fun signOut() {
        repository.signOut()
    }

    private fun getErrorMessage(exception: Throwable): String {
        return when {
            exception.message?.contains("no user record") == true ->
                "No existe una cuenta con este correo"
            exception.message?.contains("wrong-password") == true ||
                    exception.message?.contains("invalid-credential") == true ->
                "Contraseña incorrecta"
            exception.message?.contains("email-already-in-use") == true ->
                "Este correo ya está registrado"
            exception.message?.contains("weak-password") == true ->
                "La contraseña debe tener al menos 6 caracteres"
            exception.message?.contains("invalid-email") == true ->
                "Correo electrónico inválido"
            exception.message?.contains("network") == true ->
                "Error de conexión. Verifica tu internet"
            else -> "Error: ${exception.message ?: "Desconocido"}"
        }
    }
}