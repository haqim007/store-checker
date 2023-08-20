package dev.haqim.pitjarusapp.ui.storeverification

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.haqim.pitjarusapp.domain.model.Store
import dev.haqim.pitjarusapp.domain.model.Username
import dev.haqim.pitjarusapp.domain.usecase.GetStoreUseCase
import dev.haqim.pitjarusapp.domain.usecase.LoadUsernameUseCase
import dev.haqim.pitjarusapp.domain.usecase.UpdateStorePictureUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreVerificationViewModel @Inject constructor(
    private val loadUsernameUseCase: LoadUsernameUseCase,
    private val updateStorePictureUseCase: UpdateStorePictureUseCase,
    private val getStoreUseCase: GetStoreUseCase
): ViewModel() {
    private val _state = MutableStateFlow(StoreVerificationUiState())
    val state = _state.asStateFlow()
    
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
    
    fun getUpdatedStore(store: Store?){
        viewModelScope.launch {
            if (store != null) {
                getStoreUseCase(store).collectLatest {
                    _state.update { state ->
                        state.copy(
                            storeDetail = it
                        )
                    }
                }
            }
        }
    }

    fun updatePicture(uri: Uri?) {
        viewModelScope.launch {
            state.value.storeDetail?.let {
                updateStorePictureUseCase(
                    it,
                    uri
                )
            }
        }
    }
}

data class StoreVerificationUiState(
    val storeDetail: Store? = null,
    val username: Username? = null
)