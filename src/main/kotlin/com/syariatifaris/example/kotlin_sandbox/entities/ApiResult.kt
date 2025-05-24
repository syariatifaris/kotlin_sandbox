package com.syariatifaris.example.kotlin_sandbox.entities

sealed class ApiResult<T> {
    data class Success<T>(val data: T): ApiResult<T>()
    data class Error<T>(val message: String): ApiResult<T>()
}

fun <T> ApiResult<T>.getOrNull(): T? = if (this is ApiResult.Success) data else null
fun <T> ApiResult<T>.getOrDefault(default: T): T = if (this is ApiResult.Success) data else default