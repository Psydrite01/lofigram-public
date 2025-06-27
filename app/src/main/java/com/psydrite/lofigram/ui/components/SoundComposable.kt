package com.psydrite.lofigram.ui.components

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.psydrite.lofigram.R
import com.psydrite.lofigram.data.remote.viewmodel.DataViewModel
import com.psydrite.lofigram.utils.CurrentUserObj
import com.psydrite.lofigram.utils.MediaNotificationService
import com.psydrite.lofigram.utils.MediaPlayerManager
import com.psydrite.lofigram.utils.ResetStateModel
import com.psydrite.lofigram.utils.SoundTrack
import com.psydrite.lofigram.utils.StateObject
import com.psydrite.lofigram.utils.imageProvider
import java.io.File

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SoundComposable(
    goto_upgradationPage:()-> Unit,
    index: Int,
    sound: SoundTrack,
    listid: Int,
    dataViewModel: DataViewModel = hiltViewModel()
){
    val context = LocalContext.current
    var isLoading = remember { mutableStateOf(false) }
    var isPlaying = remember { mutableStateOf(false) }

    var isDownloading = remember { mutableStateOf(false) }
    var isExistsInCache = remember { mutableStateOf(false) }

    LaunchedEffect(isLoading.value) {
        Log.d("cachechecking","launcheffect triggered for: "+sound.name)
        try {
            Log.d("cachechecking","checking for: "+sound.name)
            val cachedfile = File(context.cacheDir, "cached_"+sound.idname)
            if (cachedfile.exists()){
                Log.d("cachechecking","cache file exists")
                isExistsInCache.value = true
            }else{
                Log.d("cachechecking","cache file does not exist")
            }
        }catch (e: Exception){
            errorMessage = e.message.toString()
        }
    }
    when{
        StateObject.idname == sound.idname->{
            isLoading = StateObject.isLoading
            isPlaying = StateObject.isPlaying
        }
    }

    var parentHeight by remember { mutableStateOf(0) }
    Box(
        modifier = Modifier.Companion
            .onGloballyPositioned { coordinates ->
                parentHeight = coordinates.size.height
            }
    ) {
        Card(
            modifier = Modifier.Companion,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Row(

            ) {
                Column(
                    modifier = Modifier.Companion.width(130.dp)
                ) {
                    Box(
                        modifier = Modifier.Companion
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clickable(
                                onClick = {
                                    //logic
                                    ResetStateModel()

                                    StateObject.idname = sound.idname
                                    StateObject.index = index
                                    StateObject.listid = listid
                                    StateObject.isLoading = isLoading
                                    StateObject.isPlaying = isPlaying

                                    if (isPlaying.value) {
                                        MediaPlayerManager.StopSong()
                                        MediaNotificationService.stop(context)
                                    } else if (!isLoading.value) {
                                        //user trying to download the song
                                        dataViewModel.GetSoundFromDB(
                                            context,
                                            sound.idname,
                                            isLoading,
                                            isPlaying,
                                            sound
                                        )
                                    } else if (isLoading.value) {
                                        //user trying to cancel loading
                                        dataViewModel.StopLoadingSound()
                                    }
                                }
                            )
                    ) {
                        if (isPlaying.value) {
                            val composition by rememberLottieComposition(
                                spec = LottieCompositionSpec.RawRes(R.raw.musicbars_lottie),
                            )
                            val progress by animateLottieCompositionAsState(
                                composition,
                                isPlaying = isPlaying.value,
                                iterations = LottieConstants.IterateForever
                            )
                            LottieAnimation(
                                composition = composition,
                                progress = { progress },
                                modifier = Modifier.Companion
                                    .fillMaxWidth(0.25f)
                                    .align(Alignment.Companion.Center)
                                    .zIndex(2f)
                                    .aspectRatio(1f),
                            )
                        }
                        if (isLoading.value) {
                            val composition by rememberLottieComposition(
                                spec = LottieCompositionSpec.RawRes(R.raw.loading_lottie),
                            )
                            val progress by animateLottieCompositionAsState(
                                composition,
                                isPlaying = isLoading.value,
                                iterations = LottieConstants.IterateForever
                            )
                            LottieAnimation(
                                composition = composition,
                                progress = { progress },
                                modifier = Modifier.Companion
                                    .fillMaxWidth(0.5f)
                                    .align(Alignment.Companion.Center)
                                    .zIndex(2f)
                                    .aspectRatio(1f),
                            )
                        }

                        if (isPlaying.value || isLoading.value) {
                            Column(
                                modifier = Modifier.Companion
                                    .fillMaxWidth(0.35f)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .zIndex(1f)
                                    .align(Alignment.Companion.Center)
                                    .background(MaterialTheme.colorScheme.background)
                            ) { }
                        }

                        Image(
                            painter = imageProvider(sound),
                            contentDescription = "album cover",
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .padding(5.dp)
                                .align(Alignment.Companion.TopStart)
                                .clip(RoundedCornerShape(6))
                        )
                    }

                    Text(
                        sound.name,
                        modifier = Modifier.Companion
                            .padding(5.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Companion.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                AnimatedVisibility(
                    visible = isPlaying.value || isLoading.value,
                    enter = expandHorizontally(),
                    exit = shrinkHorizontally()
                ) {
                    //this column will appear on clicking
                    Column(
                        modifier = Modifier.Companion
                            .width(100.dp)
                            .height(with(LocalDensity.current) { parentHeight.toDp() }), //gets parent height
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Box(
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                                .padding(6.dp),
                        ) {
                            //download button
                            Column(
                                modifier = Modifier.Companion
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable(
                                        onClick = {
                                            if (CurrentUserObj.isGoogleLoggedIn == true){
                                                if (CurrentUserObj.subscriptionType=="Premium" || CurrentUserObj.subscriptionType=="Trialpack"){
                                                    //download sound
                                                    if (isExistsInCache.value){
                                                        //delete
                                                        MediaPlayerManager.StopSong()
                                                        try {
                                                            Log.d("cachechecking","checking for: "+sound.name)
                                                            val cachedfile = File(context.cacheDir, "cached_"+sound.idname)
                                                            cachedfile.delete()
                                                            isExistsInCache.value = false
                                                            Toast.makeText(context, "Removed from downloads", Toast.LENGTH_SHORT).show()
                                                        }catch (e: Exception){
                                                            errorMessage = e.message.toString()
                                                        }
                                                    }else{
                                                        dataViewModel.DownloadSoundToCache(context, sound.idname, isDownloading, isExistsInCache)
                                                    }
                                                }else{
                                                    goto_upgradationPage()
                                                }
                                            }
                                            else{
                                                isLoginAlert = true
                                            }
                                        }
                                    )
                                    .background(MaterialTheme.colorScheme.background)
                                    .align(Alignment.Companion.TopStart)
                            ) { }
                            when{
                                isDownloading.value->{
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .padding(10.dp)
                                            .align(Alignment.Companion.TopStart),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                isExistsInCache.value->{
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_check_circle_24),
                                        contentDescription = "download",
                                        tint = Color.Green,
                                        modifier = Modifier.Companion
                                            .size(40.dp)
                                            .padding(10.dp)
                                            .align(Alignment.Companion.TopStart)
                                    )
                                }
                                else->{
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_file_download_24),
                                        contentDescription = "download",
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.Companion
                                            .size(40.dp)
                                            .padding(10.dp)
                                            .align(Alignment.Companion.TopStart)
                                    )
                                }
                            }

                            //favorite button
                            Column(
                                modifier = Modifier.Companion
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable(
                                        onClick = {
                                            if (CurrentUserObj.isGoogleLoggedIn == true){
                                                dataViewModel.ToggleLikeDislike(context, sound)
                                            }
                                            else{
                                                isLoginAlert = true
                                            }
                                        }
                                    )
                                    .background(MaterialTheme.colorScheme.background)
                                    .align(Alignment.Companion.TopEnd)
                            ) { }
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Add to favorite",
                                tint = if (sound.idname in CurrentUserObj.likedmusic) Color.Companion.Red
                                else MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.Companion
                                    .size(40.dp)
                                    .padding(10.dp)
                                    .align(Alignment.Companion.TopEnd)
                            )
                        }

                        Text(
                            sound.desc.toString(),
                            modifier = Modifier.Companion
                                .padding(horizontal = 5.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Companion.Normal,
                            color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
                        )

                        //genre
                        Column(
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                        ) {
                            FlowRow(
                                modifier = Modifier.Companion
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                sound.genre.forEach { genre ->
                                    Box(
                                        modifier = Modifier.Companion
                                            .background(
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                shape = androidx.compose.foundation.shape.RoundedCornerShape(
                                                    16.dp
                                                )
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = androidx.compose.foundation.shape.RoundedCornerShape(
                                                    16.dp
                                                )
                                            )
                                            .padding(horizontal = 7.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = genre,
                                            color = MaterialTheme.colorScheme.primary,
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}