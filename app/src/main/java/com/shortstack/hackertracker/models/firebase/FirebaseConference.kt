package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.shortstack.hackertracker.models.FirebaseModel
import com.shortstack.hackertracker.models.local.Conference
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class FirebaseConference(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val codeofconduct: String? = null,
    val code: String = "",
    val maps: ArrayList<FirebaseMap> = ArrayList(),
    val start_date: String = "",
    val end_date: String = "",
    val start_timestamp: Timestamp = Timestamp(Date()),
    val end_timestamp: Timestamp = Timestamp(Date()),
    val timezone: String = "",
    override val hidden: Boolean = false
) : Parcelable, FirebaseModel<Conference> {

    override fun toLocal() = Conference(
        id,
        name,
        description,
        codeofconduct,
        code,
        maps,
        start_timestamp.toDate(),
        end_timestamp.toDate(),
        timezone
    )
}