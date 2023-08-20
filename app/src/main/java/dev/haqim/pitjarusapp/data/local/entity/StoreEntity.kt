package dev.haqim.pitjarusapp.data.local.entity

import androidx.core.net.toUri
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.haqim.pitjarusapp.domain.model.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val STORES_TABLE = "stores"
@Entity(tableName = STORES_TABLE)
data class StoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val storeId: String,
    val storeCode: String,
    val channelName: String,
    val areaName: String,
    val address: String,
    val dcName: String,
    val latitude: String?,
    val regionId: String,
    val areaId: String,
    val accountId: String,
    val dcId: String,
    val subchannelId: String,
    val accountName: String,
    val storeName: String,
    val subchannelName: String,
    val regionName: String,
    val channelId: String,
    val longitude: String?,
    val picture: String? = null,
    val hasVisited: Int = 0,
    val lastVisitDateTime: String? = null
)

fun Flow<List<StoreEntity>>.toModel(): Flow<List<Store>>{
    return this.map { 
        it.toModel()
    }
}

fun List<StoreEntity>.toModel(): List<Store>{
    return this.map { it.toModel() }
}

fun StoreEntity.toModel(): Store{
    return Store(
        id, 
        storeId, storeCode, channelName, areaName, address, 
        dcName, latitude?.toDouble(), regionId, areaId, accountId, dcId, 
        subchannelId, accountName, storeName = this.storeName + " (${this.id})", subchannelName, 
        regionName, channelId, longitude?.toDouble(), picture?.toUri(), hasVisited == 1, 
        lastVisitDateTime
    )
}