package com.example.mushtool_fct.Screen.ComunityScreens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlinx.coroutines.flow.MutableStateFlow

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun MessagesMushtoolScreen(navController: NavController){
    var context = LocalContext.current
    var listaMensajes = remember { mutableStateOf(emptyList<forumMessage>()) }
    var nuevoMensaje = remember { mutableStateOf("") } // Nuevo estado para el mensaje que se va a enviar
    var idUser:String
    val updatedMessages = remember { mutableStateOf(mutableListOf<forumMessage>()) }



    LaunchedEffect(true) {
        val firestore = FirebaseFirestore.getInstance()
        val collectionRef = firestore.collection("forum")
        val userRef = firestore.collection("users")
        /*
        collectionRef.get()
            .addOnSuccessListener { querySnapshot ->
                val mensajes = querySnapshot.documents.mapNotNull { it.toObject(forumMessage::class.java) }
                listaMensajes.value = mensajes
                Log.d("Firestore", "Objetos obtenidos: ${listaMensajes.value}")
                Log.w("UserId", idUser)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error al obtener documentos: ", exception)
            }*/
        collectionRef.get()
            .addOnSuccessListener { querySnapshot ->
                val mensajes = querySnapshot.documents.mapNotNull { it.toObject(forumMessage::class.java) }

                mensajes.forEach { mensaje ->
                    val userId = mensaje.createdBy
                    userRef.document(userId).get()
                        .addOnSuccessListener { userSnapshot ->
                            val user = userSnapshot.toObject(Users::class.java)
                            val username = user?.Nombre ?: "Unknown User"
                            Log.w("Username",username.toString())
                            updatedMessages.value.add(forumMessage(username, mensaje.createdAt, mensaje.text))
                            if (updatedMessages.value.size == mensajes.size) {
                                listaMensajes.value = updatedMessages.value
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.w("Firestore", "Error al obtener usuario: ", exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error al obtener documentos: ", exception)
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { BackButton(navController);Text(text = stringResource(id = R.string.MushtoolWeb)) },
                backgroundColor = Color(0xFF8BC34A),
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                    }
                }
            )
        }, bottomBar = {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()){
                OutlinedTextField(
                    value = nuevoMensaje.value,
                    onValueChange = { nuevoMensaje.value = it },
                    trailingIcon = {ElevatedButton(
                        onClick = {
                            if (nuevoMensaje.value.isNotBlank()) {
                                val firestore = FirebaseFirestore.getInstance()
                                val collectionRef = firestore.collection("forum")
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
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("Firestore", "Error al enviar mensaje", e)
                                    }
                            }
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar")
                    }},
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5DC)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Ajuste para separar los mensajes de la barra de entrada

        ) {
            if (listaMensajes.value.isEmpty()) {
                Text("Cargando mensajes...", style = MaterialTheme.typography.bodyMedium)
            } else {
                listaMensajes.value.forEach { mensaje ->
                    MessageCard(mensaje)
                }
            }

        }
    }
}

@Composable
fun MessageCard(mensaje: forumMessage) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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