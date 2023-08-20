package dev.haqim.pitjarusapp.domain.repository

import dev.haqim.pitjarusapp.domain.mechanism.Resource
import dev.haqim.pitjarusapp.domain.model.LoginStatus
import dev.haqim.pitjarusapp.domain.model.Store
import dev.haqim.pitjarusapp.domain.model.Username
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    fun login(username: String, password: String, keepUsername: Boolean): Flow<Resource<LoginStatus>>
    suspend fun loadUsername(): Username
    fun hasLogin(): Flow<Boolean>
    suspend fun logout()
}