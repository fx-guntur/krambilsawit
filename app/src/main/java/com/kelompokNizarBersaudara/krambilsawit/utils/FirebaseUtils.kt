package com.kelompokNizarBersaudara.krambilsawit.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

object FirebaseUtils {
    val firebaseAuth: FirebaseAuth
        get() = Firebase.auth
    val firebaseUser: FirebaseUser?
        get() = firebaseAuth.currentUser
    val firebaseDB: FirebaseDatabase
        get() = Firebase.database
    val firebaseStorage: FirebaseStorage
        get() = Firebase.storage
}