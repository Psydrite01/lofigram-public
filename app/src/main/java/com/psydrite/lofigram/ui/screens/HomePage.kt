package com.psydrite.lofigram.ui.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.psydrite.lofigram.data.remote.viewmodel.DataViewModel
import com.psydrite.lofigram.ui.components.SoundComposable
import com.psydrite.lofigram.ui.navigation.NavScreensObject
import com.psydrite.lofigram.ui.navigation.currentPage
import com.psydrite.lofigram.utils.AnimeLoFiList
import com.psydrite.lofigram.utils.CityLifeSoundsList
import com.psydrite.lofigram.utils.CurrentUserObj
import com.psydrite.lofigram.utils.FavioritesList
import com.psydrite.lofigram.utils.GiveListId
import com.psydrite.lofigram.utils.NaturalSoundsList
import com.psydrite.lofigram.utils.QuietNoiseSoundsList
import com.psydrite.lofigram.utils.SoundTrack
import com.psydrite.lofigram.utils.GamingMusicList
import com.psydrite.lofigram.utils.PopularList
import com.psydrite.lofigram.utils.StateObject
import com.psydrite.lofigram.utils.isAllListsEmpty
import java.time.LocalTime
import com.psydrite.lofigram.BuildConfig
import com.psydrite.lofigram.R
import com.psydrite.lofigram.data.remote.viewmodel.MessageViewmodel
import com.psydrite.lofigram.ui.components.ShimmerBrush
import com.psydrite.lofigram.ui.components.isUpdateAlert
import com.psydrite.lofigram.utils.messageList


var areSoundsLoading by mutableStateOf(true)
var toShowUpdateAlert by mutableStateOf(true)

@OptIn(ExperimentalLayoutApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomePage(
    dataViewModel: DataViewModel = hiltViewModel(),
    goto_subscriptionpage: () -> Unit,
    goto_upgradationPage: () -> Unit,
    messageViewmodel: MessageViewmodel = hiltViewModel(),
){
    var isPremium : Boolean by remember { mutableStateOf(false) }
    LaunchedEffect(CurrentUserObj.subscriptionType) {
        if (CurrentUserObj.subscriptionType == "Premium" || CurrentUserObj.subscriptionType == "Trialpack"){
            isPremium = true
        }
        else{
            isPremium = false
        }
    }
    val appVersion = BuildConfig.VERSION_CODE

    // todo: done, no need to move to mainactivity or cache this !!!!!!!!!!!!!!!!!!!
    LaunchedEffect(Unit) {
        if (toShowUpdateAlert){
            val firebaseVersion= dataViewModel.getAppVersion()
            if(appVersion.toLong()<firebaseVersion){
                isUpdateAlert=true
            }else if (appVersion.toLong() > firebaseVersion){
                dataViewModel.updateAppVersion(appVersion.toLong())
            }
            toShowUpdateAlert = false
        }
    }

    LaunchedEffect(Unit) {
        if (isAllListsEmpty())
        {
            dataViewModel.GetSoundsData()
        }

        //if subscription type is null. go to subscription page
        dataViewModel.checkNullSubscription(goto_subscriptionpage)

    }

    LaunchedEffect(Unit) {
        if (!areMessagesLoading && messageList.isEmpty())
        messageViewmodel.ReceiveMessages()
    }

    var time = LocalTime.now()
    var timestring = "Hello there!"
    var emoji = "😊"

    when (time.hour) {
        in 0..4 -> {
            timestring = "Burning the midnight oil? "
            emoji = "🌙"
        }
        in 5..10 -> {
            timestring = "Good morning, early bird! "
            emoji = "☀️"
        }
        in 11..13 -> {
//            timestring = "Hey there, good late morning "
            timestring= "Hey there, Getting ready for the afternoon rush? "
            emoji = "🌤️"
        }
        in 14..17 -> {
            timestring = "Hope your afternoon's going well "
            emoji = "☕"
        }
        in 18..20 -> {
            timestring = "Time to relax... maybe some tea? "
            emoji = "🍵"
        }
        in 21..23 -> {
            timestring = "Winding down for the night? "
            emoji = "🌃"
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            brush = Brush.verticalGradient(colors = listOf(
                lerp(
                    MaterialTheme.colorScheme.background,
                    MaterialTheme.colorScheme.primary,
                    0.2f
                ),
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.background
            ))
        )
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopStart)
                .padding(top = 55.dp)
                .verticalScroll(state = rememberScrollState())
        ) {
            if (!isPremium){
                Text(
                    "Lofigram",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.65f),
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp)
                )
            }

            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth(if (isPremium) 1f else 0.7f)
                        .padding(vertical = 20.dp)
                        .align(Alignment.TopStart)
                ) {
                    if (isPremium){
                        Text(
                            text = buildAnnotatedString {
                                append("Hello ")
                                withStyle(
                                    SpanStyle(brush = ShimmerBrush(MaterialTheme.colorScheme.onBackground))) {
                                    append(CurrentUserObj.username)
                                }
                                if (CurrentUserObj.subscriptionType == "Premium"){
                                    withStyle(SpanStyle(baselineShift = BaselineShift(0.21f))) {
                                        append(" 👑")
                                    }
                                }
                            },
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .padding(vertical = 10.dp)
                        )
                    }
                    Text(
                        text = buildAnnotatedString {
                            append(timestring)
                            withStyle(SpanStyle(baselineShift = BaselineShift(0.21f))) {
                                append(emoji)
                            }
                        },
                        style = if (isPremium) MaterialTheme.typography.titleMedium
                            else MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(if (isPremium) 0.65f else 1f),
                        modifier = Modifier
                    )
                }

                if (CurrentUserObj.subscriptionType!="Premium"){
                    Column (
                        modifier = Modifier
                            .border(
                                1.dp,
                                brush = ShimmerBrush(
                                    MaterialTheme.colorScheme.onSecondary,
                                    MaterialTheme.colorScheme.onBackground
                                ),
                                RoundedCornerShape(30)
                            )
                            .clickable(
                                onClick = {
                                    goto_upgradationPage()

                                }
                            )
                            .align(Alignment.CenterEnd)
                    ){
                        Text(
                            text = "Upgrade",
                            modifier = Modifier
                                .padding(11.dp),
                            style = MaterialTheme.typography.titleSmall.copy(
                                brush = ShimmerBrush(MaterialTheme.colorScheme.onSecondary ,MaterialTheme.colorScheme.onBackground)
                            )
                        )
                    }
                }
            }


            //todo store the last three heard songs in local db and list it here (will be implemented in future)
//            if (false){
//                FlowRow(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    maxLines = 2,
//                    maxItemsInEachRow = 3,
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    SoundComposable()
//                    SoundComposable()
//                    SoundComposable()
//                }
//            }

            if (areSoundsLoading){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val composition by rememberLottieComposition(
                        spec = LottieCompositionSpec.RawRes(R.raw.loadingfiles_lottie),
                    )
                    val progress by animateLottieCompositionAsState(
                        composition,
                        isPlaying = areSoundsLoading,
                        iterations = LottieConstants.IterateForever
                    )
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.Companion
                            .fillMaxWidth(0.8f)
                            .alpha(0.5f)
                            .aspectRatio(1f),
                    )
                }
            }else{
                ListComposable(goto_upgradationPage, "Favorites ❤️", FavioritesList)

                when{
                    !CurrentUserObj.preferences.isNullOrEmpty() -> {
                        CurrentUserObj.preferences?.let { prefs ->
                            if ("Popular Songs in Lo-Fi" in prefs) {
                                ListComposable(goto_upgradationPage, "Popular Songs in Lo-Fi", PopularList)
                            }
                            if ("Lo-Fi Gaming Music" in prefs) {
                                ListComposable(goto_upgradationPage, "Lo-Fi Gaming Music", GamingMusicList)
                            }
                            if ("Anime Lo-Fi" in prefs) {
                                ListComposable(goto_upgradationPage, "Anime Lo-Fi", AnimeLoFiList)
                            }
                            if ("Natural Sounds" in prefs) {
                                ListComposable(goto_upgradationPage, "Natural Sounds", NaturalSoundsList)
                            }
                            if ("City Life" in prefs) {
                                ListComposable(goto_upgradationPage, "City Life", CityLifeSoundsList)
                            }
                            if ("Quiet Noises" in prefs) {
                                ListComposable(goto_upgradationPage, "Quiet Noises", QuietNoiseSoundsList)
                            }
                            if ("Popular Songs in Lo-Fi" !in prefs) {
                                ListComposable(goto_upgradationPage, "Popular Songs in Lo-Fi", PopularList)
                            }
                            if ("Lo-Fi Gaming Music" !in prefs) {
                                ListComposable(goto_upgradationPage, "Lo-Fi Gaming Music", GamingMusicList)
                            }
                            if ("Anime Lo-Fi" !in prefs) {
                                ListComposable(goto_upgradationPage, "Anime Lo-Fi", AnimeLoFiList)
                            }
                            if ("Natural Sounds" !in prefs) {
                                ListComposable(goto_upgradationPage, "Natural Sounds", NaturalSoundsList)
                            }
                            if ("City Life" !in prefs) {
                                ListComposable(goto_upgradationPage, "City Life", CityLifeSoundsList)
                            }
                            if ("Quiet Noises" !in prefs) {
                                ListComposable(goto_upgradationPage, "Quiet Noises", QuietNoiseSoundsList)
                            }
                        }
                    }
                    else->{
                        ListComposable(goto_upgradationPage, "Popular Songs in Lo-Fi", PopularList)
                        ListComposable(goto_upgradationPage, "Lo-Fi Gaming Music", GamingMusicList)
                        ListComposable(goto_upgradationPage, "Anime Lo-Fi", AnimeLoFiList)
                        ListComposable(goto_upgradationPage, "Natural Sounds", NaturalSoundsList)
                        ListComposable(goto_upgradationPage, "City Life", CityLifeSoundsList)
                        ListComposable(goto_upgradationPage, "Quiet Noises", QuietNoiseSoundsList)
                    }
                }
            }



            Spacer(Modifier.height(66.dp))

            if (!areSoundsLoading){
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(
                        "Made with ❤️ by Prudam and Biprangshu\nat PsydriteStudios",
                        color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .padding(bottom = 30.dp)
                    )
                }
            }

            Spacer(Modifier.height(150.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListComposable(
    goto_upgradationPage:()-> Unit,
    name: String,
    list: List<SoundTrack>
){
    val listid = GiveListId(list)
    if (list.isNotEmpty()){
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ){
            Text(
                name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(start = 20.dp)
                    .padding(vertical = 10.dp)
            )

            val lazyState = rememberLazyListState()
            //to scroll to unit on page change
            LaunchedEffect(currentPage) {
                if (currentPage == NavScreensObject.HOME_PAGE){
                    if (StateObject.isPlaying.value || StateObject.isLoading.value){
                        if (listid == StateObject.listid){
                            lazyState.animateScrollToItem(StateObject.index)
                        }
                    }
                }
            }

            LazyRow (
                state = lazyState,
                modifier = Modifier
                    .padding(start = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ){
                itemsIndexed(list) { index, sound ->
                    SoundComposable(goto_upgradationPage, index, sound, listid)
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun testhomepage(){
//    HomePage()
//}