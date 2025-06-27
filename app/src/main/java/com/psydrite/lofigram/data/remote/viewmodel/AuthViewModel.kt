package com.psydrite.lofigram.data.remote.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.psydrite.lofigram.data.remote.repository.UserPreferencesRespository
import com.psydrite.lofigram.ui.navigation.isRecentlyLoggedOut
import com.psydrite.lofigram.utils.ASMRList
import com.psydrite.lofigram.utils.AnimeLoFiList
import com.psydrite.lofigram.utils.ColoredNoisesList
import com.psydrite.lofigram.utils.CrowdsList
import com.psydrite.lofigram.utils.ElectronicsList
import com.psydrite.lofigram.utils.FavioritesList
import com.psydrite.lofigram.utils.GamingMusicList
import com.psydrite.lofigram.utils.InstrumentsList
import com.psydrite.lofigram.utils.NatureList
import com.psydrite.lofigram.utils.NetworkChecker
import com.psydrite.lofigram.utils.PopularList
import com.psydrite.lofigram.utils.RainstormList
import com.psydrite.lofigram.utils.UrbanList
import com.psydrite.lofigram.utils.WaterList
import com.psydrite.lofigram.utils.WindList
import com.psydrite.lofigram.utils.forceUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val googleAuthClient: googleAuthClient,
    private val userPreferencesRespository: UserPreferencesRespository,
    application: Application
): AndroidViewModel(application){

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Initial)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    var counterFirebaseCall by mutableIntStateOf(0)

    init {
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus(){
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                val isUserLoggedIn = userPreferencesRespository.isUserLoggedIn.first()
                val currentUser = auth.currentUser

                if(isUserLoggedIn){
                    _authState.value = AuthState.SignedIn(currentUser)
                }else{
                    userPreferencesRespository.setUserLoggedIn(false)
                    _authState.value = AuthState.SignedOut
                }
            }catch (e: Exception) {
                Log.e("AuthViewModel", "Error checking auth status", e)
                _authState.value = AuthState.Error("Failed to check authentication status")
            }
        }
    }

    fun checkGoogleLogIn(): Boolean{
        var isGoogleLogIn: Boolean = false //default value
        viewModelScope.launch {
            isGoogleLogIn= userPreferencesRespository.isGoogleLogIn.first()
        }

        return isGoogleLogIn
    }

    suspend fun signInWithGoogle(context: Context): IntentSender? {
        return try {

            //network check
            if(!NetworkChecker.isNetworkAvailable(context)){
                _authState.value = AuthState.Error("No internet connection! Please check your network and try again")
            }

            _authState.value = AuthState.Loading
            Log.d("AuthViewModel", "Started Google Login process")
            val intentSender = googleAuthClient.signIn()

            if(intentSender == null){
                _authState.value = AuthState.Error("Failed to Log in, could not initiate google sign in")
            }

            intentSender
        } catch (e: Exception){
            Log.e("AuthViewModel", "Google sign-in error", e)

            //if network error
            val errorMessage= when {
                e.message?.contains("network", ignoreCase = true) == true ->
                    "Network error occurred. Please check your internet connection."
                e.message?.contains("timeout", ignoreCase = true) == true ->
                    "Connection timeout. Please check your internet connection and try again."
                else -> "Sign-in failed: ${e.message ?: "Unknown error"}"
            }

            _authState.value = AuthState.Error(errorMessage)
            null
        }
    }

    fun handleGoogleSignInResult(intent: Intent, context: Context){
        viewModelScope.launch {
            try {

                //network check
                if(!NetworkChecker.isNetworkAvailable(context)){
                    _authState.value = AuthState.Error("No internet connection. Please check your network and try again.")
                    return@launch
                }

                _authState.value = AuthState.Loading

                val result = googleAuthClient.getSignInResultFromIntent(intent)

                if (result.data != null){
                    val currentUser = auth.currentUser
                    _authState.value = AuthState.SignedIn(currentUser)

                    //saving user in datastore
                    userPreferencesRespository.setUserLoggedIn(true)
                    userPreferencesRespository.setGoogleLogIn(true)
                    userPreferencesRespository.saveUser(
                        userId = result.data.userId,
                        userName = result.data.username ?: "",
                        userEmail = currentUser?.email ?: ""
                    )

                    //saving data to firebase
                    currentUser?.let {
                        saveUserToFireStore(it, it.displayName?: "User")
                    }

                }else{
                    val errorMessage = when {
                        result.errorMessage?.contains("network", ignoreCase = true) == true ->
                            "Network error during sign-in. Please check your connection."
                        result.errorMessage?.contains("timeout", ignoreCase = true) == true ->
                            "Sign-in timeout. Please try again."
                        else -> result.errorMessage ?: "Sign in failed"
                    }
                    _authState.value = AuthState.Error(errorMessage)
                }
            }catch (e: Exception){
                Log.e("AuthViewModel", "Error handling Google sign-in result", e)
                val errorMessage = when {
                    e.message?.contains("network", ignoreCase = true) == true ->
                        "Network error occurred during sign-in."
                    !NetworkChecker.isNetworkAvailable(context) ->
                        "No internet connection. Please check your network."
                    else -> e.message ?: "Unknown error occurred"
                }

                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }

    suspend fun saveUserToFireStore(user: FirebaseUser, displayName: String){
        val db= FirebaseFirestore.getInstance()

        val preexistingdata = db.collection("userdata").document(auth.currentUser!!.uid).get().await()
        val datamap = preexistingdata.data?.toMutableMap() ?: mutableMapOf()

        val userModel = mapOf(
            "userName" to displayName.ifEmpty { user.displayName ?: "User" },
            "userEmail" to user.email
        )
        datamap.putAll(userModel)

        Log.d("AuthViewModel", "Saving user to firestore now")

        counterFirebaseCall++
        db.collection("userdata").document(user.uid).set(datamap, SetOptions.merge())
            .addOnSuccessListener{
                Log.d("AuthViewModel", "Firebase user sucessfully saved, count $counterFirebaseCall")
            }
            .addOnFailureListener {e->
                Log.d("AuthViewModel", "Failed to save user to Firestore", e)
            }
    }

    suspend fun checkIfFirstLogin(): Boolean? {
        var temp: Boolean? = null
        try {
            val db = FirebaseFirestore.getInstance()

            counterFirebaseCall++
            val document = db.collection("userdata").document(auth.currentUser!!.uid).get().await()
            if (document != null && document.exists()) {
                Log.d("AuthViewmodel", "checking first login, document: ${document.data}")
                temp = document.get("isFirstSignIn") as Boolean
                Log.d("AuthViewmodel", "checking first login: $temp")
            } else {
                temp = true
            }
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error checking first login", e)
        }
        return temp
    }

    fun updateFirstSignIn(){
        val user = auth.currentUser
        val db = FirebaseFirestore.getInstance()
        user?.let {
            user?.let {
                db.collection("userdata").document(it.uid).update("isFirstSignIn", false)
                    .addOnSuccessListener {
                        Log.d("AuthViewModel", "First sign in sucessfully updated")
                    }
                    .addOnFailureListener {
                            e->
                        Log.d("AuthViewModel", "First sign in updation failed")
                    }
            }
        }
    }

    fun updateDatastoreForLocalLogin(){
        //this function is to be used for people who choose to continue without logging in
        viewModelScope.launch {
            userPreferencesRespository.setUserLoggedIn(true)
            userPreferencesRespository.setGoogleLogIn(false)
            userPreferencesRespository.saveUser(
                userId = "",
                userName = "User",
                userEmail = "Not provided"
            )
            Log.d("AuthViewModel", "Local Datastore updated with non signed in user")
        }
    }

    suspend fun checkPremiumUserOrNot(): Boolean? {
        //this function is used to check if user is premium or not
        var isPremium: Boolean? = false
        try {
            val user= auth.currentUser?: return null
            val db= FirebaseFirestore.getInstance()

            counterFirebaseCall++
            val document = db.collection("userdata").document(user.uid).get().await()
            if(document!=null && document.exists()){
                isPremium=document.getBoolean("premiumUser") ?: false
            }else{
                isPremium=false
            }
        }catch (e: Exception) {
            Log.e("AuthViewModel", "Error is premium or not", e)
        }

        return isPremium
    }

    fun signOut(){
        viewModelScope.launch {
            try {
                //empty all the lists
                FavioritesList = emptyList()

                PopularList = emptyList()
                GamingMusicList = emptyList()
                AnimeLoFiList = emptyList()

                WaterList = emptyList()
                ColoredNoisesList = emptyList()
                NatureList = emptyList()
                WindList = emptyList()
                UrbanList = emptyList()
                InstrumentsList = emptyList()
                RainstormList = emptyList()
                ASMRList = emptyList()
                CrowdsList = emptyList()
                ElectronicsList = emptyList()

                _authState.value = AuthState.Loading
                auth.signOut()

                userPreferencesRespository.setUserLoggedIn(false)
                userPreferencesRespository.clearUserData()


                _authState.value = AuthState.SignedOut
                forceUpdate = !forceUpdate //failsafe for updateCurrentUser
            }catch (e: Exception) {
                Log.e("AuthViewModel", "Error signing out", e)
                _authState.value = AuthState.Error(e.message ?: "Sign-out failed")
            }finally {
                isRecentlyLoggedOut = true
            }
        }
    }

    fun resetAuthState(){
        _authState.value = AuthState.Initial
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    object SignedOut : AuthState()
    data class SignedIn(val user: FirebaseUser?) : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class UpdateState {
    object Initial : UpdateState()
    object Loading : UpdateState()
    object Success : UpdateState()
    data class Error(val message: String) : UpdateState()
}

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val username: String?
)