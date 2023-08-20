package dev.haqim.pitjarusapp.domain.usecase

import dev.haqim.pitjarusapp.domain.repository.IAuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: IAuthRepository
){
    suspend operator fun invoke(){
        repository.logout()
    }
}