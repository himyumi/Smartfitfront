package com.example.smartfit.data

import android.content.Context
import androidx.compose.ui.input.key.type
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.appPrefs by preferencesDataStore("smartfit_prefs")

class PreferenceDataStore(private val context: Context) {

    companion object {
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
        private val BMI_CATEGORY = stringPreferencesKey("bmi_category")

        private val STEPS_GOAL = intPreferencesKey("steps_goal")
        private val CAL_GOAL = intPreferencesKey("calories_goal")
        private val WATER_GOAL = intPreferencesKey("water_goal")
    }

    // ---------- THEME ----------
    suspend fun saveTheme(isDark: Boolean) {
        context.appPrefs.edit { it[DARK_MODE] = isDark }
    }

    val themeFlow: Flow<Boolean> =
        context.appPrefs.data.map { it[DARK_MODE] ?: false }

    // ---------- BMI ----------
    suspend fun saveBmi(category: String) {
        context.appPrefs.edit { it[BMI_CATEGORY] = category }
    }

    val bmiFlow: Flow<String> =
        context.appPrefs.data.map { it[BMI_CATEGORY] ?: "" }

    // ---------- GOALS ----------
    suspend fun saveGoals(steps: Int, calories: Int, water: Int) {
        context.appPrefs.edit {
            it[STEPS_GOAL] = steps
            it[CAL_GOAL] = calories
            it[WATER_GOAL] = water
        }
    }

    val goalsFlow: Flow<Triple<Int, Int, Int>> =
        context.appPrefs.data.map {
            Triple(
                it[STEPS_GOAL] ?: 0,
                it[CAL_GOAL] ?: 0,
                it[WATER_GOAL] ?: 0
            )
        }
}

