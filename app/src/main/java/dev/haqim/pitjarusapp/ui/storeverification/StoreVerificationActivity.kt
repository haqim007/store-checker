package dev.haqim.pitjarusapp.ui.storeverification

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.haqim.pitjarusapp.BaseActivity
import dev.haqim.pitjarusapp.R
import dev.haqim.pitjarusapp.databinding.ActivityStoreVerificationBinding
import dev.haqim.pitjarusapp.domain.model.Store
import dev.haqim.pitjarusapp.domain.model.Username
import dev.haqim.pitjarusapp.ui.tagstore.TagStoreActivity
import dev.haqim.pitjarusapp.ui.tagstore.TagStoreActivity.Companion.TAG_STORE_DETAIL_INTENT_KEY
import dev.haqim.pitjarusapp.ui.visit.VisitStoreActivity
import dev.haqim.pitjarusapp.ui.visit.VisitStoreActivity.Companion.VISIT_STORE_DETAIL_KEY
import dev.haqim.pitjarusapp.util.calculateDistanceInMeter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@AndroidEntryPoint
class StoreVerificationActivity : BaseActivity() {
    
    private lateinit var binding: ActivityStoreVerificationBinding
    private val viewModel: StoreVerificationViewModel by viewModels()
    private var lastLocation: Location? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreVerificationBinding.inflate(layoutInflater)
        
        val storeDetail: Store? = intent.getParcelableExtra(STORE_DETAIL_INTENT_KEY)
        viewModel.getUpdatedStore(storeDetail)
        
        setContentView(binding.root)
        
        setupToolbar()
        bindState()
        
        binding.mcvTagLocation.setOnClickListener {
            val intent = Intent(this, TagStoreActivity::class.java)
            intent.putExtra(TAG_STORE_DETAIL_INTENT_KEY, storeDetail)
            startActivity(intent)
        }
        
        binding.mcvTakePict.setOnClickListener { 
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.choose)
                .setItems(
                    arrayOf(getString(R.string.camera), getString(R.string.gallery))
                ){ dialog, which ->
                    when (which) {
                        1 -> {
                            getImageFromGallery()
                        }
                        0 -> {
                            getImageFromCamera()
                        }
                    }
                }
                .show()
        }
        
        binding.mcvNavigation.setOnClickListener {
            val store = viewModel.state.value.storeDetail
            store?.let {
                if(it.latitude != null && it.longitude != null){
                    navigateToLocation(
                        destination = LatLng(it.latitude, it.longitude)
                    )
                }else{
                    MaterialAlertDialogBuilder(this)
                        .setMessage(getString(R.string.store_location_is_not_set))
                        .setPositiveButton(R.string.close){ dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
            
        }

        if(checkPermissionLocationAccess()){
            getLastLocation()
        }else{
            launchPermissionLocation()
        }
    }

    override fun onLastLocationNotNull(location: Location) {
        super.onLastLocationNotNull(location)
        lastLocation = location
    }
    
    private fun getImageFromGallery(){
        if(checkPermissionMediaImage()){
            launchGalleryPicker()
        }else{
            launchPermissionMediaImage()
        }
    }

    private fun getImageFromCamera(){
        if(checkPermissionCamera()){
            launchCamera()
        }else{
            launchPermissionCamera()
        }
    }

    override fun handleIntentGalleryResult(uri: Uri?) {
        viewModel.updatePicture(uri)
    }

    override fun handleIntentCameraResult(uri: Uri?) {
        viewModel.updatePicture(uri)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.customActionBar)
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
    
    private fun bindState(){
        val state = viewModel.state
        bindStoreDetail(state.map { it.storeDetail })
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
    
    private fun bindStoreDetail(storeDetailState: Flow<Store?>){
        val state = storeDetailState.distinctUntilChanged()
        lifecycleScope.launch { 
            state.collect{store ->
                store?.let { 
                    binding.tvChannelName.text = it.channelName
                    binding.tvSubChannelName.text = it.subchannelName
                    binding.tvOutletType.text = it.dcName
                    binding.tvStoreName.text = it.storeName
                    binding.tvStoreAddress.text = it.address
                    
                    binding.btnNoVisit.isVisible = it.picture != null && it.latitude != null && it.longitude != null
                    binding.btnVisit.isVisible = it.picture != null && it.latitude != null && it.longitude != null
                    binding.tvMessageAction.isVisible = !binding.btnVisit.isVisible
                    binding.tvLastVisit.text = it.lastVisitDate
                    
                    if (it.picture == null){
                        binding.tvMessageAction.text = getString(R.string.set_store_pict_first)
                    }
                    else if(it.longitude == null || it.latitude == null){
                        binding.tvMessageAction.text = getString(R.string.tag_store_location_first)
                    }
                    else{
                        binding.tvMessageAction.text = null
                    }

                    Glide.with(this@StoreVerificationActivity).load(store.picture)
                        .placeholder(R.drawable.image_broken)
                        .into(binding.ivStorePict)


                    binding.btnVisit.setOnClickListener { _ ->
                        setupButtonVisit(it)
                    }
                    
                }
            }
        }
    }

    private fun setupButtonVisit(store: Store) {
        lastLocation?.let { lastLocation ->
            if (store.latitude != null && store.longitude != null) {
                val distance = calculateDistanceInMeter(
                    start = LatLng(lastLocation.latitude, lastLocation.longitude),
                    end = LatLng(store.latitude, store.longitude)
                )
                if (distance > 100) {
                    MaterialAlertDialogBuilder(this)
                        .setMessage(getString(R.string.store_distance_is_too_far))
                        .setPositiveButton(R.string.close){ dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                } else {
                    val intent = Intent(
                        this@StoreVerificationActivity,
                        VisitStoreActivity::class.java)
                    intent.putExtra(VISIT_STORE_DETAIL_KEY, store)
                    startActivity(intent)
                }
            }else{
                MaterialAlertDialogBuilder(this)
                    .setMessage(getString(R.string.store_location_is_not_set))
                    .setPositiveButton(R.string.close){ dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
        } ?: run{
            MaterialAlertDialogBuilder(this)
                .setMessage(getString(R.string.activate_location_feature_to_get_last_location))
                .setPositiveButton(R.string.close){ dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun navigateToLocation(destination: LatLng) {
        val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${destination.latitude},${destination.longitude}")

        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps") // Set the package to open Google Maps

        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        lastLocation = null
    }
    
    companion object{
        const val STORE_DETAIL_INTENT_KEY = "store_detail_intent_key"
    }
}