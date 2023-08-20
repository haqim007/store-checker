package dev.haqim.pitjarusapp.data.remote.response

import com.google.gson.annotations.SerializedName
import dev.haqim.pitjarusapp.data.local.entity.StoreEntity


data class LoginResponse(

	@field:SerializedName("stores")
	val stores: List<StoresItem>?,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
){

	data class StoresItem(

		@field:SerializedName("store_id")
		val storeId: String,

		@field:SerializedName("store_code")
		val storeCode: String,

		@field:SerializedName("channel_name")
		val channelName: String,

		@field:SerializedName("area_name")
		val areaName: String,

		@field:SerializedName("address")
		val address: String,

		@field:SerializedName("dc_name")
		val dcName: String,

		@field:SerializedName("latitude")
		val latitude: String,

		@field:SerializedName("region_id")
		val regionId: String,

		@field:SerializedName("area_id")
		val areaId: String,

		@field:SerializedName("account_id")
		val accountId: String,

		@field:SerializedName("dc_id")
		val dcId: String,

		@field:SerializedName("subchannel_id")
		val subchannelId: String,

		@field:SerializedName("account_name")
		val accountName: String,

		@field:SerializedName("store_name")
		val storeName: String,

		@field:SerializedName("subchannel_name")
		val subchannelName: String,

		@field:SerializedName("region_name")
		val regionName: String,

		@field:SerializedName("channel_id")
		val channelId: String,

		@field:SerializedName("longitude")
		val longitude: String
	)
}

fun LoginResponse.toEntity(): List<StoreEntity>?{
	return this.stores?.map { it.toEntity() }
}

fun LoginResponse.StoresItem.toEntity(): StoreEntity {
	return StoreEntity(
		storeId = this.storeId,
		storeCode = this.storeCode,
		channelName = this.channelName,
		areaName = this.areaName,
		address = this.address,
		dcName = dcName,
		regionId = regionId,
		areaId = areaId,
		accountId = accountId,
		dcId = dcId,
		subchannelId = subchannelId,
		accountName = accountName,
		storeName = storeName,
		subchannelName = subchannelName,
		regionName = regionName,
		channelId = channelId,
		latitude = null,
		longitude = null,
	)
}