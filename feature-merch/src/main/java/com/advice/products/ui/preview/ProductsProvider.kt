package com.advice.products.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.StockStatus
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductMedia
import com.advice.core.local.products.ProductVariant
import com.advice.products.presentation.state.ProductsState
import com.advice.products.utils.toJson

class ProductsProvider : PreviewParameterProvider<ProductsState> {
    override val values: Sequence<ProductsState>
        get() {
            val options = listOf(
                ProductVariant(1,"S", emptyList(), 0, StockStatus.IN_STOCK),
                ProductVariant(2,"4XL", emptyList(), 0, StockStatus.LOW_STOCK),
                ProductVariant(3, "5XL", emptyList(), 1000, StockStatus.OUT_OF_STOCK)
            )
            val product = Product(
                -1L, "DC30 Homecoming Men's T-Shirt", 35_00, options,
                media = listOf(
                    ProductMedia(
                        "https://htem2.habemusconferencing.net/temp/dc24front.jpg",
                        0
                    )
                ),
                quantity = 1,
                selectedOption = options[0].label,
            )

            val elements = listOf(product)

            return listOf(
                ProductsState(
                    elements = elements,
                    cart = elements,
                    json = elements.toJson(),
                )
            ).asSequence()
        }
}
