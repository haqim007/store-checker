package dev.haqim.pitjarusapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

abstract class BaseMapActivity : BaseActivity(), OnMapReadyCallback {
    
    protected lateinit var mMap: GoogleMap
    protected val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    private var _mapViewBundle: Bundle? = null
    protected val mapViewBundle: Bundle?
        get() = _mapViewBundle
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if(savedInstanceState != null){
            _mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
    }
    
    override fun onMapReady(gMap: GoogleMap) {
        mMap = gMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getPermissionLocation()
        

    }

    @SuppressLint("MissingPermission")
    protected fun getPermissionLocation(){
        if(checkPermissionLocationAccess()){
            mMap.isMyLocationEnabled = true
            getLastLocation()
        }else{
            launchPermissionLocation()
        }
    }

    protected open fun showLatestLocation(location: Location, zoom: Float = 16.5f){
        val currentLocation = LatLng(location.latitude, location.longitude)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16.5f))

        mMap.addCircle(
            CircleOptions()
                .center(currentLocation)
                .radius(100.0)
                .fillColor(ContextCompat.getColor(this, R.color.blue_radius))
                .strokeColor(ContextCompat.getColor(this, R.color.blue_radius))
                .strokeWidth(3f)
        )
    }
    
    protected fun generateMarkers(title: String, latLng: LatLng, tag: Any? = null): Marker? {
        val marker = mMap
            .addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .icon(vectorToBitmap(R.drawable.baseline_location_on_24, Color.parseColor("#3DDC84")))
                    .visible(true)
            )
        marker?.tag = tag
        return marker
    }

    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onDestroy() {
        super.onDestroy()
        _mapViewBundle = null
    }
}