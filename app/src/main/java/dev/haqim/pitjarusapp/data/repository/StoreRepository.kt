package dev.haqim.pitjarusapp.data.repository

import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.android.gms.maps.model.LatLng
import dev.haqim.pitjarusapp.data.local.LocalDataSource
import dev.haqim.pitjarusapp.data.local.entity.toModel
import dev.haqim.pitjarusapp.data.mechanism.NetworkBoundResource
import dev.haqim.pitjarusapp.data.pagingsource.StorePagingSource
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
import dev.haqim.pitjarusapp.domain.repository.IStoreRepository
import dev.haqim.pitjarusapp.util.getCurrentDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StoreRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val storePagingSource: StorePagingSource
): IStoreRepository {

    override fun getStores(): Flow<List<Store>> {
        return localDataSource.getStores().toModel()
    }

    override fun getStoresPaging(): Flow<PagingData<Store>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false,
                maxSize = 90
            ),
            pagingSourceFactory = {
                storePagingSource
            }
        ).flow
    }

    override suspend fun updateStoreLocation(store: Store, newLocation: LatLng) {
        withContext(Dispatchers.IO){
            localDataSource.updateStoreLocationByStoreId(
                store.id, 
                newLocation.latitude.toString(), 
                newLocation.longitude.toString()
            )
        }
    }

    override suspend fun getStore(store: Store): Flow<Store?> {
        return withContext(Dispatchers.IO){
            localDataSource.getStoreFlow(store.id).map { it?.toModel() }
        }
    }

    override suspend fun updateStorePicture(store: Store, pictureUri: Uri?) {
        withContext(Dispatchers.IO){
            localDataSource.updatePictureByStoreId(store.id, picture = pictureUri.toString())
        }
    }

    override suspend fun updateStoreVisit(store: Store) {
        withContext(Dispatchers.IO){
            localDataSource.updateStoreVisitByStoreId(store.id, visitDateTime = getCurrentDate())
        }
    }
}