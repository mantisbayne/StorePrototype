package com.mantisbayne.storeprototype.data.local

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CartDao {

    @Query("SELECT * FROM cart WHERE id = :id LIMIT 1")
    fun getCart(id: Int)

    @Query("UPDATE cart SET count = :count WHERE id = :id")
    fun updateCartCount(id: Int, count: Int)
}