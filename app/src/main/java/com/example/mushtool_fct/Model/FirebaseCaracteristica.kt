package com.example.mushtool_fct.Model

import android.content.ContentValues
import android.util.Log
import com.example.mushtool_fct.Entity.Caracteristica
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class FirebaseCaracteristica {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var caracteristicaCollection: CollectionReference

    fun setIdioma(idioma:String = "en"){
        caracteristicaCollection = db.collection("idioma").document(idioma).collection("caracteristica")
    }

    suspend fun getCaracteristicaSnap(idDocument: String = ""): DocumentSnapshot? {
        return caracteristicaCollection.document(idDocument).get().await()
    }

    suspend fun getCaracteristicaWithID(idDocument: Int): Caracteristica {
        val idDocumentJSON = idDocument.toString()
        try {
            val snapshot: DocumentSnapshot? = getCaracteristicaSnap(idDocumentJSON)
            var caracteristica = snapshot?.toObject(Caracteristica::class.java)
            return caracteristica ?: throw Exception("No se encontró la característica.")
        } catch (e: Exception) {
            Log.w(ContentValues.TAG, "Error obteniendo documentos.", e)
            return Caracteristica()
        }
    }
}