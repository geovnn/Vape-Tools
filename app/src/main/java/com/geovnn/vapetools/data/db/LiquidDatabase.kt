package com.geovnn.vapetools.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameTable
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec

@Database(
    entities = [LiquidDto::class],
    version = 2,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
            spec = LiquidAutoMigrationSpec::class
        )
    ],
    exportSchema = true
)
abstract class LiquidDatabase : RoomDatabase() {
    abstract val dao: LiquidDao
}

@RenameTable.Entries(
    RenameTable(
        fromTableName = "Liquid",
        toTableName = "LiquidDto"
    )
)
class LiquidAutoMigrationSpec : AutoMigrationSpec