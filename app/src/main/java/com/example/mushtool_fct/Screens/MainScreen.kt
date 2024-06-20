package com.example.mushtool_fct.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mushtool_fct.AdMobBanner
import com.example.mushtool_fct.Data.NavRoute
import com.example.mushtool_fct.R
import com.google.android.gms.ads.MobileAds


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavController) {
    MobileAds.initialize(LocalContext.current)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MUSHTOOL") },
                backgroundColor = Color(0xFF8BC34A),
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5DC)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AdMobBanner()

            val items = listOf(
                NavRoute(Icons.Default.Search, stringResource(id = R.string.search),"search"),
                NavRoute(Icons.Default.Folder, stringResource(id = R.string.myMushrooms),"myMushrooms"),
                NavRoute(Icons.Default.Restaurant, stringResource(id = R.string.eat),"restaurant"),
                NavRoute(Icons.Default.School, stringResource(id = R.string.learn),"learn"),
                NavRoute(Icons.Default.Collections, stringResource(id = R.string.comunity), "comunity"),
                NavRoute(Icons.Default.Public, stringResource(id = R.string.MushtoolWeb), "mushtoolWeb"),
            )

            items.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    rowItems.forEach { item ->
                        Item(icon = item.icon, text = item.text,onClick = { navController.navigate(item.ruta) })
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
@Composable
fun Item(icon: ImageVector, text: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(8.dp)
    ) {
        IconButton(
            modifier = Modifier
                .size(80.dp) // Aumentar el tamaño del botón
                .clip(shape = CircleShape)
                .background(Color(0xFF8BC34A)),
            onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp) // Aumentar el tamaño del ícono
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2, // Permitir hasta 2 líneas de texto
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(80.dp) // Ajustar el ancho para dar más espacio al texto
        )
    }
}