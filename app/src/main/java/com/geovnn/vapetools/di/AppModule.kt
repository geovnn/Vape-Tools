package com.geovnn.vapetools.di

import androidx.room.Room
import com.geovnn.vapetools.data.db.LiquidDatabase
import com.geovnn.vapetools.data.repository.LiquidRepository
import com.geovnn.vapetools.ui.screen.coil_calculator.viewmodel.CoilViewModel
import com.geovnn.vapetools.ui.screen.liquid_calculator.viewmodel.LiquidViewModel
import com.geovnn.vapetools.ui.screen.nicotine_blender.viewmodel.NicotineViewModel
import com.geovnn.vapetools.ui.screen.ohm_calculator.viewmodel.OhmViewModel
import com.geovnn.vapetools.ui.screen.saved_screen.viewmodel.SavedLiquidsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            LiquidDatabase::class.java,
            "liquids.db"
        ).build()
    }

    // DAO
    single {
        get<LiquidDatabase>().dao
    }

    // Repository
    single {
        LiquidRepository(
            liquidDao = get(),
            context = androidContext()
        )
    }
    singleOf(::LiquidRepository)
    viewModelOf(::LiquidViewModel)
    viewModelOf(::CoilViewModel)
    viewModelOf(::NicotineViewModel)
    viewModelOf(::OhmViewModel)
    viewModelOf(::SavedLiquidsViewModel)
}