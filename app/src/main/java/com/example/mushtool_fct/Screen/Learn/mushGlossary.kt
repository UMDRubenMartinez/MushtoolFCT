package com.example.mushtool_fct.Screen.Learn

import androidx.compose.foundation.Image
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
                Row(){
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
    val idDocument = mushroomPair.first
    val mushroom = mushroomPair.second
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
        if(mushroom.altresNoms != "") {
            ItemNormalDetails("", mushroom.altresNoms)
        }
        Row(){
            val listRow: List<Pair<String, String>> = listOf(
                "" to mushroom.nomCientific,
                "" to mushroom.habitats,
            )
            RowListNormalDetails(listRow)
        }
        Row(){
            ItemNormalDetails("", mushroom.diametre)
            Spacer(modifier = Modifier.width(16.dp))
            ItemNormalDetails("", mushroom.gruixDePeu)
            Spacer(modifier = Modifier.width(16.dp))
            ItemNormalDetails("", mushroom.llargadaDelPeu)
        }
        Row(){
            ItemNormalDetails("", mushroom.temporada)
            Spacer(modifier = Modifier.width(16.dp))
            ItemNormalDetails("", mushroom.consum)
            Spacer(modifier = Modifier.width(16.dp))
            ItemNormalDetails("", mushroom.toxicitat)
        }
        //Seccion de con desplegables de descripciones según idioma
        ItemCycler(caracteristica.value)
    }
}

fun createList(caracteristica: Caracteristica): List<Pair<String, String>> {
    val items = listOfNotNull(
        caracteristica.descripcio?.let { "Descripción" to it },
        caracteristica.comentariXef?.let { "Comentario Xef" to it },
        caracteristica.comentari?.let { "Comentario Experto" to it }
    )
    return items
}

@Composable
fun ItemCycler(caracteristica: Caracteristica) {
    val listOfItems: List<Pair<String, String>> = createList(caracteristica)
    var currentIndex by remember { mutableStateOf(0) }
    TextButton(
        onClick = {
            currentIndex = (currentIndex + 1) % listOfItems.size
            if (listOfItems[currentIndex].second == "") {
                currentIndex = (currentIndex + 1) % listOfItems.size
            }
        },
        colors = ButtonDefaults.textButtonColors(),
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Text(
            text = "< " + listOfItems[currentIndex].first + " >",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(vertical = 4.dp),
            color = Color(0xFF6D4C41) // Un marrón suave para los títulos de sección
        )
    }
    Text(
        text = listOfItems[currentIndex].second,
        fontSize = 18.sp,
        color = Color.DarkGray,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .padding(8.dp)
    )
}

/*@Composable
fun DropDownDetails(titulo:String, info: String) {
    if(info != "") {
        var expanded by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .clickable { expanded = true }
        ) {
            Text(
                text = titulo,
                fontSize = 18.sp,
                color = Color.DarkGray,
                //.padding(16.dp)
                //.background(MaterialTheme.colors.surface)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(onClick = { expanded = false }) {
                    Text(text = info)
                }
            }
        }
    }
}*/

@Composable
fun ExpandedNormalDetails(titulo:String, info: String) {
    if(info != "") {
        var expanded by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .clickable { expanded = true }
        ) {
            if (!expanded) {
                Text(
                    text = titulo,
                    fontSize = 18.sp,
                    color = Color.DarkGray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(8.dp)
                )
            }
            if (expanded) {
                Text(
                    text = info,
                    fontSize = 18.sp,
                    color = Color.DarkGray,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable { expanded = !expanded }
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun RowListNormalDetails(listOfItems: List<Pair<String, String>>) {
    listOfItems.forEachIndexed { index, item ->
        if(item.second != ""){
            ItemNormalDetails(item.first, item.second)
            if (index != listOfItems.size - 1) {
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

@Composable
fun ItemNormalDetails(titulo:String, info: String) {
    Text(
        text = "${titulo}${info}",
        fontSize = 18.sp,
        color = Color.DarkGray,
        modifier = Modifier.padding(top = 8.dp)
    )
}
