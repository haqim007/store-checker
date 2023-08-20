package dev.haqim.pitjarusapp.domain.usecase

import dev.haqim.pitjarusapp.domain.model.Username
import dev.haqim.pitjarusapp.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class LoadUsernameUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(): Username{
        return repository.loadUsername()
    }
}