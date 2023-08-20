package dev.haqim.pitjarusapp.domain.usecase

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import dev.haqim.pitjarusapp.domain.model.Store
import dev.haqim.pitjarusapp.domain.repository.IStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class UpdateStorePictureUseCase @Inject constructor(
    private val repository: IStoreRepository
) {
    suspend operator fun invoke(store: Store, pictureUri: Uri?){
        return repository.updateStorePicture(store, pictureUri)
    }
}