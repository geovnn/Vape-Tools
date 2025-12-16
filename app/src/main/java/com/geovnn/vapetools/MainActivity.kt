package com.geovnn.vapetools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.geovnn.vapetools.data.db.LiquidDatabase
import com.geovnn.vapetools.data.repository.LiquidRepository
import com.geovnn.vapetools.di.appModule
import com.geovnn.vapetools.ui.navigation.Navigation
import com.geovnn.vapetools.ui.screen.saved_screen.viewmodel.SavedLiquidsViewModel
import com.geovnn.vapetools.ui.theme.VapeToolsTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            VapeToolsTheme {
                Navigation()
            }
        }
    }
}