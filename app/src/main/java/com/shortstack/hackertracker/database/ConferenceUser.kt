package com.shortstack.hackertracker.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.shortstack.hackertracker.models.local.Conference
import com.shortstack.hackertracker.models.local.Event
import kotlinx.coroutines.tasks.await

class ConferenceUser(private val auth: FirebaseAuth, private val firestore: FirebaseFirestore) {

    private var user: FirebaseUser? = null

    suspend fun init() {
        val result = auth.signInAnonymously()
            .await()

        user = result.user
    }

    // todo: do we need to store this token?
    fun setToken(conference: Conference, token: String) {
        val id = user?.uid ?: return

        val document = firestore.collection("conferences")
            .document(conference.code)
            .collection("users")
            .document(id)

        document.set(mapOf("token" to token))
    }


    // todo: replace this with a network request
    fun bookmark(event: Event) {
        val uid = user?.uid ?: return

        val document = firestore.collection("conferences")
            .document("defcon27")
            .collection("users")
            .document(uid)
            .collection("bookmarks")
            .document(event.id.toString())

        if (event.isBookmarked) {
            document.set(
                mapOf(
                    "id" to event.id.toString(),
                    "value" to true
                )
            )
        } else {
            document.delete()
        }
    }


}