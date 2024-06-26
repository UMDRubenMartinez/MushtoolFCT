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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mushtool_fct.Screen.Auth.AuthScreen
import com.example.mushtool_fct.Screen.Auth.SignupScreen
import com.example.mushtool_fct.Screen.ComunityScreens.ComunityScreen
import com.example.mushtool_fct.Screen.ComunityScreens.MessagesMushtoolScreen
import com.example.mushtool_fct.Screen.ComunityScreens.MushPhotosScreen
import com.example.mushtool_fct.Screen.ComunityScreens.RepliesScreen
import com.example.mushtool_fct.Screen.EatScreens.EatNowScreen
import com.example.mushtool_fct.Screen.EatScreens.RecipesScreen
import com.example.mushtool_fct.Screen.IdiomaManager
import com.example.mushtool_fct.Screen.MainScreen
import com.example.mushtool_fct.Screen.MushroomScreen
import com.example.mushtool_fct.Screen.EatScreens.RestaurantScreen
import com.example.mushtool_fct.Screen.SearchScreens.SearchScreen
import com.example.mushtool_fct.Screen.SearchScreens.foundedMushroomScreen
import com.example.mushtool_fct.Screen.SearchScreens.whatIsScreen
import com.example.mushtool_fct.Screen.SettingsScreen
import com.example.mushtool_fct.Screen.WebScreen
import com.example.mushtool_fct.Screen.Learn.*
import com.example.mushtool_fct.Screen.EatScreens.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
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
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(auth) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navController.navigate("home") {
                popUpTo("auth") { inclusive = true }
            }
        }
    }

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            if (auth.currentUser != null && currentRoute != "auth" && currentRoute != "signup" && currentRoute != "messages") {
                NavigationBar(
                    containerColor = Color(0xFF8BC34A), // Color de fondo del NavigationBar
                    contentColor = Color.White // Color del contenido (texto e iconos)
                ) {
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
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "auth", Modifier.padding(innerPadding)) {
            composable("auth"){ AuthScreen(navController)}
            composable("signup") { SignupScreen(navController)}
            composable("home") { MainScreen(navController) }
            composable("settings") { SettingsScreen(navController) }
            composable("myMushrooms") { MushroomScreen(navController) }
            composable("search") { SearchScreen(navController) }
            composable("restaurant") { RestaurantScreen(navController) }
            composable("learn") { LearnScreen(navController) }
            composable("comunity") { ComunityScreen(navController) }
            composable("mushtoolWeb") { WebScreen(navController) }
            composable("foundedMushrooms"){ foundedMushroomScreen(navController)}
            composable("whatIs"){ whatIsScreen(navController)}
            composable("game"){GameScreen(navController)}
            composable("learn_glossary"){mushGlossary(navController)}
            composable("mushScience"){MushscienceScreen(navController)}
            composable("files"){FilesScreen(navController)}
            composable("eatMush"){EatMushScreen(navController)}
            composable("eatNow"){EatNowScreen(navController)}
            composable("recipes"){RecipesScreen(navController)}
            composable("messages"){ MessagesMushtoolScreen(navController)}
            composable("mushPhotos"){ MushPhotosScreen(navController)}
            composable(
                "respuestas/{preguntaId}",
                arguments = listOf(navArgument("preguntaId") { type = NavType.StringType })
            ) { backStackEntry ->
                RepliesScreen(
                    navController,
                    backStackEntry.arguments?.getString("preguntaId") ?: ""
                )
            }
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