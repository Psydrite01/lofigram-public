package com.psydrite.lofigram.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.psydrite.lofigram.strings_public
import com.psydrite.lofigram.ui.navigation.NavScreensObject
import com.psydrite.lofigram.ui.navigation.currentPage


var AdShowingScreens = listOf(
    NavScreensObject.HOME_PAGE,
    NavScreensObject.FEATURES_PAGE,
    NavScreensObject.GLOBAL_CHAT_PAGE,
    NavScreensObject.USER_PROFILE_PAGE,
    NavScreensObject.UPGRADATION_PAGE,
    NavScreensObject.SCREENSAVER_PAGE
)

@Composable
fun AdShower(){
    //for ads here, still in testing tho
    if (CurrentUserObj.isGoogleLoggedIn == false || CurrentUserObj.subscriptionType=="Basic"){
        val context = LocalContext.current
        val activity = context as? Activity
        LaunchedEffect(currentPage, StateObject.idname) {
            if (currentPage in AdShowingScreens){
                Log.d("Adsgoogle", "LaunchedEffect called, mInterstitialAd = $mInterstitialAd")
                LoadFullScreenAd(context)

                //include randomness here
                val random = kotlin.random.Random.nextInt(1,101)
                if (activity != null && random<=15){
                    showInterstitialAd(activity)
                }
            }
        }
    }
}

var mInterstitialAd: InterstitialAd? by mutableStateOf(null)
var isLoadingAd by mutableStateOf(false)

fun LoadFullScreenAd(context: Context) {
    if (!isLoadingAd && mInterstitialAd == null) {
        isLoadingAd = true
        Log.d("Adsgoogle", "Loading interstitial ad")

        val adRequest = AdRequest.Builder().build()

        try {
            InterstitialAd.load(
                context,
                strings_public.INTERSTITIAL_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        mInterstitialAd = ad
                        isLoadingAd = false
                        Log.d("Adsgoogle", "Ad loaded successfully, mInterstitialAd = $mInterstitialAd")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        mInterstitialAd = null
                        isLoadingAd = false
//                        errorMessage = error.message.toString()
                        Log.e("Adsgoogle", "Ad failed to load: ${error.message.toString()}")
                    }
                }
            )
        } catch (e: Exception) {
            isLoadingAd = false
//            errorMessage = e.message.toString()
            Log.e("Adsgoogle", "Exception: ${e.message}")
        }
    }
}


fun showInterstitialAd(activity: Activity) {
    if (mInterstitialAd != null) {
        Log.d("Adsgoogle", "trying to show ad")
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
//                errorMessage = adError.message.toString()
                mInterstitialAd = null
            }

            override fun onAdShowedFullScreenContent() {
                mInterstitialAd = null
            }
        }
        mInterstitialAd?.show(activity)
    } else {
        Log.d("Adsgoogle", "Ad is null when trying to show")
    }
    LoadFullScreenAd(activity)  //load next ad
}