package com.psydrite.lofigram.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.psydrite.lofigram.R
import com.psydrite.lofigram.data.remote.viewmodel.MessageViewmodel
import com.psydrite.lofigram.ui.components.GlobalChatAlert
import com.psydrite.lofigram.ui.components.ShimmerBrush
import com.psydrite.lofigram.ui.components.showNetworkErrorAlert
import com.psydrite.lofigram.utils.CurrentUserObj
import com.psydrite.lofigram.utils.GlobalMessage
import com.psydrite.lofigram.utils.NetworkChecker
import com.psydrite.lofigram.utils.PromptHandler
import com.psydrite.lofigram.utils.isGestureNav
import com.psydrite.lofigram.utils.messageList
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue

var tempWrittenMessage by mutableStateOf(TextFieldValue(""))
var areMessagesLoading by mutableStateOf(false)

var canSend by mutableStateOf(true)
val cooldownMillis = 3000L
var cooldownstarter by mutableStateOf(false)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GlobalChat(
    messageViewmodel: MessageViewmodel = hiltViewModel()
){
    GlobalChatAlert()
    PromptHandler()

    val maxChar = 200
    var lazyColumnState = rememberLazyListState()
    val context = LocalContext.current.applicationContext
    if(NetworkChecker.isNetworkAvailable(context)==false){
        showNetworkErrorAlert= true
    }


    LaunchedEffect(cooldownstarter) {
        delay(cooldownMillis)
        canSend = true
    }
    LaunchedEffect(messageList) {
        lazyColumnState.animateScrollToItem(messageList.size)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopStart)
                .zIndex(2f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            lerp(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.primary,
                                0.2f
                            ),
                            MaterialTheme.colorScheme.background.copy(0.6f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            )
                    )
                )
        ) {
            //gradient
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .zIndex(3f)
                .padding(top = 55.dp)
        ) {
            //header Text
            Text(
                text = buildAnnotatedString {
                    append("Global chat ")
                    withStyle(
                        style = SpanStyle(
                        color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    )) {
                        append("Say Hi!!")
                    }
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 20.dp))
        }


        Column (
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(20.dp)
                .zIndex(1f)
                .padding(bottom = if (isGestureNav) 105.dp else 145.dp)
        ){
            //messages
            LazyColumn (
                state = lazyColumnState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Occupy remaining space
            ){
                items(messageList) {
                    ChatBubble(it)
                }
            }

//            var expanded by remember { mutableStateOf(false) }
//
//            DropdownMenu(
//                expanded = tempWrittenMessage.isNotBlank(),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .border(1.dp, Color.Red),
//                onDismissRequest = {}
//            ) {
//                DropdownMenuItem(
//                    text = {
//                        Text("Option")
//                    },
//                    onClick = {
//
//                    }
//                )
//            }
//
//
            var isExpanded = tempWrittenMessage.text.lastOrNull()=='@'
            AnimatedContent(
                targetState = isExpanded,
                transitionSpec = {
                    slideInVertically(animationSpec = tween(100)) { height -> -height } with
                            slideOutVertically(animationSpec = tween(100)) {height -> height }
                }
            ) { bool->
                if (bool){
                    Card (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                tempWrittenMessage = TextFieldValue(
                                    text = tempWrittenMessage.text + "yumiko ",
                                    selection = TextRange((tempWrittenMessage.text + "yumiko ").length) // Move cursor to end
                                )
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ){
                        Text(
                            text = "Yumiko AI 🤖",
                            color = Color(0xffFF69B4),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 10.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //textfield
                OutlinedTextField(
                    value = tempWrittenMessage,
                    onValueChange = { if (it.text.length <= maxChar) tempWrittenMessage = it },
                    label = { Text("Hold to send anonymously!") },
                    modifier = Modifier.weight(1f), // TextField takes available width
                    shape = RoundedCornerShape(20.dp)
                )

                //send button
                AnimatedContent(
                    targetState = canSend,
                    transitionSpec = {
                        slideInHorizontally(animationSpec = tween(400)) { width -> -width } with
                                slideOutHorizontally(animationSpec = tween(400)) {width -> width }
                    }
                ) { cansend ->
                    if (cansend){
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .height(56.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = {
                                            if (tempWrittenMessage.text.isNotBlank()) {
                                                canSend = false
                                                messageViewmodel.SendMessage(
                                                    GlobalMessage(
                                                        messageId = "",
                                                        message = tempWrittenMessage.text,
                                                        username = CurrentUserObj.username.toString(),
                                                        time = System.currentTimeMillis(),
                                                        isPremium = ((CurrentUserObj.isGoogleLoggedIn == true)
                                                                && (CurrentUserObj.subscriptionType == "Premium"
                                                                || CurrentUserObj.subscriptionType == "Trialpack")),
                                                        isAnnonymous = true
                                                    )
                                                )
                                            }
                                        },
                                        onTap = {
                                            if (tempWrittenMessage.text.isNotBlank()) {
                                                canSend = false
                                                messageViewmodel.SendMessage(
                                                    GlobalMessage(
                                                        messageId = "",
                                                        message = tempWrittenMessage.text,
                                                        username = CurrentUserObj.username.toString(),
                                                        time = System.currentTimeMillis(),
                                                        isPremium = ((CurrentUserObj.isGoogleLoggedIn == true)
                                                                && (CurrentUserObj.subscriptionType == "Premium"
                                                                || CurrentUserObj.subscriptionType == "Trialpack")),
                                                        isAnnonymous = false
                                                    )
                                                )
                                            }
                                        }
                                    )
                                }
                                .padding(horizontal = 16.dp), // Optional: padding inside the box
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Send,
                                contentDescription = "Send Message",
                                tint = MaterialTheme.colorScheme.background
                            )
                        }
                    }else{
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .height(56.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.onBackground)
                                .padding(horizontal = 16.dp), // Optional: padding inside the box
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_hourglass_top_24),
                                contentDescription = "cooldown",
                                tint = MaterialTheme.colorScheme.background,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: GlobalMessage){
    val lerpcolor = lerp(
        MaterialTheme.colorScheme.background,
        MaterialTheme.colorScheme.primary,
        0.3f
    )
    if (message.isAnnonymous){
        message.isPremium = false
    }

    val isSender = (message.username == CurrentUserObj.username.toString()) && !message.isAnnonymous
    val bubbleColor = if (isSender) lerpcolor  else MaterialTheme.colorScheme.secondary
    val textColor = MaterialTheme.colorScheme.onBackground
    val colorfromstring = colorFromString(message.username)
    val usernamecolor = if (message.isAnnonymous) MaterialTheme.colorScheme.onBackground.copy(0.7f)
    else if (message.username=="Yumiko AI") Color(0xffFF69B4)
        else lerp(
        colorfromstring,
        MaterialTheme.colorScheme.onBackground,
        0.2f
    )
    val alignment = if (isSender) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleShape = if (isSender) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(IntrinsicSize.Min) // Ensure the Card takes minimum required height
    ) {
        Box(modifier = Modifier.fillMaxWidth()){
            Card(
                modifier = Modifier
                    .align(alignment)
                    .defaultMinSize(minWidth = 100.dp) // Ensure a minimum width for the card
                    .padding(horizontal = 8.dp)
                    .border(1.dp, brush =
                        ShimmerBrush(
                            if (message.username=="Yumiko AI") Color(0xffFF69B4).copy(0.3f) else MaterialTheme.colorScheme.background,
                            if (message.isPremium) MaterialTheme.colorScheme.onSecondary
                            else if (message.username=="Yumiko AI") Color(0xffFF69B4)
                            else MaterialTheme.colorScheme.background),
                    bubbleShape),
                shape = bubbleShape,
                colors = CardDefaults.cardColors(
                    //here
                    containerColor = bubbleColor
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = if (isSender) Alignment.End else Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    if (message.username=="Yumiko AI"){
                        Text(
                            text = message.username + " 🤖",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = usernamecolor,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    else if (!isSender) {
                        if (message.isPremium){
                            Text(
                                text = message.username + " 👑",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    brush =
                                        ShimmerBrush(
                                            usernamecolor,
                                            MaterialTheme.colorScheme.onSecondary),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }else{
                            Text(
                                text = if (message.isAnnonymous) "Anonymous" else message.username,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = usernamecolor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    Text(
                        text = message.message,
                        color = textColor,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault()).format(Date(message.time)),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.End,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

fun colorFromString(input: String): Color {
    val hash = input.hashCode().absoluteValue

    val hue = (hash % 360).toFloat()            // Hue: 0–359
    val saturation = 0.6f                        // Fixed moderate saturation
    val lightness = 0.6f                         // Medium lightness for visibility

    return Color.hsl(hue, saturation, lightness)
}