package com.shortstack.hackertracker.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.shortstack.hackertracker.models.FirebaseModel
import com.shortstack.hackertracker.models.local.Conference

fun FirebaseFirestore.listener(collection: String, listener: (QuerySnapshot?, Exception?) -> Unit) {
    collection("conferences")
        .document("defcon27")
        .collection(collection)
        .addSnapshotListener { snapshot, exception ->
            listener.invoke(snapshot, exception)
        }
}

inline fun <reified T : FirebaseModel<H>, H> FirebaseFirestore.getCollection(
    conference: Conference,
    collection: String
): LiveData<List<H>> {
    val result = MediatorLiveData<List<H>>()

    collection("conferences")
        .document(conference.code)
        .collection(collection)
        .addSnapshotListener { snapshot, exception ->
            if (exception == null) {
                result.postValue(snapshot.toResult<T, H>())
            }
        }

    return result
}

inline fun <reified T : FirebaseModel<H>, H> QuerySnapshot?.toResult(): List<H> {
    if (this == null) {
        return emptyList()
    }

    return toObjects(T::class.java)
        .filter { !it.hidden }
        .map { it.toLocal() }
}

inline fun <reified T : FirebaseModel<H>, H> FirebaseFirestore.getUserCollection(
    conference: Conference,
    user: String,
    collection: String
): LiveData<List<H>> {
    val result = MediatorLiveData<List<H>>()

    collection("conferences")
        .document(conference.code)
        .collection("users")
        .document(user)
        .collection(collection)
        .addSnapshotListener { snapshot, exception ->
            if (exception == null) {
                result.postValue(snapshot.toResult<T, H>())
            }
        }

    return result
}