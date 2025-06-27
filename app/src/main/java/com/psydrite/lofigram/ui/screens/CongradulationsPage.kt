package com.psydrite.lofigram.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.psydrite.lofigram.R
import com.psydrite.lofigram.utils.isGestureNav
import com.psydrite.lofigram.utils.purchaseType

@Composable
fun CongradulationsPage(
    goto_homepage:()-> Unit
){
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
                "Congratulations!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(vertical = 20.dp)
            )
            Text(
                text = if (purchaseType == "Premium") "Enjoy your Premium Subscription!" else "Yoo-hoo! Premium is now active for 24 Hours",
                color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 30.dp)
            )

            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                val composition by rememberLottieComposition(
                    spec = LottieCompositionSpec.RawRes(R.raw.tick_lottie),
                )
                val progress by animateLottieCompositionAsState(
                    composition,
                    isPlaying = true,
                    iterations = 1
                )
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.Companion,
                )
            }
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
                    goto_homepage()
                }
            ) {
                Text("Continue")
            }
        }
    }
}