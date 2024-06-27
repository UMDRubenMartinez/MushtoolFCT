package com.example.mushtool_fct.Screen.ComunityScreens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mushtool_fct.Data.Users
import com.example.mushtool_fct.Data.forumMessage
import com.example.mushtool_fct.R
import com.example.mushtool_fct.Repository.BackButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun MessagesMushtoolScreen(navController: NavController){
    val context = LocalContext.current
    val listaMensajes = remember { mutableStateOf(emptyList<Pair<String, forumMessage>>()) }
    val nuevoMensaje = remember { mutableStateOf("") }
    val updatedMessages = remember { mutableStateOf(mutableListOf<Pair<String, forumMessage>>()) }
    val firestore = FirebaseFirestore.getInstance()
    val collectionRef = firestore.collection("forum")
    val userRef = firestore.collection("users")
    val reloadMessages = remember { mutableStateOf(false) }


   suspend fun fetchMessages() {
        try {
            val querySnapshot = collectionRef.orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING).get().await()
            val mensajes = querySnapshot.documents.mapNotNull { doc ->
                val mensaje = doc.toObject(forumMessage::class.java)
                if (mensaje != null) {
                    Pair(doc.id, mensaje)
                } else {
                    null
                }
            }

            val mensajesWithUsernames = coroutineScope {
                mensajes.map { (id, mensaje) ->
                    async {
                        val userId = mensaje.createdBy
                        val userSnapshot = userRef.document(userId).get().await()
                        val user = userSnapshot.toObject(Users::class.java)
                        val username = user?.Nombre ?: "Unknown User"
                        Pair(id, forumMessage(username, mensaje.createdAt, mensaje.text))
                    }
                }.awaitAll()
            }
            listaMensajes.value = mensajesWithUsernames
        } catch (e: Exception) {
            Log.w("Firestore", "Error al obtener documentos: ")
    }
   }




    LaunchedEffect(reloadMessages.value) {
        fetchMessages()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { BackButton(navController); Text(text = stringResource(id = R.string.MushtoolWeb)) },
                backgroundColor = Color(0xFF8BC34A),
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = nuevoMensaje.value,
                    onValueChange = { nuevoMensaje.value = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    trailingIcon = {
                        ElevatedButton(
                            onClick = {
                                if (nuevoMensaje.value.isNotBlank()) {
                                    val auth = FirebaseAuth.getInstance()
                                    val currentUser = auth.currentUser
                                    val mensaje = forumMessage(
                                        currentUser?.uid ?: "",
                                        Timestamp.now(),
                                        nuevoMensaje.value
                                    )
                                    collectionRef.add(mensaje)
                                        .addOnSuccessListener {
                                            Log.d("Firestore", "Mensaje enviado correctamente")
                                            nuevoMensaje.value = ""
                                            reloadMessages.value = !reloadMessages.value // Toggle the reload state
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w("Firestore", "Error al enviar mensaje", e)
                                        }
                                }
                            },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Enviar")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.White)
                        .padding(8.dp),
                    placeholder = { Text("Escribe tu mensaje...") }
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5DC))
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
            contentPadding = PaddingValues(bottom = 72.dp) // Ajusta este valor según la altura de tu BottomBar
        ) {
            item {
                if (listaMensajes.value.isEmpty()) {
                    Text("Cargando mensajes...", style = MaterialTheme.typography.bodyMedium)
                }
            }

            items(listaMensajes.value) { (id, mensaje) ->
                MessageCard(id, mensaje, navController)
                Spacer(modifier = Modifier.height(16.dp)) // Añade espacio entre los mensajes
            }

            // Añade un item Spacer al final para dejar espacio para la BottomBar
            item {
                Spacer(modifier = Modifier.height(72.dp)) // Ajusta este valor según la altura de tu BottomBar
            }
        }
    }
}


@Composable
fun MessageCard(id: String? = null, mensaje: forumMessage, navController: NavController) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(enabled = id != null) {
                id?.let { navController.navigate("respuestas/$it") }
            },
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Text(
                text = mensaje.createdBy,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = mensaje.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = mensaje.createdAt.toDate().toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}