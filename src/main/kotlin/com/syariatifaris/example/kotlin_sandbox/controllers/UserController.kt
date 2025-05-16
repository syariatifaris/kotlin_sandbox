package com.syariatifaris.example.kotlin_sandbox.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import kotlinx.coroutines.*
import org.springframework.web.bind.annotation.PathVariable

@RestController
class UserController {
    @GetMapping("/users")
    fun getUsers(): List<String> {
        return runBlocking {
            val users = listOf("John", "Jane", "Bob", "Alice")
            val deferredUsers = users.map { user ->
                async {
                    // Simulate some processing time
                    delay(100)
                    user.uppercase()
                }
            }
            deferredUsers.awaitAll()
        }
    }

    @GetMapping("/users/count")
    fun getUserCount(): Map<String, Int> {
        return runBlocking {
            val count = async {
                // Simulate database query
                delay(200)
                4
            }
            mapOf("count" to count.await())
        }
    }
}