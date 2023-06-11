package com.advice.schedule.repository

import com.advice.data.sources.VendorsDataSource
import com.advice.data.sources.VillagesDataSource

class OrganizationsRepository(
    private val vendorsDataSource: VendorsDataSource,
    private val villagesDataSource: VillagesDataSource,
) {

    val vendors = vendorsDataSource.get()
    val villages = villagesDataSource.get()
}