package com.psydrite.lofigram.ui.components

import android.content.Context
import android.content.Intent
import android.provider.Settings
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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.psydrite.lofigram.R

var showNetworkErrorAlert by mutableStateOf(false)

@Composable
fun NetworkErrorAlert() {

    val context = LocalContext.current
    if (showNetworkErrorAlert){

        AlertDialog(
            onDismissRequest = {
                showNetworkErrorAlert = false
            },
            containerColor = lerp(
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.onBackground,
                0.05f
            ),
            modifier = Modifier.shadow(
                elevation = 24.dp,
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(20.dp),
            title = {
                Box(){
                    Column (
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(
                            "Oops! Looks like you don't have internet",
                            modifier = Modifier
                                .padding(bottom = 30.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Image(
                            painter = painterResource(R.drawable.networkalert_vector),
                            contentDescription = "album cover",
                            modifier = Modifier.Companion
                                .fillMaxWidth(0.9f)
                                .padding(vertical = 10.dp)
                                .padding(bottom = 30.dp)
                        )
                    }
                }
            },
            text = {
                Text(
                    text = "Check your internet connection and try again 🛜",
                    color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
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
                            .fillMaxWidth(0.8f)
                            .padding(vertical = 10.dp)
                            .padding(top = 6.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                        MaterialTheme.colorScheme.primary
                                    )
                                ),
                                shape = RoundedCornerShape(18.dp)
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        onClick = {
                            context.openNetworkSettings()
                        }) {
                        Text(
                            text = "Open Internet Settings ", //can add settings emoji
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Button(
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(vertical = 10.dp)
                            .padding(top = 6.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                        MaterialTheme.colorScheme.primary
                                    )
                                ),
                                shape = RoundedCornerShape(18.dp)
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        onClick = {
                            showNetworkErrorAlert = false
                        }) {
                        Text(
                            text = "Retry",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        )

    }
}

fun Context.openNetworkSettings(){
    try {
        val intent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }catch (e: Exception){
        //could not launch wireless settings for some reason, opening entire settings
        val intent= Intent(Settings.ACTION_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
