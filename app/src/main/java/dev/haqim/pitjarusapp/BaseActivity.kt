package dev.haqim.pitjarusapp

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.haqim.pitjarusapp.ui.camera.CameraActivity
import java.io.File

abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var fusedLocationClient: FusedLocationProviderClient
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){ permissions ->
        when{
            permissions[ACCESS_FINE_LOCATION] ?: false -> {
                if(!checkPermissionLocationAccess()) launchPermissionLocation()
            }
            permissions[ACCESS_COARSE_LOCATION] ?: false -> {
                if(!checkPermissionLocationAccess()) launchPermissionLocation()
            }
            permissions[READ_EXTERNAL_STORAGE] ?: false -> {
                if(!checkPermissionMediaImage()) {
                    launchPermissionMediaImage()
                }else{
                    launchGalleryPicker()
                }
            }
            permissions[CAMERA] ?: false -> {
                if(!checkPermissionCamera()) {
                    launchPermissionCamera()
                }else{
                    launchCamera()
                }
            }
            else -> {}
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            when{
                permissions[READ_MEDIA_IMAGES] ?: false -> {
                    if(!checkPermissionMediaImage()) {
                        launchPermissionMediaImage()
                    }else{
                        launchGalleryPicker()
                    }
                }
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            handleIntentGalleryResult(selectedImg)
        }
    }
    
    protected open fun handleIntentGalleryResult(uri: Uri?){}

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra(CameraActivity.PICTURE) as File
            handleIntentCameraResult(myFile.toUri())
        }
    }

    protected open fun handleIntentCameraResult(uri: Uri?){}
    
    protected val loadingDialog: AlertDialog by lazy {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.loading_dlalog, null)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()


        dialog

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }
    
    protected fun messageDialog(message: String): AlertDialog {
        return MaterialAlertDialogBuilder(this).setMessage(message)
            .setPositiveButton(resources.getString(R.string.close)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
    
    protected fun checkPermissionLocationAccess(): Boolean{
        return checkPermission(ACCESS_FINE_LOCATION) && checkPermission(ACCESS_COARSE_LOCATION)
    }
    
    protected fun checkPermission(permission: String): Boolean{
        return ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    protected fun launchPermissionLocation(){
        requestPermissionLauncher.launch(
            arrayOf(
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION
            )
        )
    }
    
    protected fun checkPermissionMediaImage(): Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            checkPermission(READ_MEDIA_IMAGES)
        } else {
            checkPermission(READ_EXTERNAL_STORAGE)
        }
    }

    protected fun launchPermissionMediaImage(){
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            READ_MEDIA_IMAGES
        } else {
            READ_EXTERNAL_STORAGE
        }
        requestPermissionLauncher.launch(
            arrayOf(
                permission
            )
        )
    }

    protected fun launchPermissionCamera(){
       
        requestPermissionLauncher.launch(
            arrayOf(
                CAMERA
            )
        )
    }
    
    protected fun launchGalleryPicker(){
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_a_picture))
        launcherIntentGallery.launch(chooser)
    }

    protected fun checkPermissionCamera(): Boolean{
        return checkPermission(CAMERA)
    }
    
    protected fun launchCamera(){
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    @SuppressLint("MissingPermission")
    protected fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onLastLocationNotNull(location)
            }
            onCompleteGetLastLocation(location)

        }.addOnFailureListener {
            onCompleteGetLastLocation()
        }
    }

    /**
     * Callback to handle case when last location is not null
     * */
    protected open fun onLastLocationNotNull(location: Location){}

    /**
     * Callback to handle success or failure case after attempt to get last location
     * */
    protected open fun onCompleteGetLastLocation(location: Location? = null){}
    
    companion object{
        const val CAMERA_X_RESULT = 200
    }
}