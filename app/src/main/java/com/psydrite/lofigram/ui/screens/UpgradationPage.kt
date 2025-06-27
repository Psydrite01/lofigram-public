package com.psydrite.lofigram.ui.screens

import android.app.Activity
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.psydrite.lofigram.data.remote.viewmodel.DataViewModel
import com.psydrite.lofigram.ui.components.isLoginAlert
import com.psydrite.lofigram.utils.BillingManager
import com.psydrite.lofigram.utils.CurrentUserObj
import com.psydrite.lofigram.utils.isGestureNav


var appliedCoupon by mutableStateOf("lofigram_")

@Composable
fun UpgradationPage(
    supscriptionType: String,
    goto_backPage: () -> Unit,
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

    LaunchedEffect(supscriptionType) {
        selectedPlan = "Premium"
    }

    Box(modifier = Modifier
        .fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopStart)
                .padding(top = 55.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 200.dp)
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    "Upgrade plan",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                )

                //close button
                IconButton(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(65.dp),
                    onClick = goto_backPage
                ) {
                    Icon(
                        imageVector = Icons.Sharp.Close,
                        contentDescription = "close page",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                    )
                }
            }
            Text(
                "Unlock exclusive features by upgrading your plan.",
                color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 30.dp)
            )

            var priceString: String = when(appliedCoupon){
                "lofigram_offer1_"-> {"Rs 50"}
                "lofigram_offer2_"-> {"Rs 10"}
                else -> {"Rs 100"}
            }.toString()

            when(CurrentUserObj.subscriptionType){
                "Basic" ->{
                    //premium and trial option
                    OptionCard("Premium", "All features without any ads.", priceString, "For Lifetime")
                    OptionCard("Trialpack", "Premium features for a limited time.", "Rs 10", "24 Hours")
                }
                "Trialpack" ->{
                    //showing only premium option
                    OptionCard("Premium", "All features without any ads.", priceString, "For Lifetime")

                }
                else -> {
                    //default case, so showing all types
                    OptionCard("Premium", "All features without any ads.", priceString, "For Lifetime")
                    OptionCard("Trialpack", "Premium features for a limited time.", "Rs 10", "24 Hours")
                }
            }

            Card (modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 9.dp)
                .padding(top = 20.dp),
                shape = RoundedCornerShape(5),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiary.copy(0.3f)
                )
            ){
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

            //coupon code row
            }
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
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                )
                Button(
                    onClick = {
                        if (temp != ""){
                            //try to apply coupon code
                            dataViewModel.ApplyCoupon(context, temp)
                        }else{
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
                        tryingToBuy = selectedPlan
                        if (selectedPlan == "Premium"){
                            billingManager.launchPurchaseFlow(appliedCoupon+selectedPlan.lowercase())
                        }else{
                            billingManager.launchPurchaseFlow("lofigram_"+selectedPlan.lowercase())
                        }
                    }else{
                        isLoginAlert = true
                    }
                }
            ) {
                Text("Upgrade")
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun test2(){
//    UpgradationPage()
//}