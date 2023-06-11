package com.advice.schedule.repository

import com.advice.data.sources.FAQDataSource


class FAQRepository(private val faqDataSource: FAQDataSource) {

    val faqs = faqDataSource.get()

}