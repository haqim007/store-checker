package dev.haqim.pitjarusapp.domain.repository

import android.net.Uri
import androidx.paging.PagingData
import com.google.android.gms.maps.model.LatLng
import dev.haqim.pitjarusapp.domain.model.Store
import kotlinx.coroutines.flow.Flow

interface IStoreRepository {
    fun getStores(): Flow<List<Store>>

    fun getStoresPaging(): Flow<PagingData<Store>>
    suspend fun updateStoreLocation(store: Store, newLocation: LatLng)
    suspend fun updateStorePicture(store: Store, pictureUri: Uri?)
    suspend fun getStore(store: Store): Flow<Store?>
    suspend fun updateStoreVisit(store: Store)
}