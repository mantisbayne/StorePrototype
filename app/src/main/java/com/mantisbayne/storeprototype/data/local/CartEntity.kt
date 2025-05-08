package com.mantisbayne.storeprototype.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "count") val count: Int
)
