package com.shortstack.hackertracker.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.shortstack.hackertracker.models.firebase.FirebaseType
import com.shortstack.hackertracker.models.local.Conference
import com.shortstack.hackertracker.models.local.Type


class ConferenceManager(private val conference: Conference) {

    private val firestore = FirebaseFirestore.getInstance()


    fun getConferenceTypes(): LiveData<List<Type>> {
        val result = MediatorLiveData<List<Type>>()

        firestore.collection(CONFERENCES)
            .document(conference.code)
            .collection(TYPES)
            .addSnapshotListener { snapshot, exception ->
                if (exception == null) {
                    val types = snapshot?.toObjects(FirebaseType::class.java)?.map { it.toLocal() }
                    result.postValue(types)
                }
            }

        return result
    }

    companion object {
        private const val CONFERENCES = "conferences"

        private const val USERS = "users"
        private const val BOOKMARKS = "bookmarks"

        private const val EVENTS = "events"
        private const val TYPES = "types"
        private const val FAQS = "faqs"
        private const val VENDORS = "vendors"
        private const val SPEAKERS = "speakers"
        private const val LOCATIONS = "locations"
        private const val ARTICLES = "articles"
    }
}