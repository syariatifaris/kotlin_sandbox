package com.syariatifaris.example.kotlin_sandbox.entities

import kotlinx.serialization.Serializable

@Serializable
data class PokemonRestResponse(
    val id: Int,
    val name: String,
    val weight: Int,
    val height: Int
)