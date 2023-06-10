package com.advice.firebase.datasource

import com.advice.core.local.Product
import com.advice.data.UserSession
import com.advice.data.datasource.ProductsDataSource
import com.advice.firebase.models.FirebaseProduct
import com.advice.firebase.snapshotFlow
import com.advice.firebase.toMerch
import com.advice.firebase.toObjectsOrEmpty
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

@OptIn(FlowPreview::class)
class FirebaseProductsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
) : ProductsDataSource {

    override fun get(): Flow<List<Product>> {
        return userSession.getConference().flatMapMerge { conference ->
            firestore.collection("conferences")
                .document(conference.code)
                .collection("products")
                .snapshotFlow()
                .map { querySnapshot ->
                    querySnapshot.toObjectsOrEmpty(FirebaseProduct::class.java)
                        .sortedBy { it.sort_order }
                        .mapNotNull { it.toMerch() }
                }
        }
    }
}