package com.geovnn.vapetools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.geovnn.vapetools.data.db.LiquidDatabase
import com.geovnn.vapetools.data.repository.LiquidRepository
import com.geovnn.vapetools.ui.navigation.Navigation
import com.geovnn.vapetools.ui.screen.saved_screen.viewmodel.SavedLiquidsViewModel
import com.geovnn.vapetools.ui.theme.VapeToolsTheme

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            LiquidDatabase::class.java,
            "liquids.db"
        ).build()
    }

    // Crea un'istanza del repository
    private val liquidRepository by lazy {
        LiquidRepository(
            liquidDao = db.dao,
            context = applicationContext
        )
    }


    private val savedLiquidViewModel by viewModels<SavedLiquidsViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    // Inietta il repository nel ViewModel
                    if (modelClass.isAssignableFrom(SavedLiquidsViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return SavedLiquidsViewModel(liquidRepository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VapeToolsTheme {
                Navigation(savedLiquidViewModel)
            }
        }
    }
}