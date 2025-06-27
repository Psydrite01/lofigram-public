package com.psydrite.lofigram.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.psydrite.lofigram.R
import com.psydrite.lofigram.utils.ScreenSaverPlayer
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale


var currentScreenSaver: String by mutableStateOf("")

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScreenSaverPlayer(){

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp

    val scaledFontSizeSp = (screenWidthDp).sp  //consistent size across devices

    Box(
        modifier = Modifier.Companion
            .fillMaxSize()
    ) {
        Column(
            Modifier.Companion.fillMaxSize()
        ) {
            ScreenSaverPlayer(currentScreenSaver)
        }

        var formattedTime by remember { mutableStateOf("") }
        var formattedDate by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            while (true) {
                val currentTime = LocalTime.now()
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                formattedTime = currentTime.format(timeFormatter)

                val currentDate = LocalDate.now()
                val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
                formattedDate = currentDate.format(dateFormatter)

                delay(10_000L) // 10 seconds
            }
        }

        when(currentScreenSaver) {
            "ss_1.mp4" -> {
                Column(
                    modifier = Modifier.Companion
                        .fillMaxSize()
                        .align(Alignment.Companion.TopStart),
                    horizontalAlignment = Alignment.Companion.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(Modifier.fillMaxHeight(0.17f))
                    Row(
                        modifier = Modifier.Companion,
                        verticalAlignment = Alignment.Companion.CenterVertically
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append(formattedTime)
                                append("\n")
                                withStyle(style = SpanStyle(fontSize = scaledFontSizeSp*0.05)) {
                                    append(formattedDate.toUpperCase())
                                }
                            },
                            fontSize = scaledFontSizeSp*0.35,
                            fontFamily = FontFamily(Font(R.font.unsteady)),
                            color = Color.White,
                            modifier = Modifier.Companion
                                .rotate(-90f)
                                .graphicsLayer(
                                    scaleY = 1.8f
                                ),
                            textAlign = TextAlign.Companion.End
                        )
                    }
                }
            }

            "ss_2.mp4" -> {
                Column(
                    modifier = Modifier.Companion
                        .fillMaxSize()
                        .align(Alignment.Companion.TopStart),
                    horizontalAlignment = Alignment.Companion.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        modifier = Modifier.Companion,
                        verticalAlignment = Alignment.Companion.CenterVertically
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append(formattedTime)
                                append("\n")
                                withStyle(style = SpanStyle(fontSize = scaledFontSizeSp*0.05)) {
                                    append(formattedDate.toUpperCase())
                                }
                            },
                            color = Color.Black,
                            fontSize = scaledFontSizeSp*0.35,
                            fontFamily = FontFamily(Font(R.font.unsteady)),
                            modifier = Modifier.Companion
                                .rotate(-90f)
                                .graphicsLayer(
                                    scaleY = 1.8f
                                ),
                            textAlign = TextAlign.Companion.Start
                        )
                    }
                    Spacer(Modifier.fillMaxHeight(0.2f))
                }
            }

            "ss_3.mp4" -> {
                Column(
                    modifier = Modifier.Companion
                        .fillMaxSize()
                        .align(Alignment.Companion.TopStart),
                    horizontalAlignment = Alignment.Companion.Start,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        modifier = Modifier.Companion,
                        verticalAlignment = Alignment.Companion.CenterVertically
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append(formattedTime)
                                append("\n")
                                withStyle(style = SpanStyle(fontSize = scaledFontSizeSp*0.05)) {
                                    append(formattedDate.toUpperCase())
                                }
                            },
                            fontSize = scaledFontSizeSp*0.35,
                            fontFamily = FontFamily(Font(R.font.unsteady)),
                            color = Color.White.copy(0.7f),
                            modifier = Modifier.Companion
                                .rotate(-90f)
                                .graphicsLayer(
                                    scaleY = 1.8f
                                ),
                            textAlign = TextAlign.Companion.Start
                        )
                    }
                    Spacer(Modifier.fillMaxHeight(0.2f))
                }
            }
            "ss_4.mp4" -> {
                Column(
                    modifier = Modifier.Companion
                        .fillMaxSize()
                        .align(Alignment.Companion.TopStart),
                    horizontalAlignment = Alignment.Companion.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(Modifier.fillMaxHeight(0.2f))
                    Row(
                        modifier = Modifier.Companion,
                        verticalAlignment = Alignment.Companion.CenterVertically
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append(formattedTime)
                                append("\n")
                                withStyle(style = SpanStyle(fontSize = scaledFontSizeSp*0.04)) {
                                    append(formattedDate.toUpperCase())
                                }
                            },
                            fontSize = scaledFontSizeSp*0.25,
                            fontFamily = FontFamily(Font(R.font.unsteady)),
                            color = Color.Black,
                            modifier = Modifier.Companion
                                .rotate(-90f)
                                .graphicsLayer(
                                    scaleY = 1.8f
                                ),
                            textAlign = TextAlign.Companion.Start
                        )
                    }
                }
            }
            "ss_5.mp4" -> {
                Column(
                    modifier = Modifier.Companion
                        .fillMaxSize()
                        .align(Alignment.Companion.TopStart),
                    horizontalAlignment = Alignment.Companion.Start,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        modifier = Modifier.Companion,
                        verticalAlignment = Alignment.Companion.CenterVertically
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append(formattedTime)
                                append("\n")
                                withStyle(style = SpanStyle(fontSize = scaledFontSizeSp*0.04)) {
                                    append(formattedDate.toUpperCase())
                                }
                            },
                            fontSize = scaledFontSizeSp*0.27,
                            fontFamily = FontFamily(Font(R.font.unsteady)),
                            color = Color.Black,
                            modifier = Modifier.Companion
                                .rotate(-90f)
                                .graphicsLayer(
                                    scaleY = 1.8f
                                ),
                            textAlign = TextAlign.Companion.End
                        )
                    }
                    Spacer(Modifier.fillMaxHeight(0.17f))
                }
            }
            "ss_6.mp4" -> {
                Column(
                    modifier = Modifier.Companion
                        .fillMaxSize()
                        .align(Alignment.Companion.TopStart),
                    horizontalAlignment = Alignment.Companion.Start,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        modifier = Modifier.Companion,
                        verticalAlignment = Alignment.Companion.CenterVertically
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append(formattedTime)
                                append("\n")
                                withStyle(style = SpanStyle(fontSize = scaledFontSizeSp*0.04)) {
                                    append(formattedDate.toUpperCase())
                                }
                            },
                            fontSize = scaledFontSizeSp*0.25,
                            fontFamily = FontFamily(Font(R.font.unsteady)),
                            color = Color.DarkGray,
                            modifier = Modifier.Companion
                                .rotate(-90f)
                                .graphicsLayer(
                                    scaleY = 1.8f
                                ),
                            textAlign = TextAlign.Companion.Start
                        )
                    }
                    Spacer(Modifier.fillMaxHeight(0.14f))
                }
            }
        }
    }
}