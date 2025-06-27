package com.psydrite.lofigram.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.psydrite.lofigram.R
import kotlin.random.Random

var isGestureNav :Boolean by mutableStateOf(false)
fun isGestureNavigationEnabled(context: Context): Boolean {
    return try {
        val mode = Settings.Secure.getInt(context.contentResolver, "navigation_mode")
        mode == 2 // 2 = Gesture mode, 1 = 2-button, 0 = 3-button
    } catch (e: Settings.SettingNotFoundException) {
        false // Default to 3-button if not found
    }
}

fun isAllListsEmpty(): Boolean{
    var bool1 = WaterList.isEmpty()
    var bool2 = ColoredNoisesList.isEmpty()
    var bool3 = NatureList.isEmpty()
    var bool4 = WindList.isEmpty()
    var bool5 = UrbanList.isEmpty()
    var bool6 = InstrumentsList.isEmpty()
    var bool7 = RainstormList.isEmpty()
    var bool8 = ASMRList.isEmpty()
    var bool9 = CrowdsList.isEmpty()
    var bool10 = ElectronicsList.isEmpty()

    return bool1 && bool2 && bool3 && bool4 && bool5 && bool6 && bool7 && bool8 && bool9 && bool10
}

fun GiveListId(list: List<SoundTrack>): Int{
    if (list == FavioritesList) return 0
    if (list == PopularList) return 1
    if (list == GamingMusicList) return 2
    if (list == AnimeLoFiList) return 3
    if (list == NaturalSoundsList) return 4
    if (list == CityLifeSoundsList) return 5
    if (list == QuietNoiseSoundsList) return 6
    if (list == WaterList) return 7
    if (list == RainstormList) return 8
    if (list == NatureList) return 9
    if (list == WindList) return 10
    if (list == UrbanList) return 11
    if (list == InstrumentsList) return 12
    if (list == CrowdsList) return 13
    if (list == ColoredNoisesList) return 14
    if (list == ASMRList) return 15
    if (list == ElectronicsList) return 16

    return 0
}

fun GiveList(sound: SoundTrack): List<SoundTrack>{
    if (sound in FavioritesList) return FavioritesList
    if (sound in PopularList) return PopularList
    if (sound in GamingMusicList) return GamingMusicList
    if (sound in AnimeLoFiList) return AnimeLoFiList
    if (sound in NaturalSoundsList) return NaturalSoundsList
    if (sound in CityLifeSoundsList) return CityLifeSoundsList
    if (sound in QuietNoiseSoundsList) return QuietNoiseSoundsList
    if (sound in WaterList) return WaterList
    if (sound in RainstormList) return RainstormList
    if (sound in NatureList) return NatureList
    if (sound in WindList) return WindList
    if (sound in UrbanList) return UrbanList
    if (sound in InstrumentsList) return InstrumentsList
    if (sound in CrowdsList) return CrowdsList
    if (sound in ColoredNoisesList) return ColoredNoisesList
    if (sound in ASMRList) return ASMRList
    if (sound in ElectronicsList) return ElectronicsList

    return FavioritesList
}

fun RemoveSoundFromDefaultLists(soundTrack: SoundTrack){
    if (soundTrack in PopularList) PopularList = PopularList.minus(soundTrack)
    if (soundTrack in GamingMusicList) GamingMusicList = GamingMusicList.minus(soundTrack)
    if (soundTrack in AnimeLoFiList) AnimeLoFiList = AnimeLoFiList.minus(soundTrack)
    if (soundTrack in NaturalSoundsList) NaturalSoundsList = NaturalSoundsList.minus(soundTrack)
    if (soundTrack in CityLifeSoundsList) CityLifeSoundsList = CityLifeSoundsList.minus(soundTrack)
    if (soundTrack in QuietNoiseSoundsList) QuietNoiseSoundsList = QuietNoiseSoundsList.minus(soundTrack)
}

fun AddSoundToDefaultLists(sound: SoundTrack){
    sound.genre.forEach { genre->
        when(genre){
            "Water"-> {
                WaterList = WaterList.plus(sound)
            }
            "Colored noises"-> {
                ColoredNoisesList = ColoredNoisesList.plus(sound)
            }
            "Nature"-> {
                NatureList = NatureList.plus(sound)
            }
            "Wind"-> {
                WindList = WindList.plus(sound)
            }
            "Urban"-> {
                UrbanList = UrbanList.plus(sound)
            }
            "Instruments"-> {
                InstrumentsList = InstrumentsList.plus(sound)
            }
            "Rainstorm"->{
                RainstormList = RainstormList.plus(sound)
            }
            "ASMR"->{
                ASMRList = ASMRList.plus(sound)
            }
            "Crowds"->{
                CrowdsList = CrowdsList.plus(sound)
            }
            "Electronics"->{
                ElectronicsList = ElectronicsList.plus(sound)
            }
            "Minecraft", "Stardew Valley", "Undertale"-> {
                GamingMusicList = GamingMusicList.plus(sound)
            }
            "Anime"-> {
                AnimeLoFiList = AnimeLoFiList.plus(sound)
            }
            "Remix"-> {
                PopularList = PopularList.plus(sound)
            }
        }
    }
}

fun GiveAnimeAlbum(name: String, range: IntRange = 1..100): Int{
    if (name=="Suzume LoFi") return R.drawable.album_anime1
    if (name=="Bluebird LoFi") return R.drawable.album_anime3
    if (name=="Gurenge LoFi") return R.drawable.album_anime2
    if (name=="Unravel LoFi") return R.drawable.album_anime5
    if (name=="We are! LoFi") return R.drawable.album_anime4
    if (name=="Raise LoFi") return R.drawable.album_anime4

    val seed = name.hashCode().toLong()
    val random = Random(seed).nextInt(range.first, range.last+1)
    when(random%5+1){
        1->{return R.drawable.album_anime1}
        2->{return R.drawable.album_anime2}
        3->{return R.drawable.album_anime3}
        4->{return R.drawable.album_anime4}
        5->{return R.drawable.album_anime5}
        else -> {return R.drawable.album_anime1}
    }
}

fun GiveRemixAlbum(name: String, range: IntRange = 1..100): Int{
    val seed = name.hashCode().toLong()
    val random = Random(seed).nextInt(range.first, range.last+1)
    when(random%5+1){
        1->{return R.drawable.album_remix1}
        2->{return R.drawable.album_remix2}
        3->{return R.drawable.album_remix3}
        4->{return R.drawable.album_remix4}
        else -> {return R.drawable.album_remix5}
    }
}

//saperate function to get resource id of album art
fun getAlbumArtResourceId(soundTrack: SoundTrack): Int {
    return when(soundTrack.genre.toSet()) {
        setOf("Minecraft") -> R.drawable.album_minecraft
        setOf("Stardew Valley") -> R.drawable.album_stardewvalley
        setOf("Undertale") -> R.drawable.album_undertale

        setOf("Anime") -> {
            GiveAnimeAlbum(soundTrack.name)
        }
        setOf("Remix") -> {
            GiveRemixAlbum(soundTrack.name)
        }


        setOf("Water") -> R.drawable.album_water
        setOf("Colored noises") -> R.drawable.album_colorednoise
        setOf("Nature") -> R.drawable.album_nature
        setOf("Wind") -> R.drawable.album_wind
        setOf("Urban") -> R.drawable.album_urban
        setOf("Instruments") -> R.drawable.album_instruments
        setOf("Rainstorm") -> R.drawable.album_rainstorm
        setOf("ASMR") -> R.drawable.album_asmr
        setOf("Crowds") -> R.drawable.album_crowds
        setOf("Electronics") -> R.drawable.album_electronics

        setOf("Water", "Nature") -> R.drawable.album_waternature
        setOf("Water", "Wind") -> R.drawable.google_icon
        setOf("Water", "Rainstorm") -> R.drawable.google_icon
        setOf("Colored noises", "ASMR") -> R.drawable.google_icon
        setOf("Colored noises", "Crowds") -> R.drawable.google_icon

        setOf("Nature", "Wind") -> R.drawable.google_icon
        setOf("Nature", "Rainstorm") -> R.drawable.google_icon
        setOf("Wind", "Rainstorm") -> R.drawable.album_rainstormwind
        setOf("Urban", "Instruments") -> R.drawable.album_urbaninstruments
        setOf("Urban", "Electronics") -> R.drawable.google_icon

        setOf("Instruments", "Electronics") -> R.drawable.google_icon
        setOf("ASMR", "Crowds") -> R.drawable.google_icon

        else -> R.drawable.lofigram_logo
    }
}


@Composable
fun imageProvider(soundTrack: SoundTrack): Painter {
    val resourceId = getAlbumArtResourceId(soundTrack)
    return painterResource(id = resourceId)
}