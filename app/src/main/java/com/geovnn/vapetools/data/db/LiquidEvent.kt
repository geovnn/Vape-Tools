package com.geovnn.vapetools.data.db

sealed interface LiquidEvent {
    object SaveLiquid: LiquidEvent
    data class SortLiquids(val sortType: SortType): LiquidEvent
    data class DeleteLiquid(val liquid: LiquidDto) : LiquidEvent
}