package com.mantisbayne.storeprototype.viewmodel

sealed class ProductIntent {
    data object Refresh : ProductIntent()
    data class AddProduct(val id: Int) : ProductIntent()
}
