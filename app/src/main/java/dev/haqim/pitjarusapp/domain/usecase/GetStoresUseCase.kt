package dev.haqim.pitjarusapp.domain.usecase

import dev.haqim.pitjarusapp.domain.model.Store
import dev.haqim.pitjarusapp.domain.repository.IStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetStoresUseCase @Inject constructor(
    private val repository: IStoreRepository
) {
    operator fun invoke(): Flow<List<Store>> {
        return repository.getStores()
    }
}