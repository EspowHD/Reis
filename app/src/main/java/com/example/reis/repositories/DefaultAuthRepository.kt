package com.example.reis.repositories

import com.example.reis.data.entities.User
import com.example.reis.other.Resource
import com.example.reis.other.safeCall
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DefaultAuthRepository : AuthRepository {

    val auth = FirebaseAuth.getInstance()
    val users = FirebaseFirestore.getInstance().collection("users")

    override suspend fun login(email: String, password: String): Resource<AuthResult> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                Resource.Success(result)
            }
        }
    }

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): Resource<AuthResult> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid!!
                val user = User(uid, username)
                users.document(uid).set(user).await()
                Resource.Success(result)
            }
        }
    }

    override suspend fun checkValidUsername(username: String): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val isValid = users.whereEqualTo("username", username)
                        .orderBy("uid").get().await()
                        .toObjects(User::class.java).isEmpty()
                Resource.Success(isValid)
            }
        }
    }
}