package com.example.mushtool_fct.Model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mushtool_fct.Data.Users
import com.example.mushtool_fct.Data.forumMessage
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MessagesViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<forumMessage>>(emptyList())
    val messages: StateFlow<List<forumMessage>> get() = _messages

    private val firestore = FirebaseFirestore.getInstance()

    private var messagesLoaded = false

    fun loadMessages() {
        if (!messagesLoaded) {
            viewModelScope.launch {
                val firestore = FirebaseFirestore.getInstance()
                val collectionRef = firestore.collection("forum")
                val userRef = firestore.collection("users")
                val updatedMessages = mutableListOf<forumMessage>()

                collectionRef.get()
                    .addOnSuccessListener { querySnapshot ->
                        val mensajes = querySnapshot.documents.mapNotNull { documentSnapshot ->
                            val mensaje = documentSnapshot.toObject(forumMessage::class.java)
                            mensaje?.copy(id = documentSnapshot.id)
                        }

                        mensajes.forEach { mensaje ->
                            val userId = mensaje.createdBy
                            userRef.document(userId).get()
                                .addOnSuccessListener { userSnapshot ->
                                    val user = userSnapshot.toObject(Users::class.java)
                                    val username = user?.Nombre ?: "Unknown User"
                                    updatedMessages.add(mensaje.copy(createdBy =username))
                                    if (updatedMessages.size == mensajes.size) {
                                        _messages.value = updatedMessages
                                        messagesLoaded = true
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    // Handle failure
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure
                    }
            }
        }
    }

    fun addMessage(newMessage: forumMessage) {
        _messages.value = _messages.value + newMessage
    }

    fun addAnswer(questionId: String, newAnswerText: String, userId: String) {
        val answerData = hashMapOf(
            "createdBy" to userId,
            "createdAt" to Timestamp.now(),
            "text" to newAnswerText
        )

        firestore.collection("forum")
            .document(questionId)
            .collection("respuestas")
            .add(answerData)
            .addOnSuccessListener { documentReference ->

            }
            .addOnFailureListener { e ->
                // Manejar error al guardar la respuesta
            }
    }


}
