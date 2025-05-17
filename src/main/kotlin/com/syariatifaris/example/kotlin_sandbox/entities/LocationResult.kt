package com.syariatifaris.example.kotlin_sandbox.entities

sealed class LocationResult {
    class SuccessOne(val location: LocationRestResponse) : LocationResult()
    class SuccessMany(val locations: List<LocationRestResponse>) : LocationResult()
    class Error(val message: String) : LocationResult()
}