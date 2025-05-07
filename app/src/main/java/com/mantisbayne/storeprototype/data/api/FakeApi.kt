package com.mantisbayne.storeprototype.data.api

import com.mantisbayne.storeprototype.data.ProductDto

class FakeProductApi : ProductApi {
    override suspend fun getAllProducts(): List<ProductDto> = listOf(
        ProductDto(
            id = 1,
            title = "Mock T-Shirt",
            price = 19.99,
            image = "https://example.com/mock-tshirt.jpg",
            description = "A fake product for testing",
            category = "clothing"
        ),
        ProductDto(
            id = 2,
            title = "Mock Watch",
            price = 99.99,
            image = "https://example.com/mock-watch.jpg",
            description = "Another fake item",
            category = "accessories"
        )
    )

    override suspend fun getProduct(id: Int): ProductDto =
        getAllProducts().first { it.id == id }
}