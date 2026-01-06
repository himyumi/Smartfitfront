package com.example.smartfit.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfit.data.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GoalsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsDataStore = SettingsDataStore(application)

    // Expose the goals as a StateFlow for the UI to observe
    val goals: StateFlow<Triple<Int, Int, Int>> = settingsDataStore.goalsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Triple(0, 0, 0)
        )

    fun saveGoals(steps: Int, calories: Int, water: Int) {
        viewModelScope.launch {
            settingsDataStore.saveGoals(steps, calories, water)
        }
    }
}
