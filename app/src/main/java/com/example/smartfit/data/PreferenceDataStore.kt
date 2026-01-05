package com.example.smartfit.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.smartfit.ActivityItem // Make sure this is imported
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.appPrefs by preferencesDataStore("smartfit_prefs")

class PreferenceDataStore(private val context: Context) {

    private val gson = Gson()

    companion object {
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
        private val BMI_CATEGORY = stringPreferencesKey("bmi_category")

        private val STEPS_GOAL = intPreferencesKey("steps_goal")
        private val CAL_GOAL = intPreferencesKey("calories_goal")
        private val WATER_GOAL = intPreferencesKey("water_goal")

        private val SAVED_ACTIVITIES = stringPreferencesKey("saved_activities")
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


    // ---------- NEW: ACTIVITIES ----------

    // 1. Save List (Convert List -> JSON String)
    suspend fun saveActivities(activities: List<ActivityItem>) {
        val jsonString = gson.toJson(activities)
        context.appPrefs.edit { it[SAVED_ACTIVITIES] = jsonString }
    }


    // 2. Get List (Convert JSON String -> List)
    val activitiesFlow: Flow<List<ActivityItem>> = context.appPrefs.data.map { preferences ->
        val jsonString = preferences[SAVED_ACTIVITIES] ?: ""
        if (jsonString.isNotEmpty()) {
            val type = object : TypeToken<List<ActivityItem>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            emptyList()
        }
    }
    suspend fun clearBmi() {
        context.appPrefs.edit {
            it.remove(BMI_CATEGORY)
        }
    }
}



