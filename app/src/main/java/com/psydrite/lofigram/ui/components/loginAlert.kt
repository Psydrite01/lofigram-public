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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.psydrite.lofigram.R


var isLoginAlert by mutableStateOf(false)
@Composable
fun loginAlert(
    gotoLoginPage: () -> Unit
){
    if (isLoginAlert){
        AlertDialog(
            onDismissRequest = {
                isLoginAlert = false
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
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Image(
                            painter = painterResource(R.drawable.loginalert_vector),
                            contentDescription = "album cover",
                            modifier = Modifier.Companion
                                .fillMaxWidth(0.8f)
                                .padding(bottom = 30.dp)
                        )
                        Text(
                            "Oops! It seems you're not logged in.",
                            modifier = Modifier
                                .padding(bottom = 30.dp),
                            color = lerp(
                                MaterialTheme.colorScheme.onBackground,
                                MaterialTheme.colorScheme.primary,
                                0.5f
                            )
                        )
                    }
                }
            },
            text = {
                Text(
                    text = "Sign in to personalize your experience and enjoy Lofigram without interruptions.\n\nIt only takes a moment!",
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
                            .fillMaxWidth(0.4f)
                            .padding(vertical = 10.dp)
                            .padding(top = 16.dp)
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
                            isLoginAlert = false
                            gotoLoginPage()
                        }) {
                        Text(
                            text = "Login",
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
                            isLoginAlert = false
                        }) {
                        Text(text = "Not now", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        )
    }
}