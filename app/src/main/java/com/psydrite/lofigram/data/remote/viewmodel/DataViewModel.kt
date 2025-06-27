package com.psydrite.lofigram.data.remote.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.psydrite.lofigram.data.remote.repository.FirebaseRepository
import com.psydrite.lofigram.data.remote.repository.UserPreferencesRespository
import com.psydrite.lofigram.ui.components.errorMessage
import com.psydrite.lofigram.ui.screens.isLoadingMap
import com.psydrite.lofigram.utils.CurrentUserObj
import com.psydrite.lofigram.utils.MediaPlayerManager
import com.psydrite.lofigram.utils.SoundTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

var activeSoundJob: Job? = null
var activeSoundIsLoading: MutableState<Boolean>? = null

@HiltViewModel
class DataViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val googleAuthClient: googleAuthClient,
    private val userPreferencesRespository: UserPreferencesRespository,
    application: Application
): AndroidViewModel(application){
    val FirebaseRepository = FirebaseRepository(
        _auth = FirebaseAuth.getInstance(),
        _db = FirebaseFirestore.getInstance()
    )

    fun GetSoundsData(){
        viewModelScope.launch {
            FirebaseRepository.GetSoundsData()
        }
    }

    fun saveStringData(collection: String, entry: String, data: String, goto: ()-> Unit = {}){
        viewModelScope.launch {
            FirebaseRepository.saveStringData(collection, entry, data, goto)
        }
    }

    fun checkNullSubscription(goto: () -> Unit){
        viewModelScope.launch {
            FirebaseRepository.checkNullSubscription(goto)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun GetSoundFromDB(context: Context, name: String, isLoading: MutableState<Boolean>, isPlaying: MutableState<Boolean>, track: SoundTrack){
        activeSoundJob?.cancel()

        activeSoundIsLoading?.value = false
        activeSoundIsLoading = isLoading

        //if user is premium/trial, check local
        if ( CurrentUserObj.isGoogleLoggedIn == true && (CurrentUserObj.subscriptionType == "Premium" || CurrentUserObj.subscriptionType == "Trialpack")){
            activeSoundJob = viewModelScope.launch {
                try {
                    val cachedfile = File(context.cacheDir, "cached_"+name)
                    if (cachedfile.exists()){
                        MediaPlayerManager.PlaySoundFromFile(cachedfile, isPlaying, isLoading, track, context)
                    }else{
                        FirebaseRepository.StreamSound(context, name, isLoading, isPlaying, track)
                    }
                }catch (e: Exception){
                    errorMessage = e.message.toString()
                }
            }
        }else{
            activeSoundJob = viewModelScope.launch {
                FirebaseRepository.StreamSound(context, name, isLoading, isPlaying, track)
            }
        }
    }

    fun DownloadSoundToCache(context: Context, name:String, isLoading: MutableState<Boolean>, isExistsInCache: MutableState<Boolean>){
        viewModelScope.launch {
            FirebaseRepository.DownloadSoundToCache(context, name, isLoading,isExistsInCache)
        }
    }

    fun StopLoadingSound(){
        Log.d("sounds", "trying to stop loading")
        activeSoundJob?.cancel()
        activeSoundJob = null
        activeSoundIsLoading?.value = false
        activeSoundIsLoading = null
        MediaPlayerManager.StopSong()
    }

    suspend fun getSubscriptionType(): String{

        //currently this logic works, but it does not update real time, will think about it later

        return FirebaseRepository.checkSubScriptionType()

    }

    suspend fun getUserSoundTopics(): List<String>{
        return withContext(Dispatchers.IO) {
            FirebaseRepository.getUserSoundTopics()
        }
    }

    fun uploadSoundTopics(soundList: List<String>){
        viewModelScope.launch {
            FirebaseRepository.setUserSoundTopics(soundList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveTimeOut(collection: String, entry: String) {
        viewModelScope.launch {
            FirebaseRepository.saveTimeOut(collection, entry)
        }
    }

    fun ToggleLikeDislike(context: Context, sound: SoundTrack){
        viewModelScope.launch {
            FirebaseRepository.ToggleLikeDislike(context, sound)
        }
    }

    fun NewReq(context: Context, request: String, link: String, todo: () -> Unit){
        viewModelScope.launch {
            FirebaseRepository.NewReq(context, request, link, todo)
        }
    }

    suspend fun getAppVersion(): Long{
        return FirebaseRepository.getAppVersion()
    }

    fun updateAppVersion(version: Long){
        viewModelScope.launch {
            FirebaseRepository.updateAppVersion(version)
        }
    }

    fun ApplyCoupon(context: Context, code: String){
        viewModelScope.launch {
            FirebaseRepository.ApplyCoupon(context, code)
        }
    }

    fun DownloadScreenSaverToCache(context: Context, name:String){
        if (isLoadingMap[name] == true){

            return
        }else{
            screenSaverDownloadingJob = viewModelScope.launch {
                FirebaseRepository.DownloadScreenSaverToCache(context, name)
            }
        }
    }
}
var screenSaverDownloadingJob : Job? by mutableStateOf(null)