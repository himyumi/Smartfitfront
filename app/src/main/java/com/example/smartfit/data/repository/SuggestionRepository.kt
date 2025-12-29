package com.example.smartfit.data.repository

import com.example.smartfit.data.api.SuggestionApiClient
import com.example.smartfit.data.api.SuggestionDto

class SuggestionRepository {

    // Fetches the raw map from the API
    suspend fun getSuggestions(): Map<String, List<SuggestionDto>> {
        return SuggestionApiClient.service.getAllSuggestions()
    }
}