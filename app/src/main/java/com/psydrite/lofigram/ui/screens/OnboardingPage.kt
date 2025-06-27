package com.psydrite.lofigram.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.psydrite.lofigram.utils.isGestureNav
import com.psydrite.lofigram.R


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingPage(
    goto_welcomepage:()-> Unit
){
    var currentSlide by remember { mutableStateOf(1) }

    var slidetext by remember { mutableStateOf("") }
    var slidesubtext by remember { mutableStateOf("") }

    when(currentSlide){
        2->{
            slidetext = "Stream Lofi Music Anytime"
            slidesubtext = "Curated calm tunes to relax, study, or vibe."
        }
        3->{
            slidetext = "Built-in Pomodoro & Sleep Timers"
            slidesubtext = "Focus deeply or drift off peacefully — with helpful reminders."
        }
        4->{
            slidetext = "Global Chat, Your Way"
            slidesubtext = "Talk to strangers, use anonymous mode, or chat with our AI buddy."
        }
        5->{
            slidetext = "Customize Your Calm"
            slidesubtext = "Choose your mood — anime lofi, nature sounds, gaming vibes, and more."
        }
        6->{
            slidetext = "Want More Chill? Try Premium."
            slidesubtext = "Get premium and unlock downloads, glow effects, and more."
        }
        else -> {
            slidetext = "Welcome to Lofigram: Your Calm Space"
            slidesubtext = "Chill with lofi music, focus deeply, and reconnect with yourself."
        }
    }
    Box(modifier = Modifier
        .fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopStart)
        ) {

            //the columns are the image placeholders for now
            AnimatedContent(
                targetState = currentSlide,
                transitionSpec = {
                    slideInHorizontally{ width -> width } with
                            slideOutHorizontally{ width -> -width }
                }
            ) { slideno->
                when(slideno){
                    2->{
                        Column (
                            Modifier.fillMaxWidth().fillMaxHeight(0.6f)
                        ){
                            Image(
                                painter = painterResource(R.drawable.onboarding2),
                                contentDescription = "onboarding",
                                contentScale = ContentScale.FillWidth,
                                alignment = Alignment.BottomCenter
                            )
                        }
                    }
                    3->{
                        Column (
                            Modifier.fillMaxWidth().fillMaxHeight(0.6f)
                        ){
                            Image(
                                painter = painterResource(R.drawable.onboarding3),
                                contentDescription = "onboarding",
                                contentScale = ContentScale.FillWidth,
                                alignment = Alignment.BottomCenter
                            )
                        }
                    }
                    4->{
                        Column (
                            Modifier.fillMaxWidth().fillMaxHeight(0.6f)
                        ){
                            Image(
                                painter = painterResource(R.drawable.onboarding4),
                                contentDescription = "onboarding",
                                contentScale = ContentScale.FillWidth,
                                alignment = Alignment.BottomCenter
                            )
                        }
                    }
                    5->{
                        Column (
                            Modifier.fillMaxWidth().fillMaxHeight(0.6f)
                        ){
                            Image(
                                painter = painterResource(R.drawable.onboarding5),
                                contentDescription = "onboarding",
                                contentScale = ContentScale.FillWidth,
                                alignment = Alignment.BottomCenter
                            )
                        }
                    }
                    6->{
                        Column (
                            Modifier.fillMaxWidth().fillMaxHeight(0.6f)
                        ){
                            Image(
                                painter = painterResource(R.drawable.onboarding6),
                                contentDescription = "onboarding",
                                contentScale = ContentScale.FillWidth,
                                alignment = Alignment.BottomCenter
                            )
                        }
                    }
                    else -> {
                        Column (
                            Modifier.fillMaxWidth().fillMaxHeight(0.6f)
                        ){
                            Image(
                                painter = painterResource(R.drawable.onboarding1),
                                contentDescription = "onboarding",
                                contentScale = ContentScale.FillWidth,
                                alignment = Alignment.BottomCenter
                            )
                        }
                    }
                }
            }


            //dots
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (currentSlide==1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(0.2f))
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (currentSlide==2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(0.2f))
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (currentSlide==3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(0.2f))
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (currentSlide==4) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(0.2f))
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (currentSlide==5) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(0.2f))
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (currentSlide==6) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(0.2f))
                )
            }

            Text(
                slidetext,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(20.dp))
            Text(
                slidesubtext,
                color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            )
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
                    if (currentSlide == 6){
                        goto_welcomepage()
                    }else{
                        currentSlide= currentSlide+1
                    }
                }
            ) {
                Text("Next")
            }
        }
    }
}