package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import com.shortstack.hackertracker.models.FirebaseModel
import com.shortstack.hackertracker.models.local.Vendor
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseVendor(
    val id: Int = -1,
    val name: String = "",
    val description: String? = null,
    val link: String? = null,
    val partner: Boolean = false,
    val updatedAt: String = "",
    val conference: String = "",
    override val hidden: Boolean = false
) : Parcelable, FirebaseModel<Vendor> {

    override fun toLocal() = Vendor(
        id,
        name,
        description,
        link,
        partner
    )
}

