package dev.haqim.pitjarusapp.domain.usecase

import dev.haqim.pitjarusapp.domain.mechanism.Resource
import dev.haqim.pitjarusapp.domain.model.LoginStatus
import dev.haqim.pitjarusapp.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginUseCase @Inject constructor (
    private val repository: IAuthRepository
){
    operator fun invoke(username: String, password: String, keepUsername: Boolean = false): Flow<Resource<LoginStatus>> {
        return repository.login(username, password, keepUsername)
    }
}