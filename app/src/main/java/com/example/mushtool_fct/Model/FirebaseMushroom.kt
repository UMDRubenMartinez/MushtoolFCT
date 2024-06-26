package com.example.mushtool_fct.Model

import android.util.Log
import android.content.ContentValues
import com.example.mushtool_fct.Entity.Mushroom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class FirebaseMushroom {
    private val db = FirebaseFirestore.getInstance()
    private val mushroomCollection: CollectionReference = db.collection("bolets")
    private val currentUserUid: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    suspend fun getMushroomSnap(): QuerySnapshot {
        return mushroomCollection.get().await()
    }

    suspend fun getMushroomPairList(): List<Pair<Int, Mushroom>>{
        try {
            val snapshot: QuerySnapshot = getMushroomSnap()
            val mushroomList = mutableListOf<Pair<Int, Mushroom>>()
            for (document in snapshot.documents) {
                val mushroom = document.toObject(Mushroom::class.java)
                if (mushroom != null) {
                    var id: Int? = document.id.toIntOrNull()
                    if(id != null) {
                        mushroomList.add(Pair(id, mushroom))
                    }
                    Log.d("mushFirebase","${id}")
                }
            }
            return mushroomList?: mutableListOf<Pair<Int, Mushroom>>(Pair(0,Mushroom()))
        } catch (e: Exception) {
            Log.w(ContentValues.TAG, "Error obteniendo documentos.", e)
            return mutableListOf()
        }
    }
}