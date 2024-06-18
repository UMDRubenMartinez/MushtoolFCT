package com.example.mushtool_fct

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mushtool_fct.mushroomScreens.whatMushroomIsScreen
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        FirebaseApp.initializeApp(this)
        val idiomaGuardado = IdiomaManager.cargarIdiomaGuardado(this)
        IdiomaManager.actualizarIdioma(idiomaGuardado, this)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF8BC34A), // Color de fondo del NavigationBar
                contentColor = Color.White // Color del contenido (texto e iconos)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    selected = currentRoute == "home",
                    onClick = { navController.navigate("home") },
                    alwaysShowLabel = true, // Mostrar siempre el texto
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.Black,
                        unselectedTextColor = Color.Black
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    selected = currentRoute == "search",
                    onClick = { navController.navigate("search") },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.Black,
                        unselectedTextColor = Color.Black
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Folder, contentDescription = null) },
                    selected = currentRoute == "myMushrooms",
                    onClick = { navController.navigate("myMushrooms") },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.Black,
                        unselectedTextColor = Color.Black
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Restaurant, contentDescription = null) },
                    selected = currentRoute == "restaurant",
                    onClick = { navController.navigate("restaurant") },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.Black,
                        unselectedTextColor = Color.Black
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.School, contentDescription = null) },
                    selected = currentRoute == "learn",
                    onClick = { navController.navigate("learn") },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.Black,
                        unselectedTextColor = Color.Black
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Collections, contentDescription = null) },
                    selected = currentRoute == "comunity",
                    onClick = { navController.navigate("comunity") },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.Black,
                        unselectedTextColor = Color.Black
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Web, contentDescription = null) },
                    selected = currentRoute == "mushtoolWeb",
                    onClick = { navController.navigate("mushtoolWeb") },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.Black,
                        unselectedTextColor = Color.Black
                    )
                )
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "home", Modifier.padding(innerPadding)) {
            composable("home") { MainScreen(navController) }
            composable("settings") { SettingsScreen(navController) }
            composable("myMushrooms") { MushroomScreen(navController) }
            composable("search") { SearchScreen(navController) }
            composable("restaurant") { RestaurantScreen(navController) }
            composable("learn") { LearnScreen(navController) }
            composable("comunity") { ComunityScreen(navController) }
            composable("mushtoolWeb") { WebScreen(navController) }
            composable("whatMushroom"){ whatMushroomIsScreen(navController)}
        }
    }
}

@Composable
fun AdMobBanner() {
    val context = LocalContext.current
    var adUnitId = "ca-app-pub-3940256099942544/6300978111"
    AndroidView(
        factory = {
            val adView = AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
            }
            adView.loadAd(AdRequest.Builder().build())
            adView
        },
        update = { it.loadAd(AdRequest.Builder().build()) }
    )
}