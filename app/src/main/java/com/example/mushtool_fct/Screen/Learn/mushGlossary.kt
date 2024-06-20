package com.example.mushtool_fct.Screen.Learn

import androidx.compose.foundation.Image
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.mushtool_fct.Entity.Mushroom
import com.example.mushtool_fct.Model.FirebaseMushroom
import com.example.mushtool_fct.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun mushGlossary(navController: NavController){
    //var context = LocalContext.current
    val mushroomsList = remember { mutableStateOf<List<Pair<String, Mushroom>>>(emptyList()) }
    println("Lista de datos:" + mushroomsList.value)

    LaunchedEffect("getMushroomList") {
        val conectionFirebase = FirebaseMushroom()
        val fetchedList = conectionFirebase.getMushroomPairList()
        mushroomsList.value = fetchedList
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
            MushroomList(
                mushroomsList.value,
                onClick = { mushroom -> }
            )
        }
    }
}

@Composable
fun MushroomList(mushrooms: List<Pair<String, Mushroom>>, onClick: (Mushroom) -> Unit) {
    LazyColumn(modifier = Modifier) {
        items(mushrooms) { mushroomPair ->
            val mushroom = mushroomPair.second
            MushroomItem(mushroom = mushroom, onClick = {
                onClick(mushroom)
            })
        }
    }
}

@Composable
fun MushroomItem(mushroom: Mushroom, onClick: () -> Unit) {
    Card(
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(shape = RoundedCornerShape(8.dp)), // Esquinas redondeadas
        elevation = 4.dp // Sombra para dar efecto de elevación
    ) {
        Row(modifier = Modifier.clickable(onClick = onClick).padding(8.dp)) {
            Image(
                painter = rememberAsyncImagePainter(mushroom.urlFoto),
                contentDescription = null,
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(4.dp)), // Esquinas redondeadas en la imagen
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterVertically)) {
                Text(
                    text = mushroom.nomCientific,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Log.d("mush","${mushroom.nomCientific}")
                Row(){
                    Text(
                        text = mushroom.temporada,
                        style = MaterialTheme.typography.body2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = mushroom.toxicitat,
                        style = MaterialTheme.typography.body2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = mushroom.consum,
                        style = MaterialTheme.typography.body2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}