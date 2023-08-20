package dev.haqim.pitjarusapp.domain.usecase

import dev.haqim.pitjarusapp.domain.model.Store
import dev.haqim.pitjarusapp.domain.repository.IStoreRepository
import javax.inject.Inject


class UpdateStoreVisitUseCase @Inject constructor(
    private val repository: IStoreRepository
) {
    suspend operator fun invoke(store: Store){
        return repository.updateStoreVisit(store)
    }
}