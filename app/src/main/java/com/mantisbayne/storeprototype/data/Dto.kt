package com.mantisbayne.storeprototype.data

data class CartDto(
    val id: Int,
    val userId: Int,
    val date: String,
    val products: List<CartProduct>
)

data class CartProductDto(
    val productId: Int,
    val quantity: Int
)

data class ProductDto(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String
)

fun CartDto.toDomain(
    id: Int,
    userId: Int,
    date: String,
    products: List<CartProduct>
) = Cart(
    id, userId, date, products
)
fun CartProduct.toDomain(
    id: Int,
    quantity: Int
) = CartProduct(
    id, quantity
)
fun ProductDto.toDomain(
    id: Int,
    title: String,
    price: Double,
    description: String,
    category: String,
    image: String
) = Product(
    id, title, price, description, category, image
)

data class UserDto(
    val id: Int,
    val email: String,
    val username: String,
    val name: UserNameDto
)

data class UserNameDto(
    val firstname: String,
    val lastname: String
)

fun UserDto.toDomain(): User = User(
    id = id,
    email = email,
    username = username,
    name = name.toDomain()
)

fun UserNameDto.toDomain(): UserName = UserName(
    firstname = firstname,
    lastname = lastname
)
