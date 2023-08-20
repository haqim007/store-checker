package dev.haqim.pitjarusapp.data.repository

import dev.haqim.pitjarusapp.data.local.LocalDataSource
import dev.haqim.pitjarusapp.data.local.entity.toModel
import dev.haqim.pitjarusapp.data.mechanism.NetworkBoundResource
import dev.haqim.pitjarusapp.data.preference.UserPreference
import dev.haqim.pitjarusapp.data.remote.RemoteDataSource
import dev.haqim.pitjarusapp.data.remote.response.LoginResponse
import dev.haqim.pitjarusapp.data.remote.response.toEntity
import dev.haqim.pitjarusapp.domain.mechanism.Resource
import dev.haqim.pitjarusapp.domain.model.LoginStatus
import dev.haqim.pitjarusapp.domain.model.LoginStatusEnum
import dev.haqim.pitjarusapp.domain.model.Store
import dev.haqim.pitjarusapp.domain.model.Username
import dev.haqim.pitjarusapp.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val userPreference: UserPreference
): IAuthRepository {
    override fun login(username: String, password: String, keepUsername: Boolean): Flow<Resource<LoginStatus>> {
        return object: NetworkBoundResource<LoginStatus, LoginResponse>(){
            override suspend fun fetchFromRemote(): Result<LoginResponse> {
                return remoteDataSource.login(username, password)
            }

            override suspend fun saveRemoteData(data: LoginResponse) {
                if(data.status == LoginStatusEnum.SUCCESS.value){
                    data.stores?.let {
                        localDataSource.insert(data.toEntity()!!.toTypedArray())
                        userPreference.onLogin(
                            username = if(keepUsername) username else ""
                        )
                    }   
                }
            }

            override fun loadFromNetwork(data: LoginResponse): Flow<LoginStatus> {
                return flowOf(
                    if (data.status == LoginStatusEnum.FAILURE.value)
                        LoginStatus.Failure(message = data.message)
                    else
                        LoginStatus.Success
                )
            }

        }.asFlow()
    }

    override suspend fun loadUsername(): Username {
        return userPreference.getUsername().first()
    }

    override fun hasLogin(): Flow<Boolean> {
        return userPreference.hasLogin()
    }

    override suspend fun logout() {
        userPreference.onLogout()
    }
}