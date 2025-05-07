package com.mantisbayne.storeprototype.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CartEntity::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun cartDao(): CartDao
}