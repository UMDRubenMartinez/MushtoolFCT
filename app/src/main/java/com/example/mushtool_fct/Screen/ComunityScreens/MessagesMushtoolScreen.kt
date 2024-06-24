package com.example.mushtool_fct.Screen.ComunityScreens

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mushtool_fct.Data.forumMessage
import com.example.mushtool_fct.Model.MessagesViewModel
import com.example.mushtool_fct.R
import com.example.mushtool_fct.Repository.BackButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun MessagesMushtoolScreen(navController: NavController,viewModel: MessagesViewModel){
    val listaMensajes by viewModel.messages.collectAsState()
    var nuevoMensaje = remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(true) {
        viewModel.loadMessages()
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
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nuevoMensaje.value,
                    onValueChange = { nuevoMensaje.value = it },
                    trailingIcon = {
                        ElevatedButton(
                            onClick = {
                                if (nuevoMensaje.value.isNotBlank()) {
                                    val firestore = FirebaseFirestore.getInstance()
                                    val collectionRef = firestore.collection("forum")
                                    val auth = FirebaseAuth.getInstance()
                                    val currentUser = auth.currentUser
                                    val mensaje = forumMessage(
                                        createdBy = currentUser?.uid ?: "",
                                        createdAt = Timestamp.now(),
                                        text = nuevoMensaje.value
                                    )
                                    collectionRef.add(mensaje)
                                        .addOnSuccessListener { documentReference ->
                                            Log.d("Firestore", "Mensaje enviado correctamente con ID: ${documentReference.id}")
                                            val newMessage = mensaje.copy(id = documentReference.id)
                                            viewModel.addMessage(newMessage)
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
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.White)
                        .padding(8.dp),
                    placeholder = { Text("Escribe tu mensaje...") },

                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5DC)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (listaMensajes.isEmpty()) {
                Text("Cargando mensajes...", style = MaterialTheme.typography.bodyMedium)
            } else {
                listaMensajes.forEach { mensaje ->
                    MessageCard(navController, mensaje)
                }
            }
        }
    }
}

@Composable
fun MessageCard(navController: NavController,mensaje: forumMessage) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable{navController.navigate("questionDetail/${mensaje.id}") },
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