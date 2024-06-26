package com.example.mushtool_fct.Screen.ComunityScreens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.mushtool_fct.Functions.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun RepliesScreen(navController: NavController, preguntaId: String) {
    val pregunta = remember { mutableStateOf<forumMessage?>(null) }
    val listaRespuestas = remember { mutableStateOf(emptyList<Pair<String, forumMessage>>()) }
    val updatedRespuestas = remember { mutableStateOf(mutableListOf<Pair<String, forumMessage>>()) }
    var nuevaRespuesta by remember { mutableStateOf("") }
    val firestore = FirebaseFirestore.getInstance()
    val userRef = firestore.collection("users")
    val keyboardController = LocalSoftwareKeyboardController.current
    val loadData = remember { mutableStateOf(true) }


    LaunchedEffect(loadData.value) {
        if (loadData.value) {
            firestore.collection("forum").document(preguntaId).collection("respuestas").get()
                .addOnSuccessListener { querySnapshot ->
                    val respuestas = querySnapshot.documents.mapNotNull { doc ->
                        val respuesta = doc.toObject(forumMessage::class.java)
                        respuesta?.let { Pair(doc.id, it) }
                    }

                    respuestas.forEach { (id, respuesta) ->
                        val userId = respuesta.createdBy
                        userRef.document(userId).get()
                            .addOnSuccessListener { userSnapshot ->
                                val user = userSnapshot.toObject(Users::class.java)
                                val username = user?.Nombre ?: "Unknown User"
                                updatedRespuestas.value = updatedRespuestas.value.toMutableList().apply {
                                    add(Pair(id, forumMessage(username, respuesta.createdAt, respuesta.text)))
                                }
                                if (updatedRespuestas.value.size == respuestas.size) {
                                    listaRespuestas.value = updatedRespuestas.value
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.w("Firestore", "Error al obtener usuario: ", exception)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("Firestore", "Error al obtener respuestas: ", exception)
                }
            firestore.collection("forum").document(preguntaId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val mensaje = documentSnapshot.toObject(forumMessage::class.java)
                    mensaje?.let {
                        val userId = it.createdBy
                        userRef.document(userId).get()
                            .addOnSuccessListener { userSnapshot ->
                                val user = userSnapshot.toObject(Users::class.java)
                                val username = user?.Nombre ?: "Unknown User"
                                pregunta.value = forumMessage(username, it.createdAt, it.text)
                            }
                            .addOnFailureListener { exception ->
                                Log.w("Firestore", "Error al obtener usuario: ", exception)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("Firestore", "Error al obtener la pregunta: ", exception)
                }
            // Una vez cargados los datos, desactivar la carga automática
            loadData.value = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Respuestas") },
                backgroundColor = Color(0xFF8BC34A),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }, bottomBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = nuevaRespuesta,
                    onValueChange = { nuevaRespuesta = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (nuevaRespuesta.isNotBlank()) {
                                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                addAnswer(preguntaId, nuevaRespuesta, userId)
                                nuevaRespuesta = ""
                                keyboardController?.hide()
                            }
                            loadData.value = true
                        }
                    ),
                    trailingIcon = {
                        ElevatedButton(
                            onClick = {
                                if (nuevaRespuesta.isNotBlank()) {
                                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                    addAnswer(preguntaId, nuevaRespuesta, userId)
                                    nuevaRespuesta = ""
                                    keyboardController?.hide()

                                }
                                loadData.value = true
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
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
        }
    ) {
        /*Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5DC)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (pregunta.value == null) {
                Text("Cargando pregunta...", style = MaterialTheme.typography.body2)
            } else {
                // Mostrar la pregunta original
                MessageCard(null, pregunta.value!!, navController)
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (listaRespuestas.value.isEmpty()) {
                Text("Cargando respuestas...", style = MaterialTheme.typography.body2)
            } else {
                listaRespuestas.value.forEach { (_, respuesta) ->
                    MessageCard(null, respuesta, navController)
                }
            }
        }*/
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5DC)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (pregunta.value == null) {
                Text("Cargando pregunta...", style = MaterialTheme.typography.body2)
            } else {
                // Mostrar la pregunta original
                MessageCard(null, pregunta.value!!, navController)
                Spacer(modifier = Modifier.height(16.dp))
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5DC))
                    .padding(horizontal = 16.dp), // Ajustar el padding según sea necesario
                verticalArrangement = Arrangement.Top,
                contentPadding = PaddingValues(vertical = 16.dp) // Ajustar el padding interno según sea necesario
            ) {
                items(listaRespuestas.value) { (_, respuesta) ->
                    MessageCard(null, respuesta, navController)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

        }
    }
}

fun addAnswer(questionId: String, answerText: String, userId: String) {
    val firestore = FirebaseFirestore.getInstance()
    val answer = forumMessage(
        createdBy = userId,
        createdAt = Timestamp.now(),
        text = answerText
    )
    firestore.collection("forum").document(questionId).collection("respuestas").add(answer)
        .addOnSuccessListener {
            Log.d("Firestore", "Respuesta enviada correctamente")
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error al enviar respuesta", e)
        }
}