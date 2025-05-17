package com.syariatifaris.example.kotlin_sandbox.entities

import kotlinx.serialization.Serializable

@Serializable
data class LocationRestResponse(
    val id: Int,
    val name: String,
    val region: LocationRegion,
    val names: List<LocationName>,
    val areas: List<LocationArea>
)

@Serializable
data class LocationRegion(
    val name: String,
    val url: String
)

@Serializable
data class LocationName(
    val name: String,
    val language: LocationLanguage
)

@Serializable
data class LocationLanguage(
    val name: String,
    val url: String
)

@Serializable
data class LocationArea(
    val name: String,
    val url: String
)