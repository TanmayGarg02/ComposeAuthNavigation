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

    /**
     * Checks if a user is already signed in to Firebase.
     */
    fun getSignedInUser(): UserData? {
        return auth.currentUser?.run {
            UserData(
                userId = uid,
                username = displayName,
                profilePictureUrl = photoUrl?.toString()
            )
        }
    }

    /**
     * 1. Launches the Google Sign-In Bottom Sheet.
     * 2. Gets the token from Google.
     * 3. Exchanges that token with Firebase to sign in.
     */
    suspend fun signIn(): SignInResult {
        try {
            // A. Build the Google Sign-In Request
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // Show all accounts, not just previously used ones
                .setServerClientId("92975366539-t64vhsh9vagmcdfbqb31m0avp5ls7l08.apps.googleusercontent.com") // <--- PASTE YOUR WEB CLIENT ID HERE
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            // B. Open the Bottom Sheet and wait for user selection
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            // C. Parse the result to get the Google ID Token
            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleToken = googleIdTokenCredential.idToken

                // D. Authenticate with Firebase
                val authCredential = GoogleAuthProvider.getCredential(googleToken, null)
                val firebaseUser = auth.signInWithCredential(authCredential).await().user

                // E. Return Success
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
            // User cancelled or no credentials found
            Log.e("GoogleAuth", "Sign in failed: ${e.message}")
            return SignInResult(data = null, errorMessage = e.message)
        } catch (e: Exception) {
            // Generic error (network, firebase, etc)
            if (e is CancellationException) throw e
            return SignInResult(data = null, errorMessage = e.message)
        }
    }

    suspend fun signOut() {
        try {
            // 1. Clear the OS saved credential state
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            // 2. Sign out of Firebase
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

// Data classes for easy UI consumption
data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?
)