package com.example.authapp.auth

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class GoogleAuthClient(
    private val context: Context
) {
    private val auth = Firebase.auth
    private val credentialManager = CredentialManager.create(context)

    fun getSignedInUser(): UserData? {
        return auth.currentUser?.run {
            UserData(
                userId = uid,
                username = displayName,
                profilePictureUrl = photoUrl?.toString()
            )
        }
    }

    suspend fun signIn(): SignInResult {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("92975366539-t64vhsh9vagmcdfbqb31m0avp5ls7l08.apps.googleusercontent.com")
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleToken = googleIdTokenCredential.idToken

                val authCredential = GoogleAuthProvider.getCredential(googleToken, null)
                val firebaseUser = auth.signInWithCredential(authCredential).await().user

                return SignInResult(
                    data = firebaseUser?.run {
                        UserData(
                            userId = uid,
                            username = displayName,
                            profilePictureUrl = photoUrl?.toString()
                        )
                    },
                    errorMessage = null
                )
            } else {
                return SignInResult(data = null, errorMessage = "Unknown credential type")
            }

        } catch (e: GetCredentialException) {
            Log.e("GoogleAuth", "Sign in failed: ${e.message}")
            return SignInResult(data = null, errorMessage = e.message)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            return SignInResult(data = null, errorMessage = e.message)
        }
    }

    suspend fun signOut() {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?
)