package com.mantisbayne.storeprototype.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    // Update when the cart is changed
    @Query("SELECT * FROM cart")
    fun observeCart(): Flow<List<CartEntity>>

    @Query("SELECT * FROM cart")
    suspend fun getCart(): List<CartEntity>

    @Query("SELECT * FROM cart WHERE id = :id")
    suspend fun getCartItem(id: Int): CartEntity?

    @Query("UPDATE cart SET count = :count WHERE id = :id")
    suspend fun updateCartItemCount(id: Int, count: Int)

    @Query("DELETE FROM cart WHERE id = :id")
    suspend fun deleteCartItem(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cartItem: CartEntity)
}
