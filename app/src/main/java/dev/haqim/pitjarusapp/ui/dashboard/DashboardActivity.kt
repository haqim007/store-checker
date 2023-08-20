package dev.haqim.pitjarusapp.ui.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.haqim.pitjarusapp.R
import dev.haqim.pitjarusapp.databinding.ActivityDashboardBinding
import dev.haqim.pitjarusapp.ui.login.LoginActivity
import dev.haqim.pitjarusapp.ui.storelist.StoreListActivity
import dev.haqim.pitjarusapp.util.getCurrentMonthNameAndYear
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.tvVisitTitle.text = getString(R.string.visit_on_month_).format(
            getCurrentMonthNameAndYear()
        )
        
        bindStates()
        
        binding.llMenuVisit.setOnClickListener { 
            startActivity(
                Intent(this, StoreListActivity::class.java)
            )
        }
        binding.llMenuLogout.setOnClickListener { 
            viewModel.logout()
        }
    }
    
    private fun bindStates(){
        val state = viewModel.state
        bindLogoutState(state.map { it.hasLogin })
    }
    
    private fun bindLogoutState(logoutState: Flow<Boolean?>){
        val state = logoutState.distinctUntilChanged()
        lifecycleScope.launch { 
            state.collect{
                if(it == false){
                    startActivity(
                        Intent(this@DashboardActivity, LoginActivity::class.java)
                    )
                    finish()
                }
            }
        }
    }
}