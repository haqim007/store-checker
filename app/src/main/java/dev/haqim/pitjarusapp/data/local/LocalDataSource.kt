package dev.haqim.pitjarusapp.data.local

import dev.haqim.pitjarusapp.data.local.entity.StoreEntity
import dev.haqim.pitjarusapp.data.local.room.AppDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LocalDataSource @Inject constructor(
    private val database: AppDatabase
){
    private val dao = database.storeDao()
    
    fun getStores(): Flow<List<StoreEntity>>{
        return dao.getStores()
    }

    fun getStores(limit: Int, offset: Int): List<StoreEntity>{
        return dao.getStores(limit, offset)
    }

    suspend fun insert(store: Array<StoreEntity>){
        dao.resetAndInsert(store)
    }
    
    suspend fun updatePictureByStoreId(id: Int, picture: String){
        dao.updatePictureById(id, picture)
    }

    suspend fun updateStoreVisitByStoreId(id: Int, visitDateTime: String){
        dao.updateStoreVisitById(id, visitDateTime)
    }

    suspend fun updateStoreLocationByStoreId(id: Int, latitude: String, longitude: String){
        dao.updateStoreLocationById(id, latitude, longitude)
    }
    
    fun getStoreFlow(id: Int): Flow<StoreEntity?>{
        return dao.getStoreFlow(id)
    }
}