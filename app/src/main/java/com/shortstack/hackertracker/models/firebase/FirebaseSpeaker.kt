package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import com.shortstack.hackertracker.models.FirebaseModel
import com.shortstack.hackertracker.models.local.Speaker
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseSpeaker(
    val id: Int = -1,
    val name: String = "",
    val description: String = "",
    val link: String = "",
    val twitter: String = "",
    val title: String = "",
    override val hidden: Boolean = false
) : Parcelable, FirebaseModel<Speaker> {

    override fun toLocal() = Speaker(
        id,
        name,
        description,
        link,
        twitter,
        title
    )
}