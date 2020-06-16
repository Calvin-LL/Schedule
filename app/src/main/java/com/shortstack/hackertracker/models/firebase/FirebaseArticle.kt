package com.shortstack.hackertracker.models.firebase

import com.shortstack.hackertracker.models.FirebaseModel
import com.shortstack.hackertracker.models.local.Article

data class FirebaseArticle(
    val id: Int = -1,
    val name: String = "",
    val text: String = "",
    override val hidden: Boolean = false
) : FirebaseModel<Article> {

    override fun toLocal() = Article(
        id,
        name,
        text
    )
}