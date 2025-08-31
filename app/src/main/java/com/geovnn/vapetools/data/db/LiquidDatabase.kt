package com.geovnn.vapetools.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [LiquidDto::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ],
    exportSchema = true
)
abstract class LiquidDatabase: RoomDatabase() {
    abstract val dao: LiquidDao
}