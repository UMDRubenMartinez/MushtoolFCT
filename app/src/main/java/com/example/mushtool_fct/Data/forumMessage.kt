package com.example.mushtool_fct.Data

import com.google.firebase.Timestamp

data class forumMessage(
    val id:String = "",
    val createdBy:String = "",
    val createdAt:Timestamp = Timestamp.now(),
    val text: String = "")