package com.example.mushtool_fct.mushroomScreens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mushtool_fct.Data.MushroomFirestore
import com.example.mushtool_fct.R
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun whatMushroomIsScreen(navController: NavController){
    var context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mushrooms by remember { mutableStateOf(listOf<MushroomFirestore>()) }
    LaunchedEffect(true) {
        scope.launch {
            mushrooms = getMushrooms()
            println(mushrooms.size)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.MushtoolWeb)) },
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
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 128.dp),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(mushrooms.size) { index ->
                    val mushroom = mushrooms[index]
                    Column(
                        modifier = Modifier.padding(4.dp)
                    ) {
                        AsyncImage(
                            model = mushroom!!.imageDefectPath,
                            contentDescription = null
                        )
                        Text(
                            text = mushroom.name,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}


suspend fun getMushrooms(): List<MushroomFirestore> {
    val db = FirebaseFirestore.getInstance()
    return try {
        val snapshot = db.collection("mushrooms").get().await()
        snapshot.documents.mapNotNull { it.toObject(MushroomFirestore::class.java) }
    } catch (e: Exception) {
        emptyList()
    }
}