package com.psydrite.lofigram.utils

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.psydrite.lofigram.R
import com.psydrite.lofigram.ui.components.errorMessage
import java.io.File

object MediaPlayerManager {
    private var mediaPlayer: MediaPlayer? = null

    private var previousSongBoolean = mutableStateOf<Boolean>(true)

    private var onPlaybackStateChanged: ((Boolean) -> Unit)? = null


    fun PlaySong(context: Context, id: Int?, isPlaying: MutableState<Boolean>){
        StopSong()   //stop any other

        mediaPlayer = MediaPlayer.create(context, id?:R.raw.testsound).apply {
            start()
            isPlaying.value = true
            previousSongBoolean = isPlaying
            setOnCompletionListener {
                isPlaying.value = false
                release()
            }
        }
    }

    fun StopSong() {
        previousSongBoolean.value = false
        mediaPlayer?.let {
            try {
                if (it.isPlaying) {
                    it.stop()
                }
            } catch (e: IllegalStateException) {
                //player not in a valid state
            }
            it.release()
            mediaPlayer = null
            MediaNotificationService.updatePlaybackState(false)
            onPlaybackStateChanged?.invoke(false)
        }
    }

    //need to add resumablity functionality

    @RequiresApi(Build.VERSION_CODES.O)
    fun PlaySoundFromUrl(context: Context, url: String, isPlaying: MutableState<Boolean>, isLoading: MutableState<Boolean>, track: SoundTrack){
        StopSong()

        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(url)
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                isLooping = true
                prepareAsync()
                setOnPreparedListener {
                    start()
                    isPlaying.value = true
                    isLoading.value = false
                    previousSongBoolean = isPlaying

                    //to start notification service
                    MediaNotificationService.start(context, track, true)
                    onPlaybackStateChanged?.invoke(true)
                }
                setOnCompletionListener {
                    if (!isLooping){
                        isPlaying.value = false
                        release()
                        mediaPlayer = null
                    }
                }
            }catch (e: Exception){
                errorMessage = e.message.toString()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun PlaySoundFromFile(
        file: File,
        isPlaying: MutableState<Boolean>,
        isLoading: MutableState<Boolean>,
        track: SoundTrack,
        context: Context
    ) {
        StopSong()

        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(file.absolutePath)
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                isLooping = true
                prepareAsync()

                setOnPreparedListener {
                    start()
                    isPlaying.value = true
                    isLoading.value = false
                    previousSongBoolean = isPlaying

                    MediaNotificationService.start(context, track, true)
                    onPlaybackStateChanged?.invoke(true)
                }

                setOnCompletionListener {
                    if (!isLooping) {
                        isPlaying.value = false
                        release()
                        mediaPlayer = null
                        onPlaybackStateChanged?.invoke(false)
                    }
                }

            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.e("MediaPlayer", "Error: ${e.message}")
            }
        }
    }
}