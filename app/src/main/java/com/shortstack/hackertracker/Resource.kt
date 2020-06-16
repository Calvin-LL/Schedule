package com.shortstack.hackertracker

sealed class Resource<out T> {

    // todo: possibly remove this state
    object Init : Resource<Nothing>()

    object Loading : Resource<Nothing>()

    class Success<out T>(val data: T) : Resource<T>()

    class Failure(val exception: Exception) : Resource<Nothing>()

}