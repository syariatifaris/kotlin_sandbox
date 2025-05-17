package com.syariatifaris.example.kotlin_sandbox.controllers

import com.syariatifaris.example.kotlin_sandbox.entities.LocationRestResponse
import com.syariatifaris.example.kotlin_sandbox.entities.LocationResult
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/locations")
class LocationController {
    @RequestMapping("/{id}")
    fun getById(@PathVariable id: Int): LocationResult{
        return runBlocking {
            pokeAPIGetLocationById(id)
        }
    }

    suspend fun pokeAPIGetLocationById(id: Int): LocationResult{
        val request = Request.Builder()
            .url("https://pokeapi.co/api/v2/location/$id")
            .build()

        val client = OkHttpClient()
        client.newCall(request).execute().use { response ->
            if(!response.isSuccessful){
                return LocationResult.Error("Failed to fetch location: HTTP ${response.code}")
            }
            val json = response.body?.string() ?: return LocationResult.Error("Empty response body")
            val jsonParser = kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            }
            val parsed = jsonParser.decodeFromString<LocationRestResponse>(json)
            return LocationResult.SuccessOne(parsed)
        }
    }
}