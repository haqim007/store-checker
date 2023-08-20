package dev.haqim.pitjarusapp.ui.login

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.haqim.pitjarusapp.BaseActivity
import dev.haqim.pitjarusapp.R
import dev.haqim.pitjarusapp.databinding.ActivityLoginBinding
import dev.haqim.pitjarusapp.domain.mechanism.Resource
import dev.haqim.pitjarusapp.domain.mechanism.handleCollect
import dev.haqim.pitjarusapp.domain.model.LoginStatus
import dev.haqim.pitjarusapp.domain.model.Username
import dev.haqim.pitjarusapp.ui.dashboard.DashboardActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupCheckbox()
        setupForm()
        bindStates()
    }
    
    private fun setupCheckbox(){
        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )

        val colors = intArrayOf(
            getColor(R.color.blue),
            getColor(R.color.blue)
        )
        
        binding.cbKeepUsername.buttonTintList = ColorStateList(states, colors)
    }
    
    private fun setupForm(){

        var username: String = binding.etUsername.text.toString()
        var password: String = binding.etPassword.text.toString()
        binding.etUsername.addTextChangedListener {
            if (it.toString() != username){
                viewModel.onUsernameChange(it.toString())
                username = it.toString()
            }
            
        }

        binding.etPassword.addTextChangedListener {
            if (it.toString() != password){
                viewModel.onPasswordChange(it.toString())
                password = it.toString()
            }

        }
        
        binding.btnLogin.setOnClickListener {
            val keepUsername = binding.cbKeepUsername.isChecked
            viewModel.login(username, password, keepUsername)
        }
        
        binding.btnCheckUpdate.setOnClickListener { 
            Toast.makeText(this, getString(R.string.no_update_available), Toast.LENGTH_LONG).show()
        }
    }
    
    private fun bindStates(){
        val uiState = viewModel.state

        bindLoginStatusState(
            uiState.map{it.loginStatus}
        )
        bindUsernameState(
            uiState.map { it.username }
        )
        bindFormState(
            uiState.map { it.usernameError },
            uiState.map { it.passwordError }
        )
    }
    
    private fun bindLoginStatusState(loginStatusState: Flow<Resource<LoginStatus>>){
        val loginStatus = loginStatusState.distinctUntilChanged()
        lifecycleScope.launch {
            loginStatus.handleCollect(
                onLoading = {
                    loadingDialog.show()
                },
                onSuccess = { loginStatus ->
                    loadingDialog.dismiss()

                    if(loginStatus is LoginStatus.Success){
                        startActivity(
                            Intent(this@LoginActivity, DashboardActivity::class.java)
                        )
                        finish()
                    }else if(loginStatus is LoginStatus.Failure){
                        messageDialog(loginStatus.message).show()
                    }
                    
                    
                },
                onError = {message, _ ->
                    loadingDialog.dismiss()
                    messageDialog(message ?: getString(R.string.an_error_occured)).show()
                }
            )
        }
    }
    
    private fun bindUsernameState(usernameState: Flow<Username?>){
        val username = usernameState.distinctUntilChanged()
        lifecycleScope.launch { 
            username.collectLatest { 
                binding.etUsername.setText(it)
                binding.cbKeepUsername.isChecked = it != null
            }
        }
    }
    
    private fun bindFormState(
        usernameErrorState: Flow<Int?>,
        passwordErrorState: Flow<Int?>
    ){
        val usernameError = usernameErrorState.distinctUntilChanged()
        val passwordError = passwordErrorState.distinctUntilChanged()
        val formState = combine(usernameError, passwordError, ::Pair).distinctUntilChanged()
        lifecycleScope.launch {
            formState.collect {(usernameErr, passwordErr) ->
                
                usernameErr?.let {
                    binding.tilUsername.error = getString(it)
                } ?: run{
                    binding.tilUsername.error = null
                }
                passwordErr?.let {
                    binding.tilPassword.error = getString(it)
                }?: run{
                    binding.tilPassword.error = null
                }
                
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}