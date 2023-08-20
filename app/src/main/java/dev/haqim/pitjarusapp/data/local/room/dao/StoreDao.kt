package dev.haqim.pitjarusapp.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.haqim.pitjarusapp.data.local.entity.STORES_TABLE
import dev.haqim.pitjarusapp.data.local.entity.StoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg store: StoreEntity)

    @Query("DELETE FROM $STORES_TABLE")
    suspend fun resetAll()
    
    @Transaction
    suspend fun resetAndInsert(stores: Array<StoreEntity>){
        resetAll()
        insert(*stores)
    }
    
    @Query("SELECT * FROM $STORES_TABLE")
    fun getStores(): Flow<List<StoreEntity>>

    @Query("SELECT * FROM $STORES_TABLE LIMIT :limit OFFSET :offset")
    fun getStores(limit: Int, offset: Int): List<StoreEntity>

    @Query("SELECT * FROM $STORES_TABLE WHERE id = :id")
    fun getStoreFlow(id: Int): Flow<StoreEntity?>

    @Query("UPDATE $STORES_TABLE set picture = :picture WHERE id = :id")
    suspend fun updatePictureById(id: Int, picture: String)

    @Query("UPDATE $STORES_TABLE SET lastVisitDateTime = :visitDateTime, hasVisited = 1 WHERE id = :id")
    suspend fun updateStoreVisitById(id: Int, visitDateTime: String)

    @Query("UPDATE $STORES_TABLE SET latitude = :latitude, longitude = :longitude WHERE id = :id")
    suspend fun updateStoreLocationById(id: Int, latitude: String, longitude: String)
}