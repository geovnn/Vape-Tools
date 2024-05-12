package com.geovnn.vapetools.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface LiquidDao {

    @Upsert
    suspend fun upsertLiquid(liquid: Liquid)

    @Delete
    suspend fun deleteLiquid(liquid: Liquid)

    @Query("SELECT * FROM liquid ORDER BY name ASC")
    fun getLiquidsOrderedByName(): Flow<List<Liquid>>
}