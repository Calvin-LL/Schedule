package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductVariant(
    val label: String,
    val tags: List<Long>,
    val extraCost: Long,
) : Parcelable

data class ProductSelection(
    val id: Long,
    val quantity: Int,
    val selectionOption: String?,
)

@Parcelize
data class ProductMedia(
    val url: String,
    val sortOrder: Int,
) : Parcelable

// in-cart Product
@Parcelize
data class Product(
    val id: Long,
    val label: String,
    val baseCost: Long,
    val variants: List<ProductVariant>,
    val media: List<ProductMedia>,
    val quantity: Int = 0,
    val cost: Long = baseCost * quantity,
    val selectedOption: String? = null,
) : Parcelable {

    val requiresSelection: Boolean
        get() = variants.size > 1

    fun update(
        selection: ProductSelection,
    ): Product {
        val extraCost = if (selection.selectionOption != null) {
            variants.find { it.label == selection.selectionOption }?.extraCost ?: 0
        } else {
            0
        }

        val cost = (baseCost + extraCost) * selection.quantity
        return copy(
            quantity = selection.quantity,
            selectedOption = selection.selectionOption,
            cost = cost,
        )
    }
}
