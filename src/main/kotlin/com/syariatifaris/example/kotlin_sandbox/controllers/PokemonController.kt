package com.syariatifaris.example.kotlin_sandbox.controllers

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import kotlinx.serialization.Serializable

@Serializable
data class Pokemon(
    val id: Int,
    val name: String,
    val weight: Int,
    val height: Int
)

sealed class PokemonOperationResult {
    data class Success(val pokemon: Pokemon) : PokemonOperationResult()
    data class Error(val message: String) : PokemonOperationResult()
}

@RestController
class PokemonController {
    @GetMapping("/pokemonx/{id}")
    fun getPokemonById(@PathVariable id: Int): PokemonOperationResult {
        return fetchPokemon(id)
    }

    fun fetchPokemon(id: Int): PokemonOperationResult {
        val request = Request.Builder()
            .url("https://pokeapi.co/api/v2/pokemon/$id")
            .build()

        OkHttpClient().newCall(request).execute().use { response ->
            if(!response.isSuccessful){
                return PokemonOperationResult.Error("Failed to fetch Pokemon: HTTP ${response.code}")
            }
            val jsonParser = Json{
                ignoreUnknownKeys = true
            }
            val parsed = jsonParser.decodeFromString<Pokemon>(response.body?.string() ?: "")
            return PokemonOperationResult.Success(parsed)
        }
    }
}