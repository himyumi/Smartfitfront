package com.example.smartfit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfit.data.api.SuggestionDto
import com.example.smartfit.data.repository.SuggestionRepository // Import your new Repo
import kotlinx.coroutines.launch

class SuggestionViewModel : ViewModel() {

    // 1. Initialize the Repository
    private val repository = SuggestionRepository()

    var suggestions by mutableStateOf<List<SuggestionDto>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchSuggestions(bmiCategory: String) {
        if (bmiCategory.isEmpty()) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // 2. USE THE REPOSITORY HERE (Instead of SuggestionApiClient directly)
                val responseMap = repository.getSuggestions()

                // Map the category logic
                val key = mapBmiToJsonKey(bmiCategory)
                suggestions = responseMap[key] ?: emptyList()

            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "Failed to load suggestions. Check connection."
            } finally {
                isLoading = false
            }
        }
    }

    private fun mapBmiToJsonKey(appCategory: String): String {
        return when {
            appCategory.contains("Underweight", true) -> "Underweight"
            appCategory.contains("Normal", true) -> "Normal"
            appCategory.contains("Overweight", true) -> "Overweight"
            appCategory.contains("Obese", true) -> "Obese"
            else -> "Normal"
        }
    }
}