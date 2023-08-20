package dev.haqim.pitjarusapp.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.haqim.pitjarusapp.domain.model.Username
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserPreference @Inject constructor (
    private val dataStore: DataStore<Preferences>
){
    fun getUsername() = dataStore.data.map {preferences -> 
        preferences[USERNAME_KEY] ?: ""
    }

    fun hasLogin() = dataStore.data.map {preferences ->
        preferences[HAS_LOGIN_KEY] ?: false
    }
    
    suspend fun onLogin(username: Username = ""){
        dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = username
            preferences[HAS_LOGIN_KEY] = true
        }
    }

    suspend fun onLogout(username: Username = ""){
        dataStore.edit { preferences ->
            preferences[HAS_LOGIN_KEY] = false
        }
    }
    
    companion object{
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val HAS_LOGIN_KEY = booleanPreferencesKey("has_login")
    }
}
