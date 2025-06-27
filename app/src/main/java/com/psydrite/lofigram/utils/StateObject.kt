package com.psydrite.lofigram.utils

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object StateObject {
    var idname by mutableStateOf("")
    var index by mutableStateOf(0)
    var listid by mutableStateOf(0)
    var isLoading: MutableState<Boolean> = mutableStateOf(false)
    var isPlaying: MutableState<Boolean> = mutableStateOf(false)
}

fun ResetStateModel(){
    Log.d("StateObject","id = ${StateObject.idname}, loading = ${StateObject.isLoading.value}, playing = ${StateObject.isPlaying.value}")

    Log.d("StateObject","state reset")
    StateObject.idname = ""
    StateObject.index = 0
    StateObject.listid = 0
    StateObject.isLoading = mutableStateOf(false)
    StateObject.isPlaying = mutableStateOf(false)
}