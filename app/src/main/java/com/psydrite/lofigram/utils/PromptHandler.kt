package com.psydrite.lofigram.utils

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.psydrite.lofigram.data.remote.viewmodel.MessageViewmodel
import com.psydrite.lofigram.data.remote.viewmodel.MessageViewmodel.GenerationState

@Composable
fun PromptHandler(
    messageViewmodel: MessageViewmodel = hiltViewModel()
){
    val state by messageViewmodel.generationState.collectAsState()

    when (state) {
        is GenerationState.Idle -> {}
        is GenerationState.Loading -> {}
        is GenerationState.Success -> {
            Log.d("generativeai", "generated prompt successfullly. trying to send to global chat")
            messageViewmodel.SendMessage(
                GlobalMessage(
                    messageId = "",
                    message = (state as GenerationState.Success).result,
                    username = "Yumiko AI",
                    time = System.currentTimeMillis()
                )
            )
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val docref = FirebaseFirestore.getInstance()
                .collection("chatbotmemory")
                .document(userId)
                .set(
                    mapOf("usermemory" to FieldValue.arrayUnion("Yumiko AI: "+(state as GenerationState.Success).result)),
                    SetOptions.merge()
                )
            }
        is GenerationState.Error -> {
            Log.d("generativeai", "error")
        }
    }
}