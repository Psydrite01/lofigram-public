package com.psydrite.lofigram

import android.app.Application
import com.qonversion.android.sdk.Qonversion
import com.qonversion.android.sdk.QonversionConfig
import com.qonversion.android.sdk.dto.QLaunchMode
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LofigramApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val config = QonversionConfig.Builder(
            this,
            "VVI-HHqd6-nnbGx7wPMokvZ8c07TFsSU",
            QLaunchMode.Analytics
        ).build()

        Qonversion.initialize(config)
    }
}