package com.mantisbayne.storeprototype.data.local

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart WHERE id = :id LIMIT 1")
    suspend fun getCart(id: Int): Flow<CartEntity>

    @Query("UPDATE cart SET count = :count WHERE id = :id")
    suspend fun updateCartCount(id: Int, count: Int)
}