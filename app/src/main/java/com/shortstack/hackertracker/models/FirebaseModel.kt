package com.shortstack.hackertracker.models

interface FirebaseModel<T> {
    fun toLocal(): T
    val hidden: Boolean
}