package com.psydrite.lofigram.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object NetworkChecker {

    fun isNetworkAvailable(context: Context): Boolean{
        val connectivityManager= context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        //may say unnecessary but including still for safety
        return if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapablities = connectivityManager.getNetworkCapabilities(network) ?: return false

            networkCapablities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapablities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    networkCapablities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) //adding ethernet for emulator and other niche scenario
        }else{
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected == true
        }
    }
}