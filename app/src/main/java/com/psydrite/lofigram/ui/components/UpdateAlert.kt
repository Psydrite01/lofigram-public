package com.psydrite.lofigram.ui.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.darkColorScheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.psydrite.lofigram.BuildConfig
import com.psydrite.lofigram.R


var isUpdateAlert by mutableStateOf(false)
@Composable
fun UpdateAlert(){
    val context = LocalContext.current
    if (isUpdateAlert){
        AlertDialog(
            onDismissRequest = {
                isUpdateAlert = false
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
                        append("New version available!\n")
                        withStyle(style = MaterialTheme.typography.titleSmall.toSpanStyle()
                            .copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(0.65f)
                            )) {
                            append("\nGet the latest features and experiences today.")
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
                            context.openAppInPlayStore()
                        }) {
                        Text(
                            text = "Update",
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
                            isUpdateAlert = false
                        }) {
                        Text(text = "Not now", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        )
    }
}

private fun Context.openAppInPlayStore() {
    val appId = BuildConfig.APPLICATION_ID //gets the application IdAdd commentMore actions
    try {
        //trying to open in play store
        val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appId"))
        playStoreIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(playStoreIntent)
    }catch (e: ActivityNotFoundException) {
        //else, opening in good old browser
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appId"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}

@Preview(showBackground = true)
@Composable
fun testupdatealert(){
    val customColorScheme = darkColorScheme(
        primary = Color(0xff006ffd),            //buttons and all
        onPrimary = Color.Black,             //text on buttons etc

        secondary = Color(0xff202020),           //different shade of primary
        onSecondary = Color.Yellow,    //glowing color, for premium effect

        tertiary = Color(0xff282828),           //some cards and borders

        background = Color(0xff0e0e0e),
        onBackground = Color.White,           //text on background

    )

    MaterialTheme(
        colorScheme = customColorScheme,
        ) {
        isUpdateAlert = true
        UpdateAlert()
    }
}