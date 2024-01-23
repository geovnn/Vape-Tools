package com.geovnn.vapetools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.geovnn.vapetools.data.db.LiquidDatabase
import com.geovnn.vapetools.ui.navigation.Navigation
import com.geovnn.vapetools.ui.screens.saved_screen.SavedLiquidsViewModel
import com.geovnn.vapetools.ui.theme.VapeToolsTheme

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            LiquidDatabase::class.java,
            "liquids.db"
        ).build()
    }

    private val savedLiquidViewModel by viewModels<SavedLiquidsViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SavedLiquidsViewModel(db.dao) as T
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