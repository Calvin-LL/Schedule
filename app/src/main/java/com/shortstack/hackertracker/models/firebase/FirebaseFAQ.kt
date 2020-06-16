package com.shortstack.hackertracker.models.firebase

import com.shortstack.hackertracker.models.FirebaseModel
import com.shortstack.hackertracker.models.local.FAQ

data class FirebaseFAQ(
    val id: Int = -1,
    val conference: String = "",
    val question: String = "",
    val answer: String = "",
    override val hidden: Boolean = false
) : FirebaseModel<FAQ> {

    override fun toLocal() = FAQ(
        id,
        question,
        answer
    )
}