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
import org.springframework.kafka.core.KafkaTemplate
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.syariatifaris.example.kotlin_sandbox.entities.PokemonResult

@RestController
class HomeController(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    @GetMapping("/")
    fun index(): String{
        return runBlocking {
            val result = async {
                "john"
            }
            val result2 = async {
                "doe"
            }
            "${result.await()} ${result2.await()}"
        }
    }

    @GetMapping("/pokemon/{id}")
    fun getPokemonById(@PathVariable id: Int): PokemonRestResponse?{
        return runBlocking {
            val pokemon = fetchPokemon(OkHttpClient(), id)
            pokemon?.let{
                val message = jacksonObjectMapper().writeValueAsString(it)
                kafkaTemplate.send("pokemon_events", message)
            }
            pokemon
        }
    }

    @GetMapping("/pokemon/range")
    fun getPokemonByRange(@RequestParam start: Int, @RequestParam end: Int): List<PokemonRestResponse>{
        return runBlocking {
            val client = OkHttpClient()
            val deferredList = (start..end).map { id ->
                async {
                    fetchPokemon(client, id)
                }
            }
            deferredList.awaitAll().filterNotNull()
        }
    }

    fun testSomething(): List<PokemonRestResponse?>{
        return runBlocking {
            val result1 = async{
                fetchPokemon(OkHttpClient(), 1)
            }
            val result2 = async{
                fetchPokemon(OkHttpClient(), 2)
            }         
            listOf(result1.await(), result2.await())
        }
    }

    @GetMapping("/pokemon/x/{id}")
    fun getPokemonByIdX(@PathVariable id: Int): PokemonResult {
        return runBlocking {
            fetchPokemonX(OkHttpClient(), id)
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

    suspend fun fetchPokemonX(client: OkHttpClient, id: Int): PokemonResult {
        return try {
            val request = Request.Builder()
                .url("https://pokeapi.co/api/v2/pokemon/$id")
                .build()
            
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return PokemonResult.Error("Failed to fetch Pokemon: HTTP ${response.code}")
                }

                val json = response.body?.string() ?: return PokemonResult.Error("Empty response body")
                val jsonParser = Json {
                    ignoreUnknownKeys = true
                }
                val parsed = jsonParser.decodeFromString<PokemonRestResponse>(json)
                PokemonResult.Success(parsed)
            }
        } catch (e: Exception) {
            PokemonResult.Error("Error fetching Pokemon: ${e.message}")
        }
    }
}