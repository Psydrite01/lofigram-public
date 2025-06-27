package com.psydrite.lofigram.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
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
import java.io.File


@OptIn(ExperimentalLayoutApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScreenSaverPage(
    goto_player: () -> Unit,
    goto_back:() ->Unit
){
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        isCachedMap.forEach { string, boolean->
            if (isLoadingMap[string] ==false){
                val cachedfile = File(context.cacheDir, "cached_"+string)
                if (cachedfile.exists()){
                    isCachedMap[string] = true
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopStart)
                .padding(top = 55.dp)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(bottom = 200.dp)
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                //back button
                ElevatedButton(
                    onClick = { goto_back() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .padding(end = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground.copy(0.08f),
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                //header Text
                Text(
                    "Screensavers",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            //content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    maxItemsInEachRow = 2,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ScreenSaverComposable("Cat in rain", "17 MB", R.drawable.screensaver_ss1, "ss_1.mp4", goto_player)
                    ScreenSaverComposable("Totoro sleeping", "42 MB", R.drawable.screensaver_ss2, "ss_2.mp4", goto_player)
                    ScreenSaverComposable("Night city", "76 MB", R.drawable.screensaver_ss3, "ss_3.mp4", goto_player)
                    ScreenSaverComposable("Munchlax retro", "14 MB", R.drawable.screensaver_ss4, "ss_4.mp4", goto_player)
                    ScreenSaverComposable("Driver girl", "23 MB", R.drawable.screensaver_ss5, "ss_5.mp4", goto_player)
                    ScreenSaverComposable("Yukino anime girl", "60 MB", R.drawable.screensaver_ss6, "ss_6.mp4", goto_player)
                }
            }
        }
    }
}

val isLoadingMap = mutableStateMapOf(
    "ss_1.mp4" to false,
    "ss_2.mp4" to false,
    "ss_3.mp4" to false,
    "ss_4.mp4" to false,
    "ss_5.mp4" to false,
    "ss_6.mp4" to false
)
val isCachedMap = mutableStateMapOf(
    "ss_1.mp4" to false,
    "ss_2.mp4" to false,
    "ss_3.mp4" to false,
    "ss_4.mp4" to false,
    "ss_5.mp4" to false,
    "ss_6.mp4" to false
)
val progressMap = mutableStateMapOf(
    "ss_1.mp4" to 1.0,
    "ss_2.mp4" to 1.0,
    "ss_3.mp4" to 1.0,
    "ss_4.mp4" to 1.0,
    "ss_5.mp4" to 1.0,
    "ss_6.mp4" to 1.0
)



@Composable
fun ScreenSaverComposable(
    name: String,
    filesize: String,
    image: Int,
    filename: String,
    goto_player:()-> Unit,
    dataViewModel: DataViewModel = hiltViewModel()
){
    var context = LocalContext.current

    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth(0.45f),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
                .clip(RoundedCornerShape(10.dp))
                .clickable(
                    onClick = {
                        if (isLoadingMap[filename] == false){
                            val cachedfile = File(context.cacheDir, "cached_"+filename)
                            if (cachedfile.exists()){
                                //if exists continue
                                currentScreenSaver = filename
                                goto_player()
                            }
                            else{
                                //if does not exist in cache, download
                                dataViewModel.DownloadScreenSaverToCache(context, filename)
                            }
                        }else{
                            //cancel downloaing

                        }
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxHeight()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Box(){
                    //progress indicator
                    if (isLoadingMap[filename] == true){
                        Column (
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(100f/ (progressMap[filename]?.toFloat() ?: 100.0f))
                                .zIndex(1f)
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(MaterialTheme.colorScheme.primary.copy(0.7f), MaterialTheme.colorScheme.secondary.copy(0.7f)),
                                        start = androidx.compose.ui.geometry.Offset(0f, 0f), // top left
                                        end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY) // bottom right
                                    ))
                        ){}
                    }

                    Image(
                        painter = painterResource(image),
                        contentDescription = "screen saver preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .zIndex(0f)
                            .clip(RoundedCornerShape(5.dp))
                    )
                    if (isLoadingMap[filename] == true || isCachedMap[filename] == true){
                        Column(
                            modifier = Modifier.Companion
                                .fillMaxWidth(0.40f)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .zIndex(2f)
                                .align(Alignment.Companion.Center)
                                .background(MaterialTheme.colorScheme.background)
                        ) { }
                    }
                    val composition by rememberLottieComposition(
                        spec = LottieCompositionSpec.RawRes(R.raw.loading_lottie),
                    )
                    val progress by animateLottieCompositionAsState(
                        composition,
                        isPlaying = true,
                        iterations = LottieConstants.IterateForever
                    )
                    if (isLoadingMap[filename] == true){
                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.Companion
                                .fillMaxWidth(0.6f)
                                .align(Alignment.Companion.Center)
                                .zIndex(2f)
                                .aspectRatio(1f),
                        )
                    }
                    if (isCachedMap[filename] == true){
                        Icon(
                            painter = painterResource(R.drawable.baseline_check_circle_24),
                            contentDescription = "downloaded",
                            tint = Color.Green,
                            modifier = Modifier.Companion
                                .fillMaxSize(0.85f)
                                .zIndex(2f)
                                .align(Alignment.Companion.Center)
                        )
                    }
                }
                Text(name)
                Text(filesize)
            }
        }
    }
}


@Composable
fun GhostComposable(){
    Column(
        modifier = Modifier
            .fillMaxWidth(0.45f)
            .height(1.dp)
    ) {  }
}