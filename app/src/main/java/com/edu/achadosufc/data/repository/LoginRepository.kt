package com.edu.achadosufc.data.repository

import android.content.Context
import com.edu.achadosufc.R
import com.edu.achadosufc.data.SessionManager
import com.edu.achadosufc.data.model.GoogleTokenRequest
import com.edu.achadosufc.data.model.Login
import com.edu.achadosufc.data.model.LoginResponse
import com.edu.achadosufc.data.service.AuthService
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class LoginRepository(
    context: Context,
    private val api: AuthService
) {
    private val sessionManager: SessionManager = SessionManager(context)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun login(
        username: String,
        password: String,
    ): LoginResponse? {

        val response = api.login(Login(username, password))
        val loginResponse = response.body()

        if (loginResponse != null) {
            sessionManager.saveAuthToken(loginResponse.access_token)
        }

        return loginResponse
    }

    suspend fun resetPassword(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun loginWithGoogle(idToken: String): LoginResponse? {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await()

        val response = api.loginWithGoogle(GoogleTokenRequest(idToken))
        val loginResponse = response.body()

        if (loginResponse != null) {
            sessionManager.saveAuthToken(loginResponse.access_token)
        }

        return loginResponse
    }

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun isUserLogged(): Boolean {
        return auth.currentUser != null
    }
}