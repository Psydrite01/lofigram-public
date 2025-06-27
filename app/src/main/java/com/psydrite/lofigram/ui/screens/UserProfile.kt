package com.psydrite.lofigram.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.psydrite.lofigram.data.remote.viewmodel.PomodoroTimerViewModel
import com.psydrite.lofigram.data.remote.viewmodel.SleepTimerViewModel
import com.psydrite.lofigram.ui.components.ShimmerBrush
import com.psydrite.lofigram.ui.components.isReportIssueAlert
import com.psydrite.lofigram.ui.components.isSongReqAlert
import com.psydrite.lofigram.utils.CurrentUserObj
import com.psydrite.lofigram.utils.MediaNotificationService
import com.psydrite.lofigram.utils.MediaPlayerManager

@Composable
fun UserProfile(
    onSignOutClick: ()-> Unit,
    onSoundPreferencesEditClick: () -> Unit,
    getSoundPreferences: suspend ()-> List<String>,
    pomodoroTimerViewModel: PomodoroTimerViewModel,
    sleepTimerViewModel: SleepTimerViewModel,
    requestReview: () -> Unit
){

    var soundPreferences by remember{ mutableStateOf<List<String>>(emptyList()) }

    //launched effect for soundpreferences
    LaunchedEffect(Unit) {
        soundPreferences=getSoundPreferences()
    }

    val context=LocalContext.current



    Box(
        modifier = Modifier
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
                .verticalScroll(
                rememberScrollState())
                .padding(bottom = 200.dp)
        ) {
            //header Text
            Text("Your Profile", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp))
            //account info section
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            ) {
                if (CurrentUserObj.isGoogleLoggedIn == true){
                    Text("Account Information", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 10.dp))
                    //content
                    //username
                    ProfileCard(
                        title = "Username",
                        content = CurrentUserObj.username.toString()
                    )
                    //useremail
                    ProfileCard(
                        title = "Email",
                        content = CurrentUserObj.useremail.toString()
                    )
                    //subscription
                    ProfileCard(
                        title = "Subscription",
                        content = CurrentUserObj.subscriptionType.toString()
                    )
                }else{
                    //if user is not logged in
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .height(140.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            MaterialTheme.colorScheme.secondary
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 5.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = "Login to start using amazing features!",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground)
                            //login button
                            Button(
                                onClick = onSignOutClick,
                                shape = RoundedCornerShape(16.dp),
//                                modifier = Modifier.weight(0.8f)
                            ) {
                                Text("Login", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

            }
            if (CurrentUserObj.isGoogleLoggedIn == true){
                //only for signed in users
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                ) {

                    Text("Reach out to us", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 10.dp))
                    //content
                    ClickableProfileCard("Request your favorite track ❤️",{ isSongReqAlert = true })
                    ClickableProfileCard("Share your experience ⭐⭐⭐⭐⭐",{
                        requestReview()
                    })
                    ClickableProfileCard("Report an Issue ",{ isReportIssueAlert=true })
                }

                //sound preferences
                if(CurrentUserObj.isGoogleLoggedIn==true){
                    Spacer(Modifier.height(16.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                    ) {
                        Text("Your Sound Preferences", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 10.dp))
                        //content
                        //for testing just displaying text
                        SoundPreferencesCard(
                            onEditClick = onSoundPreferencesEditClick
                        )
                    }
                }


                Spacer(Modifier.height(16.dp))
                //button row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //signout button
                    Button(
                        onClick = {
                            //stopping songs on signout
                            MediaPlayerManager.StopSong()
                            //stopping notification
                            MediaNotificationService.stop(context)
                            //stopping any pomodoro timer
                            pomodoroTimerViewModel.stopTimer()
                            //stopping sleep timer
                            sleepTimerViewModel.stopTimer()
                            onSignOutClick()
                        },
                        modifier = Modifier
//                            .weight(0.8f)
                            .fillMaxWidth(0.4f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Sign out", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onError)
                    }
                }
            }else{
                Card (modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 9.dp)
                    .padding(top = 20.dp),
                    shape = RoundedCornerShape(5),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary.copy(0.3f)
                    )
                ){
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            "Login to unlock these features:",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .padding(bottom = 10.dp)
                        )
                        FeatureComposable("Personalized music categories and own favorites list🎶", "PremiumTrialpackBasic")
                        FeatureComposable("Global chat access: Make new friends or talk with our AI Chatbot! 🤖","PremiumTrialpackBasic")
                        FeatureComposable("Anonymous chat mode 🥷: Your identity remains hidden until you choose to reveal it.", "PremiumTrialpackBasic")
                        FeatureComposable("Request your favorite song personally ❤️: Yes you read that right!","PremiumTrialpackBasic")
                    }
                }
            }

        }
    }
}

//to be used for profile information
@Composable
fun ProfileCard(title: String, content: String){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .border(1.dp, brush =
                if (content == "Trialpack" || content == "Premium") ShimmerBrush(Color.Transparent)
                else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                RoundedCornerShape(12.dp))
        ,shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.secondary
        ),
//        elevation = CardDefaults.cardElevation(
//            defaultElevation = 5.dp
//        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            val shimmer = ShimmerBrush(MaterialTheme.colorScheme.onBackground)
            if (content == "Trialpack" || content == "Premium"){
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        brush = shimmer
                    ),
                    fontWeight = FontWeight.Medium,
//                color = MaterialTheme.colorScheme.onBackground,
                )
            }else{
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}

@Composable
fun ClickableProfileCard(title: String, onClick: () -> Unit){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable(
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.secondary
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

//to be used for status information of profile
@Composable
fun ProfileStatusCard(title: String){
    Card(
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                if (CurrentUserObj.isGoogleLoggedIn == true){
                    lerp(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background,
                        0.25f
            )}else{
                    lerp(
                        MaterialTheme.colorScheme.errorContainer,
                        MaterialTheme.colorScheme.background,
                        0.25f)
                }

        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if(CurrentUserObj.isGoogleLoggedIn==true){
                Text("✓", color = MaterialTheme.colorScheme.primary)
            }else{
                Text("✗", color= MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SoundPreferencesCard(
    onEditClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.secondary
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            //header for sound preferences
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sound Preferences", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                //for now I am keeping two edit button, one for preferences, one for profile, can merge later if into one button if wanted
                Button(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "Edit", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            //if empty list
            when{
                CurrentUserObj.preferences.isEmpty() -> {
                    Text(
                        text = "No preferences set", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                else->{
                    //displaying as tags
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CurrentUserObj.preferences.forEach {
                            preference->
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                            ) {
                                Text(
                                    text = preference,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}