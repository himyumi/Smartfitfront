package com.example.smartfit.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create a single instance of the DataStore
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

// Keys for our values
object SettingsKeys {
    val STEPS_GOAL = intPreferencesKey("steps_goal")
    val CALORIES_GOAL = intPreferencesKey("calories_goal")
    val WATER_GOAL = intPreferencesKey("water_goal")
}

// The class that will interact with the DataStore
class SettingsDataStore(private val context: Context) {

    // Read all goals as a Flow
    val goalsFlow: Flow<Triple<Int, Int, Int>> = context.settingsDataStore.data
        .map {
            preferences ->
            val steps = preferences[SettingsKeys.STEPS_GOAL] ?: 0
            val calories = preferences[SettingsKeys.CALORIES_GOAL] ?: 0
            val water = preferences[SettingsKeys.WATER_GOAL] ?: 0
            Triple(steps, calories, water)
        }

    // Save goals
    suspend fun saveGoals(steps: Int, calories: Int, water: Int) {
        context.settingsDataStore.edit {
            settings ->
            settings[SettingsKeys.STEPS_GOAL] = steps
            settings[SettingsKeys.CALORIES_GOAL] = calories
            settings[SettingsKeys.WATER_GOAL] = water
        }
    }
}
