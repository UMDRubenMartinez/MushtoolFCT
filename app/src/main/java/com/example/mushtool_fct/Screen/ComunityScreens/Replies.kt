package com.example.mushtool_fct.Screen.ComunityScreens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mushtool_fct.Data.Users
import com.example.mushtool_fct.Data.forumMessage
import com.example.mushtool_fct.Data.repliesMessage
import com.example.mushtool_fct.Model.MessagesViewModel
import com.example.mushtool_fct.Repository.BackButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun Replies(navController: NavController,viewModel: MessagesViewModel, question: forumMessage) {
    var nuevaRespuesta by remember { mutableStateOf("") }
    val updatedReplies = remember { mutableStateOf(mutableListOf<repliesMessage>()) }
    var listaRespuestas = remember { mutableStateOf(emptyList<repliesMessage>()) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(true) {
        val firestore = FirebaseFirestore.getInstance()
        val collectionRef = firestore.collection("forum")
            .document(question.id)
            .collection("respuestas")
        val userRef = firestore.collection("users")

        collectionRef.get()
            .addOnSuccessListener { querySnapshot ->
                val respuestas = querySnapshot.documents.mapNotNull { it.toObject(repliesMessage::class.java) }

                respuestas.forEach { respuesta ->
                    val userId = respuesta.createdBy
                    userRef.document(userId).get()
                        .addOnSuccessListener { userSnapshot ->
                            val user = userSnapshot.toObject(Users::class.java)
                            val username = user?.Nombre ?: "Unknown User"
                            Log.w("Username",username.toString())
                            updatedReplies.value.add(repliesMessage(username, respuesta.createdAt, respuesta.text))
                            if (updatedReplies.value.size == respuestas.size) {
                                listaRespuestas.value = updatedReplies.value
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
                title = { BackButton(navController); Text(text = "Detalles de la pregunta") },
                backgroundColor = Color(0xFF8BC34A)
            )
        },
            bottomBar = {
                // Barra inferior para escribir y enviar respuestas
                    // TextField para ingresar nueva respuesta
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()){
                        // TextField para ingresar nueva respuesta
                        OutlinedTextField(
                            value = nuevaRespuesta,
                            onValueChange = { nuevaRespuesta = it },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (nuevaRespuesta.isNotBlank()) {
                                        val userId =
                                            FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                        viewModel.addAnswer(question.id, nuevaRespuesta, userId)
                                        nuevaRespuesta = ""
                                        keyboardController?.hide()
                                    }
                                }
                            ), trailingIcon = {
                                ElevatedButton(
                                    onClick = {
                                        if (nuevaRespuesta.isNotBlank()) {
                                            val userId =
                                                FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                            viewModel.addAnswer(question.id, nuevaRespuesta, userId)
                                            nuevaRespuesta = ""
                                            keyboardController?.hide()
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
                            placeholder = { Text("Escribe tu respuesta aquí...") },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }
            }
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5DC))
                .padding(top = 16.dp) // Ajuste el padding superior para evitar la superposición con el TopBar
                .verticalScroll(rememberScrollState()) // Hacer scrollable toda la pantalla si es necesario
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Contenido de la pregunta
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF5F5DC)).padding(top = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = question.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Preguntado por: ${question.createdBy}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Fecha: ${question.createdAt.toDate()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Separador visual entre pregunta y respuestas
                Spacer(modifier = Modifier.height(16.dp))

                // Contenedor de respuestas
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (listaRespuestas.value.isEmpty()) {
                        Text(
                            text = "Cargando respuestas...",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        listaRespuestas.value.forEach { respuesta ->
                            ReplieCard(respuesta)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }


    }
}

@Composable
fun ReplieCard(respuesta: repliesMessage) {
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
                text = respuesta.createdBy,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = respuesta.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = respuesta.createdAt.toDate().toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}