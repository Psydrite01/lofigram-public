package com.psydrite.lofigram.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.psydrite.lofigram.R
import com.psydrite.lofigram.ui.components.errorMessage
import com.psydrite.lofigram.utils.CurrentUserObj
import com.psydrite.lofigram.utils.MediaPlayerManager
import com.psydrite.lofigram.utils.isGestureNav


var selectedTopics by mutableStateOf(listOf<String>())
val DemoSoundDataMap = mapOf(
    "Popular Songs in Lo-Fi" to R.raw.popular_demo,
    "Lo-Fi Gaming Music" to R.raw.gaming_demo,
    "Anime Lo-Fi" to R.raw.anime_demo,
    "Natural Sounds" to R.raw.nature_demo,
    "City Life" to R.raw.urban_demo,
    "Quiet Noises" to R.raw.noises_demo,
)

@Composable
fun PersonalizePage(
    gotoSubscriptionPage: () -> Unit,
    gotoUserprofile: () -> Unit,
    updateFirstSignIn: () -> Unit,
    uploadSoundTopics: (selectedTopics: List<String>) -> Unit,
    isFirstSignIn: Boolean?,
){

    LaunchedEffect(Unit) {
        selectedTopics = emptyList()
    }

    Box(modifier = Modifier
        .fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopStart)
                .padding(top = 55.dp)
        ) {
            Text(
                "Personalize your experience",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(vertical = 20.dp)
            )
            Text(
                "Choose categories that resonate with you. These will be shown at the top of the feed.",
                color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 30.dp)
            )

            TopicCard("Popular Songs in Lo-Fi")
            TopicCard("Lo-Fi Gaming Music")
            TopicCard("Anime Lo-Fi")
            TopicCard("Natural Sounds")
            TopicCard("City Life")
            TopicCard("Quiet Noises")
        }

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(20.dp)
                .padding(bottom = if (isGestureNav) 30.dp else 60.dp)
        ){
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shadow(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(15)
                    ),
                shape = RoundedCornerShape(15),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                onClick = {
                    //logic
                    MediaPlayerManager.StopSong()

                    //if first sign in is true and google logged in, we take to subscription page, if not first sign in, we directly go to homepagema
                    if(CurrentUserObj.isGoogleLoggedIn == true){
                        CurrentUserObj.preferences = selectedTopics
                        try {
                            uploadSoundTopics(selectedTopics)
                        }catch (e: Exception){
                            errorMessage = e.message.toString()
                        }
                        if (isFirstSignIn != false){
                            updateFirstSignIn()
                            gotoSubscriptionPage()
                        }else{
                            gotoUserprofile()
                        }
                    }
                }
            ) {
                Text("Confirm")
            }
        }
    }
}

@Composable
fun TopicCard(topicname: String){
    val context = LocalContext.current
    var isPlaying = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 9.dp)
            .border(2.dp, if (topicname in selectedTopics) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary, RoundedCornerShape(20)),
        shape = RoundedCornerShape(20),
        colors = CardDefaults.cardColors(
            containerColor = if (topicname in selectedTopics) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.background
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = {
                        // logic
                        if (topicname in selectedTopics){
                            selectedTopics = selectedTopics.minus(topicname)
                            if (isPlaying.value){
                                MediaPlayerManager.StopSong()
                            }
                        }
                        else if (selectedTopics.size<8 && topicname !in selectedTopics){
                            selectedTopics = selectedTopics.plus(topicname)

                            //now play demo sound
                            MediaPlayerManager.PlaySong(context, DemoSoundDataMap[topicname], isPlaying)
                        }
                    }
                ),
            ){
            Row(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterStart),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    topicname,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (topicname in selectedTopics) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                )

                //animation
                if (isPlaying.value){
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
                        modifier = Modifier
                            .size(23.dp),
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun testpersonalizepage(){
//    PersonalizePage()
//}