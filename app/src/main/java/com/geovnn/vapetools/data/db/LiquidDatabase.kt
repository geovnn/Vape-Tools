package com.geovnn.vapetools.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Liquid::class],
    version = 1
)
abstract class LiquidDatabase: RoomDatabase() {
    abstract val dao: LiquidDao
}