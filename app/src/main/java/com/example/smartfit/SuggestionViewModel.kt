package com.example.smartfit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfit.data.api.SuggestionApiClient
import com.example.smartfit.data.api.SuggestionDto
import kotlinx.coroutines.launch

class SuggestionViewModel : ViewModel() {
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
                // 1. Fetch from API
                val response = SuggestionApiClient.service.getAllSuggestions()

                // 2. Map "Normal" -> "Normal weight" to match JSON keys
                val key = when {
                    bmiCategory.contains("Underweight", true) -> "Underweight"
                    bmiCategory.contains("Normal", true) -> "Normal"
                    bmiCategory.contains("Overweight", true) -> "Overweight"
                    bmiCategory.contains("Obese", true) -> "Obese"
                    else -> "Normal"
                }

                suggestions = response[key] ?: emptyList()
            } catch (e: Throwable) { // <--- CHANGED FROM Exception TO Throwable
                e.printStackTrace()
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}