package com.shortstack.hackertracker.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlin.coroutines.suspendCoroutine

class ConferenceUser(private val auth: FirebaseAuth) {

    suspend fun init() = suspendCoroutine<FirebaseUser> { cont ->
        auth.signInAnonymously().addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result?.user
                if (user != null) {
                    cont.resumeWith(Result.success(user))
                    return@addOnCompleteListener
                }
            }
            
            val exception = it.exception
            if (exception != null) {
                cont.resumeWith(Result.failure(exception))
            }
        }
    }
}