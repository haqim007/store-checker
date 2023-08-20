package dev.haqim.pitjarusapp.data.preference

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "user preference")