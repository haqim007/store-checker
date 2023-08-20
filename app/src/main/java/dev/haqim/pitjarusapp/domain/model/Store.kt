package dev.haqim.pitjarusapp.domain.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Store(
    val id: Int = 0,
    val storeId: String,
    val storeCode: String,
    val channelName: String,
    val areaName: String,
    val address: String,
    val dcName: String,
    val latitude: Double?,
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
    val longitude: Double?,
    val picture: Uri? = null,
    val hasVisited: Boolean = false,
    val lastVisitDate: String? = null
): Parcelable
