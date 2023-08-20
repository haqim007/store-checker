package dev.haqim.pitjarusapp.ui.login

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.haqim.pitjarusapp.R
import dev.haqim.pitjarusapp.domain.mechanism.Resource
import dev.haqim.pitjarusapp.domain.model.LoginStatus
import dev.haqim.pitjarusapp.domain.model.Username
import dev.haqim.pitjarusapp.domain.usecase.LoadUsernameUseCase
import dev.haqim.pitjarusapp.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor (
    private val loginUseCase: LoginUseCase,
    private val loadUsernameUseCase: LoadUsernameUseCase
): ViewModel() {
    private var _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state
        .asStateFlow()
    
    init {
        loadUsername()
    }
    
    
    private fun loadUsername(){
        viewModelScope.launch { 
            _state.update { state ->
                state.copy(
                    username = loadUsernameUseCase()
                )
            }
        }
    }
    
    fun onUsernameChange(username: String){
        _state.update { state ->
            state.copy(
                usernameError = if (username.isEmpty()){
                    R.string.field_is_required
                }else{
                    null
                }
            )
        }
    }
    
    fun onPasswordChange(password: String){
        _state.update { state ->
            state.copy(
                passwordError = if (password.isEmpty()){
                    R.string.field_is_required
                }else{
                    null
                }
            )
        }
    }
    
    fun login(username: String, password: String, keepUsername: Boolean){
        if (username.isEmpty() && password.isEmpty()){
            _state.update { state ->
                state.copy(
                    usernameError = R.string.field_is_required,
                    passwordError = R.string.field_is_required
                )
            }
        }
        else if (username.isEmpty()){
            _state.update { state ->
                state.copy(
                    usernameError = R.string.field_is_required,
                    passwordError = null //password should not be error at this point
                )
            }
        }
        else if(password.isEmpty()){
            _state.update { state ->
                state.copy(
                    usernameError = null, //username should not be error at this point
                    passwordError = R.string.field_is_required
                )
            }
        }else{
            _state.update { state ->
                state.copy(
                    username = username,
                    usernameError = null, 
                    passwordError = null, //password should not be error at this point
                    
                )
            }
            viewModelScope.launch {
                loginUseCase(username, password, keepUsername).collect{
                    _state.update { state ->
                        state.copy(
                            loginStatus = it
                        )
                    }
                }
            }
        }
    }
        
}

data class LoginUiState(
    val username: Username? = null,
    val loginStatus: Resource<LoginStatus> = Resource.Idle(),
    @StringRes val usernameError: Int? = null,
    @StringRes val passwordError: Int? = null
)
