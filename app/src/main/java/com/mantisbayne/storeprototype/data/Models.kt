package com.mantisbayne.storeprototype.data

data class Cart(
    val id: Int,
    val userId: Int,
    val date: String,
    val products: List<CartProduct>
)

data class CartProduct(
    val productId: Int,
    val quantity: Int
)

data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String
)

data class User(
    val id: Int,
    val email: String,
    val username: String,
    val name: UserName
)

data class UserName(
    val firstname: String,
    val lastname: String
)
