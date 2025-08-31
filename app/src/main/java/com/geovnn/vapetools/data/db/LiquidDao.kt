package com.geovnn.vapetools.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface LiquidDao {

    @Upsert
    suspend fun upsertLiquid(liquid: LiquidDto)

    @Delete
    suspend fun deleteLiquid(liquid: LiquidDto)

    @Query("SELECT * FROM liquiddto ORDER BY name ASC")
    fun getLiquidsOrderedByName(): Flow<List<LiquidDto>>

    @Query("SELECT * FROM liquiddto WHERE id = :id")
    suspend fun getLiquidById(id: Int): LiquidDto?
}