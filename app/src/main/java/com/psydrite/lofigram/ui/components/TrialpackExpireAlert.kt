package com.psydrite.lofigram.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.psydrite.lofigram.R


var isTrialpackExpireAlert by mutableStateOf(false)
@Composable
fun TrialpackExpireAlert(
    goto_upgradepage:()-> Unit
){
    val context = LocalContext.current
    if (isTrialpackExpireAlert){
        AlertDialog(
            onDismissRequest = {
                isTrialpackExpireAlert = false
            },
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.shadow(
                elevation = 24.dp,
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
            shape = RectangleShape,
            title = {
                Box(){
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        Image(
                            painter = painterResource(R.drawable.update_alert),
                            contentDescription = "album cover",
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                        )
                    }
                }
            },
            text = {
                Text(
                    text = buildAnnotatedString {
                        append("Oops! your Trialpack has ended\n")
                        withStyle(style = MaterialTheme.typography.titleSmall.toSpanStyle()
                            .copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(0.65f)
                            )) {
                            append("\nEnjoyed Premium? Get it today!")
                        }
                    },
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(top = 48.dp)
                )
            },
            confirmButton = {
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){

                    Button(
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
//                            .weight(1f)
                            .fillMaxWidth(0.4f)
                            .padding(vertical = 10.dp)
                            .padding(top = 16.dp)
//                            .height(38.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                    )
                                ),
                                shape = RoundedCornerShape(18.dp)
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        onClick = {
                            goto_upgradepage()
                            isTrialpackExpireAlert = false
                        }) {
                        Text(
                            text = "Upgrade",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Button(
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onBackground.copy(0.7f)
                        ),
                        onClick = {
                            isTrialpackExpireAlert = false
                        }) {
                        Text(text = "Not now", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        )
    }
}