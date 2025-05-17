package com.syariatifaris.example.kotlin_sandbox.entities

sealed class PokemonResult{
    data class Success(val pokemon: PokemonRestResponse) : PokemonResult()
    data class Error(val message: String) : PokemonResult()
}