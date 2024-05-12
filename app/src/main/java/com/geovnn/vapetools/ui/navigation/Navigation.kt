package com.geovnn.vapetools.ui.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.FolderSpecial
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.geovnn.vapetools.R
import com.geovnn.vapetools.ui.screens.coil_screen.CoilScreen
import com.geovnn.vapetools.ui.screens.liquid_screen.LiquidScreen
import com.geovnn.vapetools.ui.screens.nicotine_screen.NicotineScreen
import com.geovnn.vapetools.ui.screens.ohm_screen.OhmScreen
import com.geovnn.vapetools.ui.screens.saved_screen.SavedLiquidsViewModel
import com.geovnn.vapetools.ui.screens.saved_screen.SavedScreen
import kotlinx.coroutines.launch

@Composable
fun Navigation(savedLiquidViewModel: SavedLiquidsViewModel) {
    val navController = rememberNavController()
    val items = listOf(
        NavigationItem(
            title = "E-Liquid Calculator",
            selectedIcon = Icons.Outlined.Science,
            unselectedIcon = Icons.Filled.Science,
            destination = "liquid_screen"
        ),
        NavigationItem(
            title = "Nicotine Blender",
            selectedIcon = Icons.Outlined.LocalDrink,
            unselectedIcon = Icons.Filled.LocalDrink,
            destination = "nicotine_screen"
        ),
        NavigationItem(
            title = "Coil Calculator",
            selectedIcon = Icons.Outlined.ElectricBolt,
            unselectedIcon = Icons.Filled.ElectricBolt,
            destination = "coil_screen"
        ),
        NavigationItem(
            title = "Ohm's Law",
            selectedIcon = Icons.Outlined.Calculate,
            unselectedIcon = Icons.Filled.Calculate,
            destination = "ohm_screen"
        ),
        NavigationItem(
            title = "Saved liquids",
            selectedIcon = Icons.Outlined.FolderSpecial,
            unselectedIcon = Icons.Filled.FolderSpecial,
            destination = "saved_screen"
        ),
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
        val scope = rememberCoroutineScope()
        var showAbout by remember { mutableStateOf(false) }
        var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
        if (showAbout) {
            AboutDialog{ boolean ->
                showAbout = boolean
            }
        }
        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(Modifier.height(32.dp))
                    items.forEachIndexed { index, item ->
                        NavigationDrawerItem(
                            label = {
                                Text(text = item.title)
                            },
                            selected = index == selectedItemIndex,
                            onClick = {
                                selectedItemIndex = index
                                scope.launch {
                                    navController.navigate(item.destination){
                                        popUpTo(0){
                                            inclusive = true
                                            saveState = true
                                        }
                                    }
                                    drawerState.close()
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (index == selectedItemIndex) {
                                        item.selectedIcon
                                    } else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            modifier = Modifier
                                .padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1.0f))
                    NavigationDrawerItem(
                        label = {
                            Text(text = "About")
                        },
                        selected = false,

                        onClick = {
                            scope.launch {
                                showAbout = true
                                drawerState.close()
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Filled.Info,
                                contentDescription = "About"
                            )
                        },
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    Spacer(Modifier.height(32.dp))
                }
            },
            drawerState = drawerState
        ) {
            NavHost(navController = navController, startDestination = Screen.LiquidScreen.route) {
                composable(route = Screen.LiquidScreen.route) {
                    LiquidScreen(drawerState)
                }
                composable(route = Screen.NicotineScreen.route) {
                    NicotineScreen(drawerState)
                }
                composable(route = Screen.CoilScreen.route) {
                    CoilScreen(drawerState)
                }
                composable(route = Screen.OhmScreen.route) {
                    OhmScreen(drawerState)
                }
                composable(route = Screen.SavedScreen.route) {
                    SavedScreen(drawerState,savedLiquidViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutDialog(getFlag: (Boolean) -> Unit) {
    val context = LocalContext.current
    val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/geovnn")) }

    AlertDialog(
        onDismissRequest = {getFlag(false)
        },
    ) {
        val githubBadge = painterResource(id = R.drawable.github_badge)
        Card(
            Modifier
                .wrapContentSize()
        ) {
            Column (
                Modifier
                    .wrapContentSize()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Vape Tools\nby Giovanni",
                    Modifier
                        .wrapContentSize(),
                    textAlign = TextAlign.Center,
                    )
                Image(
                    painter = githubBadge,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .height(50.dp)
                        .size(120.dp)
                        .wrapContentSize()
                        .clickable {
                            context.startActivity(intent)
                        }
                )
            }
        }
    }
}