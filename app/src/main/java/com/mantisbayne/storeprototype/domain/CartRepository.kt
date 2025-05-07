package com.mantisbayne.storeprototype.domain

import com.mantisbayne.storeprototype.data.local.CartDao
import com.mantisbayne.storeprototype.data.local.CartEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(private val cartDao: CartDao) : CartRepository {

    private val _cartFlow = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val cartFlow = _cartFlow.asStateFlow()

    override fun observeCart(): Flow<Map<Int, Int>> = cartFlow

    override suspend fun getCart(id: Int): CartEntity {
        withContext(Dispatchers.Default) {
            cartDao.getCart(id)
        }
    }

    override suspend fun updateCart(id: Int): CartEntity {
        withContext(Dispatchers.Default) {
            val cart = cartFlow.value
            cart[id]?.let { cartDao.updateCartCount(id, it) }
                ?: throw IllegalStateException("Unable to update cart")
        }
    }

    fun add(id: Int) {
        _cartFlow.update {
            it.toMutableMap().apply { merge(id, 1, Int::plus) }
        }
    }

    fun remove(id: Int) {
        _cartFlow.update { idToCount ->
            idToCount.toMutableMap().apply {
                val cart = cartFlow.value.toMutableMap()
                cart[id]?.let {
                    val count = it - 1
                    cart[id] = count
                    if (count < 1) cart.remove(id)
                } ?: throw IllegalStateException("Unable to remove item")
            }
        }
    }
}

interface CartRepository {
    fun observeCart(): Flow<Map<Int, Int>>

    suspend fun getCart(id: Int): CartEntity

    suspend fun updateCart(id: Int): CartEntity
}
