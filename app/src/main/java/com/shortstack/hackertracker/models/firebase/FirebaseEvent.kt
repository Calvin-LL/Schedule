package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import com.shortstack.hackertracker.models.FirebaseModel
import com.shortstack.hackertracker.models.local.Event
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat

@Parcelize
data class FirebaseEvent(
    val id: Int = -1,
    val conference: String = "",
    val title: String = "",
    val description: String = "",
    val begin: String = "",
    val end: String = "",
    val link: String = "",
    val updated: String = "",
    val speakers: ArrayList<FirebaseSpeaker> = ArrayList(),
    val type: FirebaseType = FirebaseType(),
    val location: FirebaseLocation = FirebaseLocation(),
    override val hidden: Boolean = false
) : Parcelable, FirebaseModel<Event> {

    override fun toLocal() = Event(
        id,
        conference,
        title,
        description,
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(begin),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(end),
        link,
        updated,
        speakers.map { it.toLocal() },
        type.toLocal(),
        location.toLocal()
    )
}