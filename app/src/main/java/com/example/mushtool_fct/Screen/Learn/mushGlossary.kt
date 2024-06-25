package com.example.mushtool_fct.Screen.Learn

import androidx.compose.foundation.Image
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberBottomSheetScaffoldState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.mushtool_fct.Entity.Caracteristica
import com.example.mushtool_fct.Entity.Criba
import com.example.mushtool_fct.Entity.Mushroom
import com.example.mushtool_fct.Model.FirebaseCaracteristica
import com.example.mushtool_fct.Model.FirebaseCriba
import com.example.mushtool_fct.Model.FirebaseMushroom
import com.example.mushtool_fct.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun mushGlossary(navController: NavController){
    //var context = LocalContext.current
    val mushroomsList = remember { mutableStateOf<List<Pair<Int, Mushroom>>>(emptyList()) }
    val mushroomsCribaList = remember { mutableStateOf<List<Pair<Int, Criba>>>(emptyList()) }

    var selectedmushroomPair by remember { mutableStateOf<Pair<Int, Criba>?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberBottomSheetScaffoldState()
    //println("Lista de datos:" + mushroomsList.value)

    Log.d("SELECTED","${selectedmushroomPair}")
    Log.d("SHEET","${sheetState}")

    LaunchedEffect("getMushroomList") {
        val conectionFirebaseMush = FirebaseMushroom()
        val fetchedMushList = conectionFirebaseMush.getMushroomPairList()
        mushroomsList.value = fetchedMushList

        val conectionFirebaseCriba = FirebaseCriba()
        conectionFirebaseCriba.setIdioma("en")
        val fetchedCribaList = conectionFirebaseCriba.getCribaMushroomPairList(mushroomsList.value)
        mushroomsCribaList.value = fetchedCribaList
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
        BottomSheetScaffold(
            scaffoldState = sheetState,
            //topBar = {},
            //backgroundColor = Color(0xFFEDE7F6), // Fondo más suave
            sheetContent = {
                selectedmushroomPair?.let { mushroomPair ->
                    MushroomDetails(mushroomPair = mushroomPair) // Asegúrate de que este composable esté correctamente implementado
                }
            },
            sheetPeekHeight = 0.dp
        ){
            innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5DC)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                MushroomList(
                    mushroomsCribaList.value,
                    modifier = Modifier.padding(innerPadding),
                    onClick = { mushroom ->
                        selectedmushroomPair = mushroom
                        coroutineScope.launch {
                            sheetState.bottomSheetState.expand()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MushroomList(mushrooms: List<Pair<Int, Criba>>, modifier: Modifier = Modifier, onClick: (Pair<Int, Criba>) -> Unit) {
    LazyColumn(modifier = modifier) {
        items(mushrooms) { mushroomPair ->
            val mushroom = mushroomPair.second
            MushroomItem(mushroom = mushroom, onClick = {
                onClick(mushroomPair)
            })
        }
    }
}

@Composable
fun MushroomItem(mushroom: Criba, onClick: () -> Unit) {
    Card(
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(shape = RoundedCornerShape(8.dp)) // Esquinas redondeadas
            .clickable(
                onClick = onClick
            ),
        elevation = 4.dp // Sombra para dar efecto de elevación
    ) {
        Row(modifier = Modifier
            .clickable(
                onClick = onClick
            )
            .padding(8.dp))
        {
            Image(
                painter = rememberAsyncImagePainter(mushroom.urlFoto),
                contentDescription = null,
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(4.dp)), // Esquinas redondeadas en la imagen
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically))
            {
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
                    Text(
                        text = mushroom.nomConegut,
                        style = MaterialTheme.typography.body2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun MushroomDetails(mushroomPair: Pair<Int, Criba>) {
    var idDocument = mushroomPair.first
    var mushroom = mushroomPair.second
    val caracteristica = remember { mutableStateOf<Caracteristica>(Caracteristica()) }

    LaunchedEffect(idDocument) {
        val conectionFirebaseCaracteristica = FirebaseCaracteristica()
        conectionFirebaseCaracteristica.setIdioma("en")
        val fetchedCaract = conectionFirebaseCaracteristica.getCaracteristicaWithID(idDocument)
        caracteristica.value = fetchedCaract
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0EAE2)) // Un fondo suave que complemente el tema de naturaleza
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Permite el desplazamiento vertical si el contenido excede la pantalla
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(mushroom.urlFoto),
            contentDescription = "${mushroom.nomConegut} image",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(8.dp)), // Esquinas redondeadas para la imagen
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = mushroom.nomConegut,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4E342E) // Un color oscuro que contraste bien con el fondo
        )
        Text(
            text = "Scientific name: ${mushroom.nomCientific}",
            fontSize = 18.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "Description",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(vertical = 8.dp),
            color = Color(0xFF6D4C41) // Un marrón suave para los títulos de sección
        )
        Text(
            text = caracteristica.value.descripcio,
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = caracteristica.value.comentariXef,
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = caracteristica.value.comentari,
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        // Repite el patrón para otros detalles como el diámetro del sombrero, la temporada, y el tipo
        //DetailSection(title = "Hat Diameter", detail = mushroom.hatDiameter)
        //DetailSection(title = "Season", detail = mushroom.season)
        //DetailSection(title = "Type", detail = mushroom.type)
    }
}
