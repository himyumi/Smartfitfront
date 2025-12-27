package com.example.smartfit.data.api


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// 1. The Data Model
data class SuggestionDto(
    val title: String,
    val description: String,
    val icon: String
)

// 2. The Connection Interface
interface SuggestionApiService {
    @GET("https://gist.githubusercontent.com/himyumi/7671601b0b3004b885fb981d8e9db55c/raw/35936d7d0b8fd2cc2e7d333516aad411a9ac6f3a/gistfile1.txt")
    suspend fun getAllSuggestions(): Map<String, List<SuggestionDto>>
}

// 3. The Builder
object SuggestionApiClient {
    private const val BASE_URL = "https://gist.githubusercontent.com/"

    val service: SuggestionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SuggestionApiService::class.java)
    }
}