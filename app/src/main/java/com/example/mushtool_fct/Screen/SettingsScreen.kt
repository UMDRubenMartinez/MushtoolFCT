package com.example.mushtool_fct.Screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mushtool_fct.Entity.MultiLanguage
import com.example.mushtool_fct.Screen.Auth.logOut
import java.util.Locale

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SettingsScreen(navController: NavHostController) {
    var context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("Español") }
    val multiLanguage = MultiLanguage()

    val languageCodes = mapOf(
        "Español" to "es",
        "Català" to "ca",
        "English" to "en"
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { androidx.compose.material.Text("MUSHTOOL") },
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
            Text(Locale.getDefault().language);
            context.resources.configuration.locale
            Box {
                Text(
                    text = "Selecciona el idioma:",
                    modifier = Modifier
                        .clickable(onClick = { expanded = true })
                        .padding(16.dp)
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .widthIn(max = 200.dp)
                ) {
                    languageCodes.forEach { (language, code) ->
                        DropdownMenuItem(onClick = {
                            selectedLanguage = language
                            expanded = false
                            multiLanguage.cambiarIdioma(
                                code,
                                context
                            ) // Aquí se llama al método cambiarIdioma con el código ISO del idioma seleccionado
                        }) {
                            Text(text = language)
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                logOut(navController)
            },colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8BC34A)
            )) {
                Text("Log Out")
            }
        }
    }
}


