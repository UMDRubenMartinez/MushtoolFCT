package com.example.mushtool_fct.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mushtool_fct.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MushroomScreen(navController: NavController) {
    var context = LocalContext.current
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("LISTADO", "MAPA")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.myMushrooms)) },
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
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colorScheme.primary,
                        height = 2.dp
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }

            // Content for the selected tab
            when (selectedTabIndex) {
                0 -> ListadoScreen()
                1 -> MapaScreen()
            }
        }

        }
    }
@Composable
fun ListadoScreen() {
    // Your "LISTADO" screen content
    Text("Listado Content")
}

@Composable
fun MapaScreen() {
    // Your "MAPA" screen content
    Text("Mapa Content")
}