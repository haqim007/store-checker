package dev.haqim.pitjarusapp.ui.storelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.haqim.pitjarusapp.domain.model.Store
import dev.haqim.pitjarusapp.domain.model.Username
import dev.haqim.pitjarusapp.domain.usecase.GetStoresPagingUseCase
import dev.haqim.pitjarusapp.domain.usecase.GetStoresUseCase
import dev.haqim.pitjarusapp.domain.usecase.LoadUsernameUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreListViewModel @Inject constructor(
    private val getStoresUseCase: GetStoresUseCase,
    private val getStoresPagingUseCase: GetStoresPagingUseCase,
    private val loadUsernameUseCase: LoadUsernameUseCase
): ViewModel() {
    private val _state = MutableStateFlow(StoreListUiState())
    val state = _state.asStateFlow()
    val pagingDataFlow: Flow<PagingData<Store>>
    
    init {
        getStores()
        loadUsername()
        pagingDataFlow = getStoresPaging().cachedIn(viewModelScope)
    }
    
    private fun loadUsername(){
        viewModelScope.launch {
            _state.update { state->
                state.copy(
                    username = loadUsernameUseCase()
                )
            }
        }
    }
    
    private fun getStores(){
        viewModelScope.launch { 
            getStoresUseCase().collectLatest{
                _state.update {state -> 
                    state.copy(
                        stores = it
                    )
                }
            }
        }
    }
    
    private fun getStoresPaging(): Flow<PagingData<Store>>{
        return getStoresPagingUseCase()
    }
}

data class StoreListUiState(
    val stores: List<Store> = listOf(),
    val username: Username? = null
)