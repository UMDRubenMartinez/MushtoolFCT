package com.example.mushtool_fct.Model

import android.content.ContentValues
import android.util.Log
import com.example.mushtool_fct.Entity.Criba
import com.example.mushtool_fct.Entity.Mushroom
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

    suspend fun getCribaPairList(): List<Pair<Int, Criba>>{
        try {
            val snapshot: QuerySnapshot = getCribaSnap()
            val cribaList = mutableListOf<Pair<Int, Criba>>()
            for (document in snapshot.documents) {
                val criba = document.toObject(Criba::class.java)
                if (criba != null) {
                    var id: Int? = document.id.toIntOrNull()
                    if(id != null) {
                        cribaList.add(Pair(id, criba))
                    }
                    Log.d("mushFirebase","${id}")
                }
            }
            return cribaList?: mutableListOf<Pair<Int, Criba>>(Pair(0,Criba()))
        } catch (e: Exception) {
            Log.w(ContentValues.TAG, "Error obteniendo documentos.", e)
            return mutableListOf()
        }
    }

    suspend fun getCribaMushroomPairList(mushroomList: List<Pair<Int, Mushroom>>): List<Pair<Int, Criba>>{
        //val conectionFirebaseMush = FirebaseMushroom()
        //val fetchedMushList = conectionFirebaseMush.getMushroomPairList()

        val mapList = getCribaPairList().toMap()

        /*val result = mushroomList.mapNotNull { (key, mushroom) ->
            mapList[key]?.apply {
                setMush(mushroom)
            }
        }*/
        return mushroomList.mapNotNull  { (key, mushroom) ->
            mapList[key]?.let { criba ->
                criba.setMush(mushroom)
                key to criba
            }
        }
    }
}