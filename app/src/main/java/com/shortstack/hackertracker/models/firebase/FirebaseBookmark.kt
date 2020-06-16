package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import com.shortstack.hackertracker.models.FirebaseModel
import com.shortstack.hackertracker.models.local.Bookmark
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseBookmark(
    val id: String = "",
    val value: Boolean = false,
    override val hidden: Boolean = false
) : Parcelable, FirebaseModel<Bookmark> {

    override fun toLocal() = Bookmark(id, value)
}