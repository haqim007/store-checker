package dev.haqim.pitjarusapp.ui.visit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dev.haqim.pitjarusapp.R
import dev.haqim.pitjarusapp.databinding.ActivityVisitBinding
import dev.haqim.pitjarusapp.domain.model.Store
import dev.haqim.pitjarusapp.ui.storelist.StoreListActivity
import dev.haqim.pitjarusapp.ui.tagstore.TagStoreActivity

@AndroidEntryPoint
class VisitStoreActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityVisitBinding
    private val viewModel: VisitStoreViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storeDetail: Store? = intent.getParcelableExtra(VISIT_STORE_DETAIL_KEY)
        binding.btnSave.setOnClickListener {
            if (storeDetail != null) {
                viewModel.saveVisit(storeDetail)
                val intent = Intent(this, StoreListActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }
        if (storeDetail != null) {
            bindDetail(storeDetail)
        }
    }
    
    private fun bindDetail(store: Store){
        Glide.with(this).load(store.picture)
            .placeholder(R.drawable.image_broken)
            .into(binding.ivStorePict)
        binding.tvStoreCode.text = store.storeCode
        binding.tvStoreName.text = store.storeName
    }
    
    companion object{
        const val VISIT_STORE_DETAIL_KEY = "visit store detail key"
    }
}