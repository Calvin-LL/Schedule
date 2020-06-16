package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import com.shortstack.hackertracker.models.FirebaseModel
import com.shortstack.hackertracker.models.local.Type
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseType(
    val id: Int = -1,
    val name: String = "",
    val conference: String = "",
    val color: String = "#343434",
    override val hidden: Boolean = false
) : Parcelable, FirebaseModel<Type> {

    override fun toLocal() = Type(
        id,
        name,
        conference,
        color
    )
}