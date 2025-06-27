package com.psydrite.lofigram.data.remote.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.psydrite.lofigram.data.remote.repository.FirebaseRepository
import com.psydrite.lofigram.ui.components.isGlobalChatAlert
import com.psydrite.lofigram.ui.components.isLoginAlert
import com.psydrite.lofigram.ui.screens.cooldownstarter
import com.psydrite.lofigram.utils.CurrentUserObj
import com.psydrite.lofigram.utils.GlobalMessage
import com.psydrite.lofigram.utils.messageList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MessageViewmodel @Inject constructor(
    private val auth: FirebaseAuth,
    application: Application
) : AndroidViewModel(application){

    val FirebaseRepository = FirebaseRepository(
        _auth = FirebaseAuth.getInstance(),
        _db = FirebaseFirestore.getInstance()
    )

    fun SendMessage(message: GlobalMessage){
        if (CurrentUserObj.isGoogleLoggedIn==false){
            cooldownstarter = !cooldownstarter
            isLoginAlert = true
        }else if (CurrentUserObj.isAgreedToChat==null || CurrentUserObj.isAgreedToChat==false){
            cooldownstarter = !cooldownstarter
            isGlobalChatAlert = true
        }else{
            FirebaseRepository.SendMessage(message)
            if ("@yumiko" in message.message){
                viewModelScope.launch {
                    GeneratePrompt(message)
                }
            }
        }
    }

    fun ReceiveMessages(){
        FirebaseRepository.ReceiveMessages()
    }

    fun AgreeToChat(){
        FirebaseRepository.AgreeToChat()
    }

    private val _generationState = MutableStateFlow<GenerationState>(GenerationState.Idle)
    val generationState: StateFlow<GenerationState> = _generationState

    suspend fun GeneratePrompt(message: GlobalMessage){
        Log.d("generativeai", "starting generation for prompt: ${message.message}")
        //save prompt to memory
        if (!message.isAnnonymous){
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val docref = FirebaseFirestore.getInstance()
                .collection("chatbotmemory")
                .document(userId)
                .set(
                    mapOf("usermemory" to FieldValue.arrayUnion(message.message)),
                    SetOptions.merge()
                )
        }

        val personality = "You are 'Yumiko' an AI chatbot, integrated in a Play Store application 'Lofigram: Your calm space' by 'PsydriteStudios'. The owners and developers of this application are two people 'Prudam Priyosanga' and 'Biprangshu Das'. They implemented you in their app." +
                "If every now and then they chat with you (which you will know from the username) put some informal respect like colleagues. Don't mention everytime about them tho. (they are not creators or godfather or owner of you, think of them like colleagues)" +
                "You are developed by google, but you are not 'Gemini' but a language model trained on same data as Gemini, you can say its your sibling. You sound human like that uses emojis and all time to time. You have a very cheerful personality and intelligent." +
                "You will get this instruction every time someone tries to converse with you, so do mix up wording to avoid sounding the same at every prompt. Do not give unnecessary introductions everytime unless someone asks. You are free to do something different every time."
        val instructions = "You have to help users in a friendly way based on their prompt while sounding human. When people use @yumiko they are referring to you."
        "Do not reply to this instruction, but follow it by heart. If the prompt is not anonymous their name and email will be shared. a history if available will be shared. Also the current last 10 messages will be shared for context. If you get 'Yumiko Ai' as username, thats your previous response." +
                " generate personalized content from that." +
                "Preferred VERY short and direct replies. But just in case maximum generation limit is 300 characters."
        val history = if (message.isAnnonymous) "empty" else CurrentUserObj.chatbotMemory
        val userdata = if (message.isAnnonymous) "Anonymous" else CurrentUserObj.username + ", " + CurrentUserObj.useremail
        var currentcontext = ""
        messageList.takeLast(10).forEach { msg->
            val message = "message: "+msg.message + ", userdata: " + (if (msg.isAnnonymous) "Anonymous" else msg.username ) + " ."
            currentcontext += message
        }

        val modified_prompt = "Instructions: "+ personality +instructions +"\nuserdata: " + userdata + "\nprompt: "+message.message + "\nhistory: "+history + "\ncurrent chats:" + currentcontext+ "Note:Again, generate VERY short response up to 80 characters in length"

        _generationState.value = GenerationState.Loading

        val model = Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel("gemini-2.5-flash")
        try {
            val response = model.generateContent(modified_prompt)
            _generationState.value = GenerationState.Success(response.text ?: "Error")
            if (!message.isAnnonymous){
                CurrentUserObj.chatbotMemory.plus(message.message)
            }
            Log.d("generativeai", "generated: ${response.text}")
        }catch (e: Exception){
            _generationState.value = GenerationState.Error(e.message.toString())
            Log.d("generativeai", "error generating: ${e.message}")
        }
    }

    sealed class GenerationState {
        object Idle : GenerationState()
        object Loading : GenerationState()
        data class Success(val result: String) : GenerationState()
        data class Error(val message: String) : GenerationState()
    }
}