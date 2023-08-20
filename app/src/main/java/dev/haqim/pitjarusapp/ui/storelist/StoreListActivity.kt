package dev.haqim.pitjarusapp.ui.storelist

import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import dev.haqim.pitjarusapp.BaseMapActivity
import dev.haqim.pitjarusapp.R
import dev.haqim.pitjarusapp.databinding.ActivityStoreListBinding
import dev.haqim.pitjarusapp.domain.model.Store
import dev.haqim.pitjarusapp.domain.model.Username
import dev.haqim.pitjarusapp.ui.storeverification.StoreVerificationActivity
import dev.haqim.pitjarusapp.ui.storeverification.StoreVerificationActivity.Companion.STORE_DETAIL_INTENT_KEY
import dev.haqim.pitjarusapp.util.getCurrentDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StoreListActivity : BaseMapActivity() {
    private lateinit var binding: ActivityStoreListBinding
    private val viewModel: StoreListViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        bindStates()
        
        binding.tvVisitTitle.text = getString(R.string.list_kunjungan_s).format(
            getCurrentDate()
        )
        binding.mapView.onCreate(mapViewBundle)
        binding.mapView.getMapAsync(this)
    }
    
    private fun bindStates(){
        val state = viewModel.state
        bindUsernameState(state.map { it.username })
    }
    
    private fun bindUsernameState(usernameState: Flow<Username?>){
        val state = usernameState.distinctUntilChanged()
        lifecycleScope.launch { 
            state.collectLatest { 
                binding.tvUsername.text = it
            }
        }
    }

    override fun onMapReady(gMap: GoogleMap) {
        super.onMapReady(gMap)

        val state = viewModel.state
        showStoresLocation(state.map { it.stores })
    }
    
    private fun showStoresLocation(storesState: Flow<List<Store>>){
        val state = storesState.distinctUntilChanged()
        lifecycleScope.launch { 
            state.collect{stores ->
                stores.onEach { 
                    if(it.latitude != null && it.longitude != null){
                        val marker = generateMarkers(
                            it.storeName,
                            LatLng(it.latitude, it.longitude),
                            it
                        )
                        marker?.showInfoWindow()
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.customActionBar)
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onCompleteGetLastLocation(location: Location?) {
        super.onCompleteGetLastLocation(location)
        // show list with current location
        bindStoreList(location)
    }

    private lateinit var adapter: StoreListAdapter
    private fun bindStoreList(currentLocation: Location?){
        val pagingData = viewModel.pagingDataFlow
        adapter = StoreListAdapter(
            object: StoreListListener(){
                override val currentLocation: Location?
                    get() = currentLocation
                override fun onClick(store: Store) {
                    val intent = Intent(this@StoreListActivity, StoreVerificationActivity::class.java)
                    intent.putExtra(STORE_DETAIL_INTENT_KEY, store)
                    startActivity(intent)
                }
            }
        )

        binding.rvStoreList.adapter = adapter
        lifecycleScope.launch {
            pagingData.collect(adapter::submitData)
        }
    }
    

    override fun onLastLocationNotNull(location: Location) {
        showLatestLocation(location)
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onRestart() {
        super.onRestart()
        if(::adapter.isInitialized){
            adapter.refresh()
        }
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

}