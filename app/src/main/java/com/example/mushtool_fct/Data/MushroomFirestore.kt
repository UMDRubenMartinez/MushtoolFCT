package com.example.mushtool_fct.Data

import com.google.firebase.firestore.GeoPoint
import java.sql.Timestamp

data class MushroomFirestore(
    val description:String = "",
    val id:String = "",
    val imageDefectPath:String = "",
    val images:Array<String> ,
    val locations:Array<GeoPoint>,
    val name:String = "",
    val timestamp: Timestamp,
    val userId:String = "",

)
