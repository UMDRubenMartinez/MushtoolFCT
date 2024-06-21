package com.example.mushtool_fct.Repository

import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BackButton(navController: NavController) {
    IconButton(onClick = { navController.navigateUp()}) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
    }
}