package com.psydrite.lofigram.ui.screens

import android.app.Activity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.psydrite.lofigram.R
import com.psydrite.lofigram.data.remote.viewmodel.DataViewModel
import com.psydrite.lofigram.ui.components.isLoginAlert
import com.psydrite.lofigram.utils.BillingManager
import com.psydrite.lofigram.utils.CurrentUserObj
import com.psydrite.lofigram.utils.isGestureNav

var selectedPlan by mutableStateOf("Premium")
var tryingToBuy by mutableStateOf("")

@Composable
fun SubscriptionPage(
    gotoHomePage: ()-> Unit,
    dataViewModel: DataViewModel = hiltViewModel()
){
    //for billing
    val context = LocalContext.current
    val acitivity = context as Activity
    val billingManager = remember { BillingManager(acitivity) }
    LaunchedEffect(Unit) {
        billingManager.startBillingConnection()

        appliedCoupon = "lofigram_"
    }

    LaunchedEffect(Unit) {
        selectedPlan = "Premium"
    }

    var priceString: String = when(appliedCoupon){
        "lofigram_offer1_"-> {"Rs 50"}
        "lofigram_offer2_"-> {"Rs 10"}
        else -> {"Rs 100"}
    }.toString()

    Box(modifier = Modifier
        .fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopStart)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 200.dp)
        ) {
            Text(
                "Choose your plan",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(20.dp)
                    .padding(top = 55.dp)
            )
            Text(
                "Unlock exclusive features and enhance your Lofigram experience. Choose a plan that suits you best.",
                color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 30.dp)
            )

            OptionCard("Premium", "All features without any ads.", priceString, "For Lifetime")
            OptionCard("Trialpack", "Premium features for a limited time.", "Rs 10", "24 Hours")
            OptionCard("Basic", "Get started at no cost.", "FREE", "Upgrade anytime")

            var temp by remember { mutableStateOf("") }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = temp,
                    onValueChange = { temp = it },
                    label = { Text("Have a coupon code?") },
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    shape = RoundedCornerShape(5.dp),
                    colors = OutlinedTextFieldDefaults.colors( // cannot find the function here(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.5f
                        ),
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.5f
                        )
                    )
                )
                Button(
                    onClick = {
                        if (temp != "") {
                            //try to apply coupon code
                            dataViewModel.ApplyCoupon(context, temp)
                        } else {
                            appliedCoupon = "lofigram_"
                        }
                    },
                    modifier = Modifier
                        .padding(start = 8.dp),
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Apply")
                }
            }


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 9.dp)
                    .padding(top = 20.dp),
                shape = RoundedCornerShape(5),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiary.copy(0.3f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        "You'll get:",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                    )
                    FeatureComposable("Unlimited music and amazing screensavers", "PremiumTrialpackBasic")
                    FeatureComposable("More than 80% of content in Lofigram lab", "PremiumTrialpackBasic")
                    FeatureComposable("Unlimited global chat and Chatbot access, make new friends!", "PremiumTrialpackBasic")
                    FeatureComposable("Download favorite tracks and listen offline", "PremiumTrialpack")
                    FeatureComposable("Sleep timer: Relax and fall asleep peacefully","PremiumTrialpack")
                    FeatureComposable("Golden glow 👑: Shine brighter in global chat!","PremiumTrialpack")
                    FeatureComposable("No more interruptions ever!", "Premium")
                }
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
                    //logic
                    if (CurrentUserObj.isGoogleLoggedIn==true){
                        if (selectedPlan=="Basic"){
                            dataViewModel.saveStringData("userdata","subscriptionplan",selectedPlan, gotoHomePage)
                        }else if (selectedPlan=="Premium"){
                            tryingToBuy = selectedPlan
                            billingManager.launchPurchaseFlow(appliedCoupon+selectedPlan.lowercase())
                        }else{
                            tryingToBuy = selectedPlan
                            billingManager.launchPurchaseFlow("lofigram_"+selectedPlan.lowercase())
                        }
                    }else{
                        isLoginAlert = true
                    }
                }
            ) {
                Text("Continue")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionCard(plan_name: String, plan_details: String, plan_cost: String, plan_duration: String){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 9.dp)
            .border(2.dp, if (selectedPlan == plan_name) Color.Transparent else MaterialTheme.colorScheme.tertiary, RoundedCornerShape(20)),
        shape = RoundedCornerShape(20),
        colors = CardDefaults.cardColors(
            containerColor = if (selectedPlan == plan_name) MaterialTheme.colorScheme.secondary else Color.Transparent
        )
    ) {
        //gives custom ripple effect color
        CompositionLocalProvider(LocalRippleConfiguration provides RippleConfiguration(color = MaterialTheme.colorScheme.primary)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = {
                            // logic
                            selectedPlan = plan_name
                        }
                    ),
            ){
                Row(
                    modifier = Modifier
                        .padding(15.dp)
                        .align(Alignment.CenterStart),
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //icon
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .border(if (selectedPlan == plan_name) 6.dp else 2.dp,
                                if (selectedPlan == plan_name) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                                CircleShape)
                    )


                    Column (
                        modifier = Modifier,
                    ){
                        Text(
                            plan_name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                        )
                        Text(
                            plan_details,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                        )
                    }
                }

                Column (
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(15.dp),
                    horizontalAlignment = Alignment.End
                ){
                    if (plan_cost != "Rs 100" && plan_name=="Premium"){
                        Row {
                            Text(
                                plan_cost+"  ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                            )
                            Text(
                                "Rs 100",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = TextDecoration.LineThrough,
                                color = MaterialTheme.colorScheme.onBackground.copy(0.6f),
                                modifier = Modifier
                            )
                        }
                    }else{
                        Text(
                            plan_cost,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                        )
                    }
                    Text(
                        plan_duration,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.6f),
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureComposable(name: String, plans: String){
    Row (
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ){
        if (selectedPlan in plans){
            Icon(
                painter = painterResource(id = R.drawable.baseline_check_circle_outline_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(20.dp)
            )
        }
        else{
            Icon(
                painter = painterResource(id = R.drawable.baseline_add_circle_outline_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground.copy(0.6f),
                modifier = Modifier
                    .size(20.dp)
                    .rotate(45f)
            )
        }
        Text(
            name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
            modifier = Modifier
                .padding(vertical = 5.dp)
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun test(){
//    SubscriptionPage()
//}