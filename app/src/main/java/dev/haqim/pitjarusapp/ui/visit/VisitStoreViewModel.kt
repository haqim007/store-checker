package dev.haqim.pitjarusapp.ui.visit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.haqim.pitjarusapp.domain.model.Store
import dev.haqim.pitjarusapp.domain.usecase.GetStoreUseCase
import dev.haqim.pitjarusapp.domain.usecase.UpdateStoreLocationUseCase
import dev.haqim.pitjarusapp.domain.usecase.UpdateStoreVisitUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VisitStoreViewModel @Inject constructor(
    private val updateStoreVisitUseCase: UpdateStoreVisitUseCase
): ViewModel() {
    private val _state = MutableStateFlow(TagStoreUiState())
    val state = _state.asStateFlow()
    
    
    fun saveVisit(store: Store){
        viewModelScope.launch {
            updateStoreVisitUseCase(store)
        }
    }
}

data class TagStoreUiState(
    val newPosition: LatLng? = null
)