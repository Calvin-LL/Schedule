package com.advice.products.presentation.state

import com.advice.core.local.Product

data class ProductsState(
    val elements: List<Product>,
    val cart: List<Product> = emptyList(),
    val hasDiscount: Boolean = false,
    val json: String? = null,
)