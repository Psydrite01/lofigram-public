package com.psydrite.lofigram.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class SoundTrack(
    val idname: String,
    val name: String,
    val genre: List<String>,
    val creator: String?,
    val desc: String?
)

var FavioritesList by mutableStateOf<List<SoundTrack>>(emptyList())

var PopularList by mutableStateOf<List<SoundTrack>>(emptyList())
var GamingMusicList by mutableStateOf<List<SoundTrack>>(emptyList())
var AnimeLoFiList by mutableStateOf<List<SoundTrack>>(emptyList())

var NaturalSoundsList by mutableStateOf<List<SoundTrack>>(emptyList())
var CityLifeSoundsList by mutableStateOf<List<SoundTrack>>(emptyList())
var QuietNoiseSoundsList by mutableStateOf<List<SoundTrack>>(emptyList())


var WaterList by mutableStateOf<List<SoundTrack>>(emptyList())
var RainstormList by mutableStateOf<List<SoundTrack>>(emptyList())
var NatureList by mutableStateOf<List<SoundTrack>>(emptyList())
var WindList by mutableStateOf<List<SoundTrack>>(emptyList())

var UrbanList by mutableStateOf<List<SoundTrack>>(emptyList())
var InstrumentsList by mutableStateOf<List<SoundTrack>>(emptyList())
var CrowdsList by mutableStateOf<List<SoundTrack>>(emptyList())

var ColoredNoisesList by mutableStateOf<List<SoundTrack>>(emptyList())
var ASMRList by mutableStateOf<List<SoundTrack>>(emptyList())
var ElectronicsList by mutableStateOf<List<SoundTrack>>(emptyList())


data class PurchaseDataType(
    var purchasetoken: String,
    var orderid: String
)

var PurchasesList by mutableStateOf<List<PurchaseDataType>>(emptyList())


data class GlobalMessage(
    var messageId: String = "",
    var message: String = "",
    var username: String = "",
    var time: Long  = System.currentTimeMillis(),
    var isPremium: Boolean = false,
    var isAnnonymous: Boolean = false
)

var messageList by mutableStateOf<List<GlobalMessage>>(emptyList())