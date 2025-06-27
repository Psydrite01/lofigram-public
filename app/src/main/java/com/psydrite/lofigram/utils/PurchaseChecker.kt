package com.psydrite.lofigram.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.psydrite.lofigram.data.remote.viewmodel.DataViewModel

var CheckForPurchase by mutableStateOf(false)
var purchaseType by mutableStateOf("")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PurchaseChecker(
    gotocongratspage: ()-> Unit,
    dataViewModel: DataViewModel = hiltViewModel()
){
    LaunchedEffect(CheckForPurchase) {
        Log.d("purchases", "inside launchedeffect, list = "+PurchasesList.toString())
        PurchasesList.forEach { it->
            if (it.orderid == "lofigram_premium" || it.orderid == "lofigram_offer1_premium" || it.orderid == "lofigram_offer2_premium"){
                purchaseType = "Premium"
                dataViewModel.saveStringData("userdata","subscriptionplan","Premium")
                dataViewModel.saveStringData("userdata","orderid",it.orderid, gotocongratspage)
            }else if (it.orderid == "lofigram_trialpack"){
                purchaseType = "Trialpack"
                dataViewModel.saveTimeOut("userdata","TrialPackTimeout")
                dataViewModel.saveStringData("userdata","subscriptionplan","Trialpack")
                dataViewModel.saveStringData("userdata","orderid",it.orderid, gotocongratspage)
            }else{
                //failsafe
                purchaseType = "Trialpack"
                dataViewModel.saveTimeOut("userdata","TrialPackTimeout")
                dataViewModel.saveStringData("userdata","subscriptionplan","Trialpack")
                dataViewModel.saveStringData("userdata","failsafe","unknown product id")
                dataViewModel.saveStringData("userdata","orderid",it.orderid, gotocongratspage)
            }
        }
    }
}