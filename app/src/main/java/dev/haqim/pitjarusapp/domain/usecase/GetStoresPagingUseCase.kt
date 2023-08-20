package dev.haqim.pitjarusapp.domain.usecase

import androidx.paging.PagingData
import dev.haqim.pitjarusapp.domain.model.Store
import dev.haqim.pitjarusapp.domain.repository.IStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetStoresPagingUseCase @Inject constructor(
    private val repository: IStoreRepository
) {
    operator fun invoke(): Flow<PagingData<Store>> {
        return repository.getStoresPaging()
    }
}