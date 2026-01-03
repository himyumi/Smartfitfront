package com.example.smartfit.data.repository

import com.example.smartfit.data.api.SuggestionApiClient
import com.example.smartfit.data.api.SuggestionDto

class SuggestionRepository {
    // The repository handles WHERE the data comes from (API)
    suspend fun getSuggestions(): Map<String, List<SuggestionDto>> {
        return SuggestionApiClient.service.getAllSuggestions()
    }
}