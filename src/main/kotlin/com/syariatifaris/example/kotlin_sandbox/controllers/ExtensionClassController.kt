package com.syariatifaris.example.kotlin_sandbox.controllers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Serializable data class ReqResData(val data: ReqResUser)

@Serializable
data class ReqResUser(
        val id: Int,
        @SerialName("first_name") val firstName: String,
        @SerialName("last_name") val lastName: String,
        val avatar: String
)

sealed class ReqResResult {
    data class Success(val data: ReqResData) : ReqResResult()
    data class Error(val message: String) : ReqResResult()
}

fun ReqResResult.getOrNull(): ReqResData? = if (this is ReqResResult.Success) data else null

fun ReqResResult.getOrDefault(default: ReqResData): ReqResData =
        if (this is ReqResResult.Success) data else default

fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }

@RestController
@RequestMapping("/extension_class")
class ExtensionClassController {
    @RequestMapping("/user/{id}")
    fun getUserByID(@PathVariable id: Int): ReqResData? {
        return fetchUserById(id).getOrNull()?.let { 
            it.copy(data = it.data.copy(firstName = it.data.firstName.capitalizeWords()))
        }
    }

    fun fetchUserById(id: Int): ReqResResult {
        val client = OkHttpClient()
        val request = Request.Builder().url("https://reqres.in/api/users/$id").build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                return ReqResResult.Error("Failed to fetch user: HTTP ${response.code}")
            }
            val json = response.body?.string() ?: return ReqResResult.Error("Empty response body")
            val jsonParser = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
            val parsed = jsonParser.decodeFromString<ReqResData>(json)
            return ReqResResult.Success(parsed)
        }
    }
}
