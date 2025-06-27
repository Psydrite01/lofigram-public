package com.psydrite.lofigram.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.psydrite.lofigram.data.remote.repository.UserPreferencesRespository
import com.psydrite.lofigram.data.remote.viewmodel.DataViewModel
import com.psydrite.lofigram.ui.components.errorMessage
import com.psydrite.lofigram.ui.components.isTrialpackExpireAlert
import com.psydrite.lofigram.ui.navigation.NavScreensObject
import com.psydrite.lofigram.ui.navigation.currentPage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneId

object CurrentUserObj {
    var isLocalLoggedIn: Boolean? by mutableStateOf(null)
    var isGoogleLoggedIn: Boolean? by mutableStateOf(null)
    var username: String? by mutableStateOf(null)
    var useremail: String? by mutableStateOf(null)
    var subscriptionType: String? by mutableStateOf(null)
    var preferences: List<String> by mutableStateOf(emptyList())
    var likedmusic: List<String> by mutableStateOf(emptyList())
    var isAgreedToChat: Boolean? by mutableStateOf(false)
    var chatbotMemory: List<String> by mutableStateOf(emptyList())
}

var toUpdateSongs by mutableStateOf(false)
var forceUpdate by mutableStateOf(false)
var isCurrentUserUpdating by mutableStateOf(false)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpdateCurrentUser(
    userPreferencesRespository: UserPreferencesRespository,
    goto_homepage:()-> Unit,
    dataViewModel: DataViewModel = hiltViewModel()
){
    LaunchedEffect(currentPage, forceUpdate) {
        isCurrentUserUpdating = true
        Log.d("currentuser", "updating currentuser")
        try {
            val db= FirebaseFirestore.getInstance()
            val auth= FirebaseAuth.getInstance()
            //update from local db/ firebase
            //is userLogged in
            CurrentUserObj.isLocalLoggedIn= userPreferencesRespository.isUserLoggedIn.first()
            //is google logged in
            CurrentUserObj.isGoogleLoggedIn = userPreferencesRespository.isGoogleLogIn.first()
            // useremail
            CurrentUserObj.useremail = userPreferencesRespository.userEmail.first()
            // user name
            CurrentUserObj.username = userPreferencesRespository.userName.first()

            val user=auth.currentUser
            if (user!=null) {
                val document = db.collection("userdata").document(user.uid).get().await()

                if(document!=null){
                    CurrentUserObj.isAgreedToChat = document.getBoolean("isAgreedToChat")
                    CurrentUserObj.subscriptionType= document.get("subscriptionplan").toString()
                    if (currentPage != NavScreensObject.USER_PROFILE_PAGE){
                        CurrentUserObj.preferences = (document.get("soundpreferences") as? List<String>).orEmpty().toList()
                    }
                    CurrentUserObj.likedmusic = (document.get("likedmusic") as? List<String>).orEmpty().toList()
                }
            }else{
                //not logged in
                CurrentUserObj.subscriptionType = null
                CurrentUserObj.preferences = emptyList()
                CurrentUserObj.likedmusic = emptyList()
            }


            if (toUpdateSongs){
                Log.d("currentuser", "updating sounds")
                dataViewModel.GetSoundsData()
            }

            //if user is in Trialpack, check timestamp and update
            if (CurrentUserObj.subscriptionType=="Trialpack"){
                if (auth.currentUser != null){
                    var timeout: LocalDateTime? = null
                    val document = db.collection("userdata").document(auth.currentUser!!.uid).get().await()
                    if(document!=null){
                        timeout = document.getTimestamp("TrialPackTimeout")?.toDate()
                            ?.toInstant()
                            ?.atZone(ZoneId.systemDefault())
                            ?.toLocalDateTime()
                    }
                    val tempRef = db.collection("server_time_debug").document(auth.currentUser!!.uid)
                    tempRef.set(mapOf("timestamp" to FieldValue.serverTimestamp())).await()

                    // Read it back after write
                    val snapshot = tempRef.get().await()
                    val serverTimestamp = snapshot.getTimestamp("timestamp")

                    val localdatetime = serverTimestamp?.toDate()
                        ?.toInstant()
                        ?.atZone(ZoneId.systemDefault())
                        ?.toLocalDateTime()

                    if (timeout != null && localdatetime != null) {
                        if (timeout.isBefore(localdatetime)){
                            isTrialpackExpireAlert = true

                            //update database
                            dataViewModel.saveStringData("userdata","subscriptionplan","Basic", goto_homepage)
                        }
                    }
                }
            }
            if (CurrentUserObj.chatbotMemory==emptyList<String>() && auth.currentUser!=null){
                val document = db.collection("chatbotmemory").document(user!!.uid).get().await()
                CurrentUserObj.chatbotMemory = (document.get("usermemory") as? List<String>).orEmpty().toList()

                if (CurrentUserObj.chatbotMemory.joinToString("").length > 40000){
                    val trimmedMemory = CurrentUserObj.chatbotMemory.drop(10) //drop first 10 chats
                    db.collection("chatbotmemory")
                        .document(user.uid)
                        .update("usermemory", trimmedMemory)
                        .addOnSuccessListener {
                            CurrentUserObj.chatbotMemory = trimmedMemory
                        }
                }
            }

            isCurrentUserUpdating = false
            Log.d("currentuser", "current user updated. values: ${CurrentUserObj.isGoogleLoggedIn}, ${CurrentUserObj.useremail}")
        }catch (e: Exception){
            errorMessage = e.message.toString()
        }
    }
}