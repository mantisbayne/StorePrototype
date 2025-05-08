package com.mantisbayne.storeprototype.domain

import com.mantisbayne.storeprototype.data.local.CartDao
import com.mantisbayne.storeprototype.data.local.CartEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(private val cartDao: CartDao) : CartRepository {

    override fun observeCart(): Flow<Map<Int, Int>> =
        cartDao.observeCart().map {
            it.associate { cartItem ->
                cartItem.id to cartItem.count
            }
        }

    override suspend fun getCart(): List<CartEntity> {
        return cartDao.getCart()
    }

    override suspend fun getCartItem(productId: Int): CartEntity? {
        return cartDao.getCartItem(productId)
    }

    override suspend fun updateCartItemCount(productId: Int, newCount: Int) {
        val cartItem = cartDao.getCartItem(productId)

        when {
            cartItem != null -> {
                if (newCount < 1) {
                    cartDao.deleteCartItem(productId)
                } else {
                    cartDao.updateCartItemCount(productId, newCount)
                }
            }
            else -> cartDao.insert(CartEntity(productId, 1))
        }
    }

    override suspend fun deleteCartItem(productId: Int) {
        cartDao.deleteCartItem(productId)
    }

}

interface CartRepository {
    fun observeCart(): Flow<Map<Int, Int>>

    suspend fun getCart(): List<CartEntity>

    suspend fun getCartItem(productId: Int): CartEntity?

    suspend fun updateCartItemCount(productId: Int, newCount: Int)

    suspend fun deleteCartItem(productId: Int)
}
