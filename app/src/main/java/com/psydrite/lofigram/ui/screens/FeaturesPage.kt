package com.psydrite.lofigram.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.psydrite.lofigram.R
import com.psydrite.lofigram.data.remote.viewmodel.PomodoroState
import com.psydrite.lofigram.data.remote.viewmodel.PomodoroTimerViewModel
import com.psydrite.lofigram.data.remote.viewmodel.SleepTimerViewModel
import com.psydrite.lofigram.ui.components.errorMessage
import com.psydrite.lofigram.utils.CurrentUserObj
import kotlinx.coroutines.delay
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FeaturesPage(
    sleepTimerViewModel: SleepTimerViewModel,
    pomodoroTimerViewModel: PomodoroTimerViewModel,
    goto_upgradePage: ()-> Unit,
    goto_screensaver:()-> Unit,
){

    //variables for sleep timer
    val showSleepTimerModal by sleepTimerViewModel.showSleepTimerModal.collectAsState()
    val sleepTimerSeconds by sleepTimerViewModel.timerSeconds.collectAsState()
    val isSleepTimerRunning by sleepTimerViewModel.isTimerRunning.collectAsState()

    val context= LocalContext.current

    //variables for pomodoro
    val showPomodoroModal by pomodoroTimerViewModel.showPomodoroModal.collectAsState()
    val pomodoroSeconds by pomodoroTimerViewModel.timerSeconds.collectAsState()
    val isPomodoroRunning by pomodoroTimerViewModel.isTimerRunning.collectAsState()
    val pomodoroState by pomodoroTimerViewModel.currentState.collectAsState()
    val pomodoroProgress by remember(pomodoroSeconds, pomodoroTimerViewModel.totalSeconds.collectAsState().value) { mutableStateOf(pomodoroTimerViewModel.getProgress()) }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        lerp(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.primary,
                            0.2f
                        ),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .zIndex(1f)
                .padding(top = 55.dp)
        ) {
            //header Text
            Text(
                text = buildAnnotatedString {
                    append("Lofigram Lab ")
                    withStyle(SpanStyle(baselineShift = BaselineShift(0.21f))) {
                        append("🔬")
                    }
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 20.dp))
        }

        @OptIn(ExperimentalLayoutApi::class)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopStart)
                .padding(top = 155.dp, bottom = 120.dp)

        ) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth().verticalScroll(
                        rememberScrollState()
                    ),
                maxItemsInEachRow = 2,
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                //sleep timer composable with sleep timer
                SleepTimerComposable(
                    sleepTimerSeconds = sleepTimerSeconds,
                    isTimerRunning = isSleepTimerRunning,
                    onTimerClick = {sleepTimerViewModel.showModal()},
                    gotoUpgradePage = goto_upgradePage
                )
                PomodoroTimerComposable(
                    pomodoroSeconds= pomodoroSeconds,
                    isTimerRunning = isPomodoroRunning,
                    currentState = pomodoroState,
                    progress = pomodoroProgress,
                    onTimerClick = { pomodoroTimerViewModel.showModal() }
                )
                FeatureComposable("Screensavers", R.drawable.feature_screensaver, {goto_screensaver()})
                DaypercentComposable()
                FeatureComposable("richup.io", R.drawable.feature_richup){ context.openUrlInBrowser(url = "https://richup.io") }
                FeatureComposable("scribbl.io", R.drawable.feature_scribbl){ context.openUrlInBrowser(url = "https://skribbl.io")}
                FeatureComposable("neal.fun", R.drawable.feature_neal){ context.openUrlInBrowser(url = "https://neal.fun")}
                FeatureComposable("Chess.com", R.drawable.feature_chess){ context.openUrlInBrowser(url = "https://www.chess.com")}
            }

            Spacer(Modifier.height(50.dp))
        }

        //sleep timer modal menu
        if(showSleepTimerModal){
            SleepTimerModal(
                onDismis = {
                    if(isSleepTimerRunning){
                        sleepTimerViewModel.stopTimer()
                        sleepTimerViewModel.hideModal()
                    }else{
                        sleepTimerViewModel.hideModal()
                    }

                           },
                onTimeSelected = {
                    min->
                    sleepTimerViewModel.startTimer(min)
                }
            )
        }
        
        //show pomodoro timer modal
        if(showPomodoroModal){
            PomodoroTimerModal(
                pomodoroViewModel = pomodoroTimerViewModel,
                onDismiss = { pomodoroTimerViewModel.hideModal() }
            )
        }
    }
}

@Composable
fun FeatureComposable(name: String, icon: Int, onClick:()-> Unit = {}){
    Card(
        modifier = Modifier
            .fillMaxWidth(0.4f),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(MaterialTheme.colorScheme.secondary)
                .clip(RoundedCornerShape(10.dp))
                .clickable(
                    onClick = onClick
                )
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxHeight()
                    .padding(vertical = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = name,
                    modifier = Modifier.Companion
                        .fillMaxSize(0.7f)
                )
                Text(name)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DaypercentComposable(){

    var percentday by remember { mutableStateOf(getDayProgressPercent())}
    var percentweek by remember { mutableStateOf(getWeekProgressPercent())}
    var percentyear by remember { mutableStateOf(getYearProgressPercent())}

    LaunchedEffect(Unit) {
        while (true) {
            percentday = getDayProgressPercent()
            percentweek = getWeekProgressPercent()
            percentyear = getYearProgressPercent()
            delay(30_000L) //update every minute
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(0.4f),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(MaterialTheme.colorScheme.secondary)
                .clip(RoundedCornerShape(10.dp))
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxHeight()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start
            ) {
                Column {
                    Text("${(percentday * 100).toInt()}% DAY", style = MaterialTheme.typography.titleSmall)
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .background(Color.White)
                        ) {  }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(percentday * 1f)
                                .height(5.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {  }
                    }
                }

                Column {
                    Text("${(percentweek * 100).toInt()}% WEEK", style = MaterialTheme.typography.titleSmall)
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .background(Color.White)
                        ) {  }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(percentweek * 1f)
                                .height(5.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {  }
                    }
                }

                Column {
                    Text("${(percentyear * 100).toInt()}% YEAR", style = MaterialTheme.typography.titleSmall)
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .background(Color.White)
                        ) {  }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(percentyear * 1f)
                                .height(5.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {  }
                    }
                }
            }
        }
    }
}

@Composable
fun SleepTimerComposable(
    sleepTimerSeconds: Int,
    isTimerRunning: Boolean,
    onTimerClick: () -> Unit,
    gotoUpgradePage: () -> Unit
) {
    //card for sleep timer with dynamic text in it
    Card(
        modifier = Modifier.fillMaxWidth(0.4f).clickable{
            if (CurrentUserObj.isGoogleLoggedIn==true && (CurrentUserObj.subscriptionType=="Premium" || CurrentUserObj.subscriptionType=="Trialpack")){
                onTimerClick()
            }else{
                gotoUpgradePage()
            }
             },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().aspectRatio(1f).background(MaterialTheme.colorScheme.secondary).clip(RoundedCornerShape(10.dp))
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center).fillMaxHeight().padding(vertical = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.feature_clock), contentDescription = "Sleep Timer", modifier = Modifier.fillMaxSize(0.7f)
                )

                //logic for dynamic text, displaying the remaining time here when active, when not active showing sleep timer
                if(isTimerRunning && sleepTimerSeconds>0){
                    val min= sleepTimerSeconds/60
                    val sec = sleepTimerSeconds%60
                    Text(
                        text = String.format("Time left: %d:%02d", min, sec),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }else{
                    Text("Sleep Timer")
                }
            }
        }
    }
}

@Composable
fun PomodoroTimerComposable(
    pomodoroSeconds: Int,
    isTimerRunning: Boolean,
    currentState: PomodoroState,
    progress: Float,
    onTimerClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.4f)
            .clickable { onTimerClick() },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(MaterialTheme.colorScheme.secondary)
                .clip(RoundedCornerShape(10.dp))
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxHeight()
                    .padding(vertical = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //if running circular progress indicator or initial image
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isTimerRunning || currentState != PomodoroState.STOPPED) {

                        CircularProgressIndicator(
                            progress = 1f,
                            modifier = Modifier.fillMaxSize().zIndex(-1f),
                            color = Color.White.copy(0.8f),
                            strokeWidth = 6.dp
                        )

                        CircularProgressIndicator(
                            progress = progress,
                            modifier = Modifier.fillMaxSize().zIndex(3f),
                            strokeWidth = 6.dp,
                            color = when (currentState) {
                                PomodoroState.WORK -> MaterialTheme.colorScheme.primary
                                PomodoroState.SHORT_BREAK -> Color(0xFF4CAF50)
                                PomodoroState.LONG_BREAK -> Color(0xFF2196F3)
                                PomodoroState.STOPPED -> MaterialTheme.colorScheme.primary
                            },
                            strokeCap = StrokeCap.Round
                        )
                        //dynamic text for timer
                        if (pomodoroSeconds > 0) {
                            val minutes = pomodoroSeconds / 60
                            val seconds = pomodoroSeconds % 60
                            Text(
                                text = String.format("%d:%02d", minutes, seconds),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Image(
                            painter = painterResource(R.drawable.feature_pomodoro),
                            contentDescription = "Pomodoro Timer",
                            modifier = Modifier.fillMaxSize(0.8f)
                        )
                    }
                }

                //current status text
                Text(
                    text = when {
                        isTimerRunning && currentState == PomodoroState.WORK -> "Focus Time"
                        isTimerRunning && currentState == PomodoroState.SHORT_BREAK -> "Short Break"
                        isTimerRunning && currentState == PomodoroState.LONG_BREAK -> "Long Break"
                        currentState != PomodoroState.STOPPED && !isTimerRunning -> "Paused"
                        else -> "Pomodoro"
                    },
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepTimerModal(
    onDismis: () -> Unit,
    onTimeSelected: (Int)-> Unit
){
    var sheetState = rememberModalBottomSheetState()

    //default choosable times
    val defaultTimes = listOf(
        Pair(1, "1 Minute"), // for testing, can remove later
        Pair(5, "5 Minutes"),
        Pair(10, "10 minutes"),
        Pair(15, "15 minutes"),
        Pair(30, "30 minutes"),
        Pair(45, "45 minutes"),
        Pair(60, "1 hour"),
        Pair(90, "1.5 hours"),
        Pair(120, "2 hours")
    )

    ModalBottomSheet(
        onDismissRequest = onDismis
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp).padding(bottom = 32.dp)
        ) {
            Text(
                text = "Set Sleep Timer",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            defaultTimes.chunked(2).forEach {
                times->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    times.forEach {
                        (minutes, label) ->
                        OutlinedButton(
                            onClick = { onTimeSelected(minutes) },
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Text(
                                text = label,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    //if odd number of items in default times, leaving white space
                    if(times.size==1){
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            //cancel button
            Button(
                onClick = onDismis,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Cancel Sleep Timer", color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroTimerModal(
    pomodoroViewModel: PomodoroTimerViewModel,
    onDismiss: () -> Unit
) {
    val isTimerRunning by pomodoroViewModel.isTimerRunning.collectAsState()
    val currentState by pomodoroViewModel.currentState.collectAsState()
    val completedSessions by pomodoroViewModel.completedSessions.collectAsState()
    val timerSeconds by pomodoroViewModel.timerSeconds.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Pomodoro Timer",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            //number of sessions completed
            Text(
                text = "Completed Sessions: $completedSessions",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            //current timer status
            if (currentState != PomodoroState.STOPPED) {
                val minutes = timerSeconds / 60
                val seconds = timerSeconds % 60
                val stateText = when (currentState) {
                    PomodoroState.WORK -> "Work Session"
                    PomodoroState.SHORT_BREAK -> "Short Break"
                    PomodoroState.LONG_BREAK -> "Long Break"
                    PomodoroState.STOPPED -> ""
                }

                Text(
                    text = "$stateText: ${String.format("%d:%02d", minutes, seconds)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                //choices for timer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (isTimerRunning) {
                                pomodoroViewModel.pauseTimer()
                            } else {
                                pomodoroViewModel.resumeTimer()
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text(if (isTimerRunning) "Pause Session" else "Resume Session")
                    }

                    OutlinedButton(
                        onClick = { pomodoroViewModel.stopTimer() },
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Stop Pomodoro")
                    }
                }
            } else {
                //pomodoro options
                val pomodoroOptions = listOf(
                    Triple(25, "Work Session", "25 min"),
                    Triple(5, "Short Break", "5 min"),
                    Triple(15, "Long Break", "15 min")
                )

                pomodoroOptions.chunked(2).forEach { options ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        options.forEach { (minutes, type, label) ->
                            OutlinedButton(
                                onClick = {
                                    when (type) {
                                        "Work Session" -> pomodoroViewModel.startWorkSession()
                                        "Short Break" -> pomodoroViewModel.startShortBreak()
                                        "Long Break" -> pomodoroViewModel.startLongBreak()
                                    }
                                },
                                modifier = Modifier.weight(1f).height(70.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = type,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        //leaving extra space if odd options
                        if (options.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            //reset buttons
            OutlinedButton(
                onClick = { pomodoroViewModel.resetSessions() },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Reset Session Counter")
            }

            Spacer(modifier = Modifier.height(8.dp))

            //close button
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Close", color = Color.White)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getDayProgressPercent(): Float {
    val now = LocalTime.now()
    val secondsPassed = now.toSecondOfDay()
    val totalSecondsInDay = 24 * 60 * 60
    val rawPercent = secondsPassed.toFloat() / totalSecondsInDay

    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.HALF_UP

    return df.format(rawPercent).toFloat()
}

@RequiresApi(Build.VERSION_CODES.O)
fun getWeekProgressPercent(): Float {
    val now = LocalDateTime.now()
    val dayOfWeek = now.dayOfWeek.value  // Monday = 1, Sunday = 7
    val minutesToday = now.toLocalTime().toSecondOfDay() / 60
    val totalMinutesPassed = (dayOfWeek - 1) * 24 * 60 + minutesToday

    val totalMinutesInWeek = 7 * 24 * 60
    val rawPercent = totalMinutesPassed.toFloat() / totalMinutesInWeek

    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.HALF_UP

    return df.format(rawPercent).toFloat()
}

@RequiresApi(Build.VERSION_CODES.O)
fun getYearProgressPercent(): Float {
    val now = LocalDate.now()
    val startOfYear = LocalDate.of(now.year, 1, 1)
    val endOfYear = LocalDate.of(now.year, 12, 31)

    val daysPassed = ChronoUnit.DAYS.between(startOfYear, now).toFloat()
    val totalDays = ChronoUnit.DAYS.between(startOfYear, endOfYear).toFloat() + 1 // +1 to include last day

    val rawPercent = daysPassed / totalDays

    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.HALF_UP

    return df.format(rawPercent).toFloat()
}

//function to pass intent to open external websites
private fun Context.openUrlInBrowser(url: String){
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }catch (e: Exception){
        errorMessage= e.message.toString()
    }
}
