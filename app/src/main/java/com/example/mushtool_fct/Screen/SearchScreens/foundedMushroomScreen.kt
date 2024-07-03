package com.example.mushtool_fct.Screen.SearchScreens

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.mushtool_fct.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun foundedMushroomScreen(navController: NavController) {
    val context = LocalContext.current

    val photoUriState = remember { mutableStateOf<Uri?>(null) }
    val photoFile = remember { mutableStateOf<File?>(null) }
    val commentState = remember { mutableStateOf("") }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            photoFile.value?.let { file ->
                val photoURI = FileProvider.getUriForFile(
                    context,
                    "com.example.mushtool_fct.fileprovider",
                    file
                )
                photoUriState.value = photoURI
            }
        }
    }

    val takePhoto = {
        val file = createImageFile(context)
        file?.also {
            val photoURI = FileProvider.getUriForFile(
                context,
                "com.example.mushtool_fct.fileprovider",
                it
            )
            cameraLauncher.launch(photoURI)
            photoFile.value = it
        }
    }

    val savePhotoToFirebase = {
        val storage = FirebaseStorage.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        val photoFile = photoFile.value
        val photoUri = photoUriState.value
        val comment = commentState.value

        if (photoFile != null && photoUri != null) {
            val fileName = "${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference.child("images/$fileName")

            storageRef.putFile(photoUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val photoData = hashMapOf(
                            "url" to uri.toString(),
                            "date" to Timestamp.now(),
                            "comment" to comment
                        )

                        firestore.collection("mushPhotos")
                            .add(photoData)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Foto guardada con éxito", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Error al guardar la foto: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error al subir la foto: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.MushtoolWeb)) },
                backgroundColor = Color(0xFF8BC34A),
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5DC)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            photoUriState.value?.let { uri ->
                Image(
                    painter = rememberImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp) // Ajusta el tamaño de la imagen aquí
                        .padding(16.dp), // Agrega padding para separar la imagen del botón
                    contentScale = ContentScale.Crop
                )
            }



            Button(onClick = { takePhoto() }) {
                Text(text = "Abrir Cámara")
            }
            TextField(
                value = commentState.value,
                onValueChange = { commentState.value = it },
                label = { Text("Comentario") },
                modifier = Modifier.padding(16.dp)
            )
            if (photoUriState.value != null) {
                Button(onClick = { savePhotoToFirebase() }) {
                    Text(text = "Guardar")
                }
            }
        }
    }
}

@Throws(IOException::class)
private fun createImageFile(context: Context): File? {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )
}