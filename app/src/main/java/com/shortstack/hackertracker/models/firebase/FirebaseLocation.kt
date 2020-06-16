package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import com.shortstack.hackertracker.models.FirebaseModel
import com.shortstack.hackertracker.models.local.Location
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseLocation(
    val name: String = "",
    val hotel: String? = null,
    val conference: String = "",
    override val hidden: Boolean = false
) : Parcelable, FirebaseModel<Location> {

    override fun toLocal() = Location(
        name,
        hotel,
        conference
    )
}