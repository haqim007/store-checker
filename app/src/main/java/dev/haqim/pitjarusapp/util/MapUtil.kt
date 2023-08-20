package dev.haqim.pitjarusapp.util

import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371 // Earth radius in kilometers

    val lat1Rad = Math.toRadians(lat1)
    val lon1Rad = Math.toRadians(lon1)
    val lat2Rad = Math.toRadians(lat2)
    val lon2Rad = Math.toRadians(lon2)

    val dLat = lat2Rad - lat1Rad
    val dLon = lon2Rad - lon1Rad

    val a = sin(dLat / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return R * c
}

fun calculateDistanceInMeter(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double{
    return calculateDistance(lat1, lon1, lat2, lon2) * 1000
}

fun calculateDistanceInMeter(start: LatLng, end: LatLng): Double{
    return calculateDistance(start.latitude, start.longitude, end.latitude, end.longitude) * 1000
}
