package dev.haqim.pitjarusapp.data.remote

import dev.haqim.pitjarusapp.data.remote.response.LoginResponse
import dev.haqim.pitjarusapp.data.remote.retrofit.ApiService
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RemoteDataSource @Inject constructor(
    private val service: ApiService
) {
    suspend fun login(username: String, password: String): Result<LoginResponse>{
        return try {
            Result.success(service.login(username, password))
        }catch (e: Exception){
            Result.failure(e)
        }
    }
}