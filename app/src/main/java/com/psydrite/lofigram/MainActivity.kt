package com.psydrite.lofigram

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.psydrite.lofigram.data.remote.repository.UserPreferencesRespository
import com.psydrite.lofigram.data.remote.viewmodel.PomodoroTimerViewModel
import com.psydrite.lofigram.data.remote.viewmodel.googleAuthClient
import com.psydrite.lofigram.ui.components.BottomBar
import com.psydrite.lofigram.ui.components.NetworkErrorAlert
import com.psydrite.lofigram.ui.components.TrialpackExpireAlert
import com.psydrite.lofigram.ui.components.UpdateAlert
import com.psydrite.lofigram.ui.components.songReqAlert
import com.psydrite.lofigram.ui.components.errorAlert
import com.psydrite.lofigram.ui.components.errorMessage
import com.psydrite.lofigram.ui.components.loginAlert
import com.psydrite.lofigram.ui.components.reportIssueAlert
import com.psydrite.lofigram.ui.navigation.AppNav
import com.psydrite.lofigram.ui.navigation.NavScreensObject
import com.psydrite.lofigram.ui.navigation.targetPage
import com.psydrite.lofigram.ui.screens.isLoadingMap
import com.psydrite.lofigram.ui.theme.LofigramTheme
import com.psydrite.lofigram.utils.AdShower
import com.psydrite.lofigram.utils.MediaNotificationService
import com.psydrite.lofigram.utils.MediaPlayerManager
import com.psydrite.lofigram.utils.NotificationHelper
import com.psydrite.lofigram.utils.PurchaseChecker
import com.psydrite.lofigram.utils.UpdateCurrentUser
import com.psydrite.lofigram.utils.isGestureNav
import com.psydrite.lofigram.utils.isGestureNavigationEnabled
import com.qonversion.android.sdk.Qonversion
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    //for google auth
    private val googleAuthUiClient by lazy {
        googleAuthClient(
            context = this,
            oneTapClient = Identity.getSignInClient(this)
        )
    }

    private lateinit var pomodoroTimerViewModel: PomodoroTimerViewModel
    private lateinit var notificationHelper: NotificationHelper


    private val reviewManager: ReviewManager by lazy {
        ReviewManagerFactory.create(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Qonversion.shared.syncPurchases()  //testing

        MobileAds.initialize(this)

        //notificationhelper for pomo
        notificationHelper = NotificationHelper(this)

        //viewmodel with notification helper
        pomodoroTimerViewModel = ViewModelProvider(this, object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                //checked, does not cause issues
                @Suppress("UNCHECKED_CAST")
                return PomodoroTimerViewModel(notificationHelper) as T
            }
        })[PomodoroTimerViewModel::class.java]

        //permission request for android 13 higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }


        enableEdgeToEdge()
        setContent {
            LofigramTheme {
                val NavController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {BottomBar(
                        goto_homepage = { targetPage = NavScreensObject.HOME_PAGE
                            NavController.navigate(NavScreensObject.HOME_PAGE)
                            {popUpTo(0) {inclusive = true}}
                        },
                        goto_featurespage = { targetPage = NavScreensObject.FEATURES_PAGE
                            NavController.navigate(NavScreensObject.FEATURES_PAGE)
                            {popUpTo(0) {inclusive = true}}
                        },
                        goto_globalchatpage = { targetPage = NavScreensObject.GLOBAL_CHAT_PAGE
                            NavController.navigate(NavScreensObject.GLOBAL_CHAT_PAGE)
                            {popUpTo(0) {inclusive = true}}
                        },
                        goto_userprofilepage = { targetPage = NavScreensObject.USER_PROFILE_PAGE
                            NavController.navigate(NavScreensObject.USER_PROFILE_PAGE)
                            {popUpTo(0) {inclusive = true}}
                        }
                    )}
                ) { innerPadding ->
                    isGestureNav = isGestureNavigationEnabled(this) //checks the type of navigation
                    errorAlert()
                    loginAlert({
                        NavController.navigate(NavScreensObject.LOGIN_PAGE)
                    })
                    songReqAlert()
                    TrialpackExpireAlert { NavController.navigate(NavScreensObject.UPGRADATION_PAGE) }
                    UpdateAlert()
                    reportIssueAlert()
                    NetworkErrorAlert()
                    AdShower()

                    PurchaseChecker(gotocongratspage = {NavController.navigate(NavScreensObject.CONGRATULATIONS_PAGE)})
                    AppNav(
                        NavController,
                        request_reviewFunction = {
                            requestReview()
                        }
                    )
                    val context = LocalContext.current
                    UpdateCurrentUser(UserPreferencesRespository(context), goto_homepage = {NavController.navigate(
                        NavScreensObject.HOME_PAGE){
                        popUpTo(0) {inclusive = true}
                    }})

                    DisposableEffect(Unit) {
                        onDispose {
                            isLoadingMap.forEach { file, status->
                                if (status){
                                    //exited app while downloading, clear the corrupted files
                                    isLoadingMap[file] = false
                                    File(context.cacheDir, "cached_$file").delete()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //cause after android 13, notification permission is required, before that apprantely you were allowed to bombard with notification
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission is granted, notifications will work
                } else {
                    //if permission is denied, can explain here why the notification is required
                }
            }
        }
    }


    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    }

    fun requestReview(){
        val request: Task<ReviewInfo> = reviewManager.requestReviewFlow()
        request.addOnCompleteListener {
            task->
            try {
                if(task.isSuccessful){
                    //review can be requested
                    val reviewInfo: ReviewInfo = task.result
                    val flow: Task<Void> = reviewManager.launchReviewFlow(this, reviewInfo)
                }
            }catch(e: Exception){
                //need to remove them before production
                errorMessage=e.message.toString()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MediaPlayerManager.StopSong()
        MediaNotificationService.stop(this)
    }
}