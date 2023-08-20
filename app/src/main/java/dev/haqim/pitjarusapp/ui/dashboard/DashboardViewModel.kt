package dev.haqim.pitjarusapp.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.haqim.pitjarusapp.domain.usecase.CheckLoginUseCase
import dev.haqim.pitjarusapp.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val checkLoginUseCase: CheckLoginUseCase,
    private val logoutUseCase: LogoutUseCase
): ViewModel() {
    
    private val _state = MutableStateFlow(DashboardUiState())
    val state 
        get() =  _state.asStateFlow()
    
    init {
        checkLogin()
    }
    
    private fun checkLogin(){
        viewModelScope.launch { 
            checkLoginUseCase().collect{
                _state.update { state ->
                    state.copy(
                        hasLogin = it
                    )
                }
            }
        }
    }
    
    fun logout(){
        viewModelScope.launch { 
            logoutUseCase()
        }
    }
    
}

data class DashboardUiState(
    val hasLogin: Boolean? = null
)