package com.kelompokNizarBersaudara.krambilsawit.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object FirebaseUtils {
    val firebaseAuth: FirebaseAuth
        get() = Firebase.auth
    val firebaseUser: FirebaseUser?
        get() = firebaseAuth.currentUser
}