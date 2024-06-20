package com.example.mushtool_fct.Model

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class FirebaseCriba {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var cribaCollection: CollectionReference

    fun setIdioma(idioma:String = "en"){
        cribaCollection = db.collection("idioma").document(idioma).collection("criba")
    }

    suspend fun getCribaSnap(): QuerySnapshot {
        return cribaCollection.get().await()
    }
}