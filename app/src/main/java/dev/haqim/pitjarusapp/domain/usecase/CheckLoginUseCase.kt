package dev.haqim.pitjarusapp.domain.usecase

import dev.haqim.pitjarusapp.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckLoginUseCase @Inject constructor(
    private val repository: IAuthRepository
){
    operator fun invoke(): Flow<Boolean>{
        return repository.hasLogin()
    }
}