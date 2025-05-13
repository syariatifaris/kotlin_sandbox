package com.syariatifaris.example.kotlin_sandbox.controllers

import com.syariatifaris.example.kotlin_sandbox.entities.PokemonRestResponse
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlinx.coroutines.*

@RestController
class HomeController {
    @GetMapping("/")
    fun index(): String{
        runBlocking {
            coroutineScope {
                launch {
                    "john"
                }
            }
        }
        return "It Works!"
    }

    @GetMapping("/pokemon/{id}")
    fun getPokemonById(@PathVariable id: Int): PokemonRestResponse?{
        return runBlocking {
            fetchPokemon(OkHttpClient(), id)
        }
    }

    @GetMapping("/pokemon/range")
    fun getPokemonByRange(@RequestParam start: Int, @RequestParam end: Int): List<PokemonRestResponse>{
        return runBlocking {
            val client = OkHttpClient()
            val deferredList = (start..end).map { id ->
                CoroutineScope(Dispatchers.IO).async {
                    fetchPokemon(client, id)
                }
            }
            deferredList.awaitAll().filterNotNull()
        }
    }

    suspend fun fetchPokemon(client: OkHttpClient, id: Int): PokemonRestResponse?{
        val request = Request.Builder()
            .url("https://pokeapi.co/api/v2/pokemon/$id")
            .build()
        client.newCall(request).execute().use { response ->
            if(!response.isSuccessful){
                return null
            }

            val json = response.body?.string() ?: return null
            val jsonParser = Json {
                ignoreUnknownKeys = true
            }
            val parsed = jsonParser.decodeFromString<PokemonRestResponse>(json)
            return parsed
        }
    }
}