package com.mantisbayne.storeprototype.viewmodel

sealed class ProductIntent {
    data class UpdateProduct(val id: Int, val shouldAdd: Boolean) : ProductIntent()
}
