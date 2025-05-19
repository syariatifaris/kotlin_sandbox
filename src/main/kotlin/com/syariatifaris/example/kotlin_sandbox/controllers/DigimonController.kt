package com.syariatifaris.example.kotlin_sandbox.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.OkHttpClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/digimon")
class DigimonController {
    data class Digimon(val name: String, val img: String, val level:String)

    sealed class DigimonResult {
        data class Success(val digimon: Digimon) : DigimonResult()
        data class Error(val message: String) : DigimonResult()
    }

    private val client = OkHttpClient()
    private val mapper = jacksonObjectMapper()

    private fun safeApiCall(block:() -> Digimon): DigimonResult{
        return try{
            DigimonResult.Success(block())
        }catch (e:Exception){
            DigimonResult.Error(e.message ?: "Unknown error")
        }
    }
    
    @RequestMapping("/{name}")
    fun getDigimonByName(@PathVariable name: String): ResponseEntity<Any>{
        val result = safeApiCall {
            val request = okhttp3.Request.Builder()
                .url("https://digimon-api.vercel.app/api/digimon")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw RuntimeException("Failed to fetch Digimon: HTTP ${response.code}")
                }

                val body = response.body?.string() ?: throw RuntimeException("Empty response body")

                val list: List<Digimon> = mapper.readValue(body)
                if(list.isEmpty()){
                    throw RuntimeException("No Digimon found")
                }
                list.find { it.name.lowercase() == name } ?: throw RuntimeException("Digimon not found")
            }
        }
        return result.let {
            when(it){
                is DigimonResult.Success -> ResponseEntity.ok(it.digimon)
                is DigimonResult.Error -> ResponseEntity.badRequest().body(it.message)
            }
        }
    }
}