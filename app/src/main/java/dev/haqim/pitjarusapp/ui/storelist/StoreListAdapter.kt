package dev.haqim.pitjarusapp.ui.storelist

import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import dev.haqim.pitjarusapp.R
import dev.haqim.pitjarusapp.databinding.StoreItemBinding
import dev.haqim.pitjarusapp.domain.model.Store
import dev.haqim.pitjarusapp.util.calculateDistanceInMeter

class StoreListAdapter(
    private val listener: StoreListListener
) : PagingDataAdapter<Store, RecyclerView.ViewHolder>(DIFF_CALLBACK){

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).onBind(getItem(position), listener)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.onCreate(parent)
    }
    
    class ViewHolder private constructor(
        private val binding: StoreItemBinding
    ): RecyclerView.ViewHolder(binding.root){
        
        fun onBind(store: Store?, listener: StoreListListener){
            store?.let { 
                binding.tvStoreName.text = it.storeName
                if(it.latitude == null || it.longitude == null){
                    binding.ivLocationPin.setImageDrawable(
                        ContextCompat.getDrawable(binding.root.context, R.drawable.baseline_wrong_location_24)
                    )
                }else{
                    binding.ivLocationPin.setImageDrawable(
                        ContextCompat.getDrawable(binding.root.context, R.drawable.baseline_location_on_24)
                    )
                    val currentLocation = listener.currentLocation
                    if(currentLocation != null){
                        val distance = calculateDistanceInMeter(currentLocation.latitude, currentLocation.longitude, it.latitude, it.longitude)
                        binding.tvDistance.text = binding.root.context.getString(R.string.distance_in_meter)
                            .format(
                                "%.1f".format(distance)
                            )
                    }else{
                        binding.tvDistance.text = binding.root.context.getString(R.string.undefined)
                    }
                    
                    binding.ivHasVisited.isVisible = it.hasVisited
                    
                }
                
                binding.root.setOnClickListener { 
                    listener.onClick(store)
                }
            }
        }
        
        companion object{
            fun onCreate(parent: ViewGroup): ViewHolder{
                val itemView = StoreItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(itemView)
            }
        }
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Store>(){
            override fun areItemsTheSame(oldItem: Store, newItem: Store): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Store, newItem: Store): Boolean {
                return oldItem.storeId == newItem.storeId
            }
        }
    }
}

abstract class StoreListListener{
    abstract val currentLocation: Location?
    abstract fun onClick(store: Store)
}