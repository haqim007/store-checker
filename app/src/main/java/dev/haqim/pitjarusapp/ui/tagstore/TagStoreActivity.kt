package dev.haqim.pitjarusapp.ui.tagstore

import android.location.Location
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import dagger.hilt.android.AndroidEntryPoint
import dev.haqim.pitjarusapp.BaseMapActivity
import dev.haqim.pitjarusapp.R
import dev.haqim.pitjarusapp.databinding.ActivityTagStoreBinding
import dev.haqim.pitjarusapp.domain.model.Store
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TagStoreActivity : BaseMapActivity() {
    private val viewModel: TagStoreViewModel by viewModels()
    private lateinit var binding: ActivityTagStoreBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTagStoreBinding.inflate(layoutInflater)

        val storeDetail: Store? = intent.getParcelableExtra(TAG_STORE_DETAIL_INTENT_KEY)
        viewModel.getUpdatedStore(storeDetail)
        
        setContentView(binding.root)

        binding.mapView.onCreate(mapViewBundle)
        binding.mapView.getMapAsync(this)
        binding.btnCancel.setOnClickListener { 
            finish()
        }
        binding.btnSave.setOnClickListener { 
            viewModel.savePosition()
            finish()
        }
    }

    override fun onMapReady(gMap: GoogleMap) {
        super.onMapReady(gMap)

        val store = viewModel.state.map { it.storeDetail }.distinctUntilChanged()
        lifecycleScope.launch { 
            store.collectLatest {
                var marker: Marker? = null
                val markerTitle = it?.storeName ?: getString(R.string.none)

                if(it?.latitude != null && it.longitude != null){
                    marker = generateMarkers(
                        markerTitle,
                        LatLng(it.latitude, it.longitude),
                        it
                    )
                    marker?.showInfoWindow()
                }

                mMap.setOnMapClickListener { latLng ->
                    if(marker == null){
                        marker = generateMarkers(
                            markerTitle,
                            latLng,
                            it
                        )
                    }else{
                        marker!!.position = latLng
                    }

                    viewModel.setNewPosition(latLng)
                    marker?.showInfoWindow()
                }

                
            }
        }
        
    }

    override fun onLastLocationNotNull(location: Location) {
        showLatestLocation(location)
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }


    companion object{
        const val TAG_STORE_DETAIL_INTENT_KEY = "TAG_STORE_DETAIL_INTENT_KEY"
    }
}