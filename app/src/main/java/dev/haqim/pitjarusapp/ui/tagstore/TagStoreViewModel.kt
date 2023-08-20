package dev.haqim.pitjarusapp.ui.tagstore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.haqim.pitjarusapp.domain.model.Store
import dev.haqim.pitjarusapp.domain.usecase.GetStoreUseCase
import dev.haqim.pitjarusapp.domain.usecase.UpdateStoreLocationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagStoreViewModel @Inject constructor(
    private val updateStoreLocationUseCase: UpdateStoreLocationUseCase,
    private val getStoreUseCase: GetStoreUseCase
): ViewModel() {
    private val _state = MutableStateFlow(TagStoreUiState())
    val state = _state.asStateFlow()
    
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
    
    fun setNewPosition(latLng: LatLng){
        _state.update { state ->
            state.copy(
                newPosition = latLng
            )
        }
    }
    
    fun savePosition(){
        viewModelScope.launch {
            val newPosition = state.value.newPosition
            val store = state.value.storeDetail
            if(newPosition != null && store != null){
                updateStoreLocationUseCase(store, newPosition)
            }
        }
    }
}

data class TagStoreUiState(
    val storeDetail: Store? = null,
    val newPosition: LatLng? = null
)