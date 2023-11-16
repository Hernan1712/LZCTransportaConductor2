package com.optic.LZCTransportaConductor.providers

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.optic.LZCTransportaConductor.models.Driver
import java.io.File
import com.google.android.gms.tasks.Tasks


class DriverProvider {

    val db = Firebase.firestore.collection("Drivers")
    var storage = FirebaseStorage.getInstance().getReference().child("profile")

    fun create(driver: Driver): Task<Void> {
        return db.document(driver.id!!).set(driver)
    }

    fun uploadImage(id: String, file: File): StorageTask<UploadTask.TaskSnapshot> {
        var fromFile = Uri.fromFile(file)
        val ref = storage.child("$id.jpg")
        storage = ref
        val uploadTask = ref.putFile(fromFile)

        return uploadTask.addOnFailureListener {
            Log.d("STORAGE", "ERROR: ${it.message}")
        }
    }

    fun createToken(idDriver: String, onComplete: (String) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                updateToken(idDriver, token)
                onComplete(token)
            }
        }
    }

    fun updateToken(idDriver: String, token: String): Task<Void> {
        val map: MutableMap<String, Any> = HashMap()
        map["token"] = token
        return db.document(idDriver).update(map)
    }

    fun getImageUrl(): Task<Uri> {
        return storage.downloadUrl
    }

    fun getDriver(idDriver: String): Task<DocumentSnapshot> {
        return db.document(idDriver).get()
    }

    fun update(driver: Driver): Task<Void> {
        val map: MutableMap<String, Any> = HashMap()
        driver.name?.let { map["name"] = it }
        driver.lastname?.let { map["lastname"] = it }
        driver.plateNumber?.let { map["plateNumber"] = it }
        driver.image?.let { map["image"] = it }
        driver.token?.let { map["token"] = it } // Incluir el campo 'token'

        // Verificar si hay cambios antes de actualizar
        if (map.isNotEmpty()) {
            return db.document(driver.id!!).update(map)
        } else {
            // No hay cambios para actualizar
            // Puedes lanzar una tarea completada inmediatamente si no hay cambios
            return Tasks.forResult(null)
        }
    }



}