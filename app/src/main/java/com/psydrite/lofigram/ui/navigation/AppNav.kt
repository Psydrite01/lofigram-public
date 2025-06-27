package com.psydrite.lofigram.ui.navigation

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.psydrite.lofigram.data.remote.viewmodel.AuthState
import com.psydrite.lofigram.data.remote.viewmodel.AuthViewModel
import com.psydrite.lofigram.data.remote.viewmodel.DataViewModel
import com.psydrite.lofigram.data.remote.viewmodel.PomodoroTimerViewModel
import com.psydrite.lofigram.data.remote.viewmodel.SleepTimerViewModel
import com.psydrite.lofigram.ui.components.OstrichAlgorithm
import com.psydrite.lofigram.ui.components.errorMessage
import com.psydrite.lofigram.ui.components.isErrorAlert
import com.psydrite.lofigram.ui.screens.CongradulationsPage
import com.psydrite.lofigram.ui.screens.FeaturesPage
import com.psydrite.lofigram.ui.screens.GlobalChat
import com.psydrite.lofigram.ui.screens.HomePage
import com.psydrite.lofigram.ui.screens.LoadingPage
import com.psydrite.lofigram.ui.screens.LoginPage
import com.psydrite.lofigram.ui.screens.OnboardingPage
import com.psydrite.lofigram.ui.screens.PersonalizePage
import com.psydrite.lofigram.ui.screens.ScreenSaverPage
import com.psydrite.lofigram.ui.screens.ScreenSaverPlayer
import com.psydrite.lofigram.ui.screens.SubscriptionPage
import com.psydrite.lofigram.ui.screens.UpgradationPage
import com.psydrite.lofigram.ui.screens.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

var isRecentlyLoggedOut by mutableStateOf(false)
var currentPage by mutableStateOf(NavScreensObject.LOADING_PAGE)
var targetPage by mutableStateOf(NavScreensObject.LOADING_PAGE)

private var _isFirstSignIn: Boolean?= true

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNav(
    NavController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    dataViewModel: DataViewModel = hiltViewModel(),
    sleepTimerViewModel: SleepTimerViewModel = viewModel(),
    pomodoroTimerViewModel: PomodoroTimerViewModel = viewModel(),
    request_reviewFunction: ()-> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()

    var subScriptionType by remember{ mutableStateOf<String>("")}
    var shouldLoadSubScriptionType = false

    LaunchedEffect(authState) {
        Log.d("authstate","authstate = ${authState}")
        when(authState){
            is AuthState.SignedIn -> {
                try {
                    val isFirstSignIn = authViewModel.checkIfFirstLogin()
                    val isGoogleLogIn= authViewModel.checkGoogleLogIn()
                    _isFirstSignIn=isFirstSignIn

                    if(isFirstSignIn == false || !isGoogleLogIn){
                        NavController.navigate(NavScreensObject.HOME_PAGE){
                            popUpTo(0) {inclusive = true}
                        }
                    } else if(isFirstSignIn == true || isFirstSignIn == null){
                        NavController.navigate(NavScreensObject.PERSONALIZE_PAGE){
                            popUpTo(0) {inclusive = true}
                        }
                    }
                } catch (e: Exception) {
                    Log.e("AppNav", "Error checking first login status", e)
                    //default behaviour, can choose what to do here
                    NavController.navigate(NavScreensObject.HOME_PAGE){
                        popUpTo(0) {inclusive = true}
                    }
                }
            }
            is AuthState.SignedOut -> {
                delay(100)
                if (isRecentlyLoggedOut){
                    NavController.navigate(NavScreensObject.LOGIN_PAGE){
                        popUpTo(0) {inclusive = true}
                    }
                    isRecentlyLoggedOut = false
                } else {
                    NavController.navigate(NavScreensObject.ONBOARDING_PAGE){
                        popUpTo(0) {inclusive = true}
                    }
                }
            }
            is AuthState.Loading -> {
                //handle login
            }
            is AuthState.Error -> {
                Log.e("AppNav", "Auth error: ${(authState as AuthState.Error).message}")
            }
            is AuthState.Initial -> {
                //dont know what to do with it
            }
        }
    }

    LaunchedEffect(shouldLoadSubScriptionType) {
        subScriptionType = dataViewModel.getSubscriptionType()
    }


    //for errorMessage
    LaunchedEffect(key1 = errorMessage) {
        if (errorMessage !="" && errorMessage !in OstrichAlgorithm){
            isErrorAlert =true
        }
    }

    NavHost(
        navController = NavController,
        startDestination = NavScreensObject.LOADING_PAGE,
    ){
        composable(NavScreensObject.LOADING_PAGE) {
            currentPage = NavScreensObject.LOADING_PAGE
            LoadingPage()
        }
        composable(NavScreensObject.ONBOARDING_PAGE) {
            currentPage = NavScreensObject.ONBOARDING_PAGE
            OnboardingPage(
                goto_welcomepage = { targetPage = NavScreensObject.LOGIN_PAGE
                    NavController.navigate(NavScreensObject.LOGIN_PAGE)}
            )
        }
        composable(NavScreensObject.SUBSCRIPTION_PAGE) {
            currentPage = NavScreensObject.SUBSCRIPTION_PAGE
            SubscriptionPage(
                gotoHomePage = {
                    targetPage = NavScreensObject.HOME_PAGE
                    NavController.navigate(NavScreensObject.HOME_PAGE) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(
            NavScreensObject.UPGRADATION_PAGE,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = {height -> height},
                    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = {height -> height},
                    animationSpec = tween(durationMillis = 500, easing = LinearEasing)
                )
            }
        ) {
            currentPage = NavScreensObject.UPGRADATION_PAGE
            shouldLoadSubScriptionType=true
            UpgradationPage(
                supscriptionType = subScriptionType,
                goto_backPage = {
                    NavController.popBackStack()
                }
            )
            shouldLoadSubScriptionType=false
        }
        composable(NavScreensObject.LOGIN_PAGE) {
            currentPage = NavScreensObject.LOGIN_PAGE

            //google login
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult()
            ) { result ->
                if(result.resultCode == Activity.RESULT_OK){
                    result.data?.let { intent ->
                        authViewModel.handleGoogleSignInResult(intent, context)
                    }
                } else {
                    Log.d("AppNav", "Google sign-in cancelled or failed")
                    authViewModel.resetAuthState()
                }
            }

            LoginPage(
                authState = authState,
                onSignInClick = {
                    coroutineScope.launch {
                        try {
                            val signInIntentSender = authViewModel.signInWithGoogle(context)
                            signInIntentSender?.let { intentSender ->
                                launcher.launch(
                                    IntentSenderRequest.Builder(intentSender).build()
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("AppNav", "Error initiating Google Sign-In", e)
                        }
                    }
                },
                goto_homepage = { targetPage = NavScreensObject.HOME_PAGE
                    NavController.navigate(NavScreensObject.HOME_PAGE)}
            )
        }

        composable(NavScreensObject.PERSONALIZE_PAGE) {
            currentPage = NavScreensObject.PERSONALIZE_PAGE
            PersonalizePage(
                gotoSubscriptionPage = { targetPage = NavScreensObject.SUBSCRIPTION_PAGE
                    NavController.navigate(NavScreensObject.SUBSCRIPTION_PAGE)
                },
                updateFirstSignIn = {
                    authViewModel.updateFirstSignIn()
                },
                isFirstSignIn = _isFirstSignIn,
                uploadSoundTopics = {
                    soundData->
                    dataViewModel.uploadSoundTopics(soundData)
                },
                gotoUserprofile = { targetPage = NavScreensObject.USER_PROFILE_PAGE
                    NavController.navigate(NavScreensObject.USER_PROFILE_PAGE)}
            )
        }
        composable(
            NavScreensObject.HOME_PAGE,
            enterTransition = {
                if (currentPage==NavScreensObject.FEATURES_PAGE
                    || currentPage==NavScreensObject.GLOBAL_CHAT_PAGE
                    || currentPage==NavScreensObject.USER_PROFILE_PAGE){
                    slideInHorizontally(
                        initialOffsetX = {-1000},
                        animationSpec = tween(400)
                    )
                }else{
                    null
                }
            },
            exitTransition = {
                if (targetPage== NavScreensObject.FEATURES_PAGE
                    || targetPage==NavScreensObject.GLOBAL_CHAT_PAGE
                    || targetPage==NavScreensObject.USER_PROFILE_PAGE){
                    slideOutHorizontally(
                        targetOffsetX = {-1000},
                        animationSpec = tween(400)
                    )
                }else{
                    null
                }
            }
        ) {
            currentPage = NavScreensObject.HOME_PAGE
            HomePage(
                goto_subscriptionpage = {
                    targetPage = NavScreensObject.SUBSCRIPTION_PAGE
                    NavController.navigate(NavScreensObject.SUBSCRIPTION_PAGE)
                },
                goto_upgradationPage = {
                    targetPage = NavScreensObject.UPGRADATION_PAGE
                    NavController.navigate(NavScreensObject.UPGRADATION_PAGE)
                },
            )
        }
        composable(
            NavScreensObject.FEATURES_PAGE,
            enterTransition = {
                if (currentPage==NavScreensObject.HOME_PAGE){
                    slideInHorizontally(
                        initialOffsetX = {1000},
                        animationSpec = tween(400)
                    )
                }else if (currentPage==NavScreensObject.GLOBAL_CHAT_PAGE
                    || currentPage==NavScreensObject.USER_PROFILE_PAGE) {
                    slideInHorizontally(
                        initialOffsetX = {-1000},
                        animationSpec = tween(400)
                    )
                }
                else{
                    null
                }
            },
            exitTransition = {
                if (targetPage== NavScreensObject.HOME_PAGE){
                    slideOutHorizontally(
                        targetOffsetX = {1000},
                        animationSpec = tween(400)
                    )
                }else if (targetPage== NavScreensObject.GLOBAL_CHAT_PAGE
                    || targetPage==NavScreensObject.USER_PROFILE_PAGE){
                    slideOutHorizontally(
                        targetOffsetX = {-1000},
                        animationSpec = tween(400)
                    )
                }
                else{
                    null
                }
            }
        ) {
            currentPage = NavScreensObject.FEATURES_PAGE
            FeaturesPage(
                sleepTimerViewModel = sleepTimerViewModel,
                pomodoroTimerViewModel = pomodoroTimerViewModel,
                goto_upgradePage = {
                    NavController.navigate(NavScreensObject.UPGRADATION_PAGE)
                },
                goto_screensaver = { NavController.navigate(NavScreensObject.SCREENSAVER_PAGE) },
            )
        }
        composable(
            NavScreensObject.GLOBAL_CHAT_PAGE,
            enterTransition = {
                if (currentPage==NavScreensObject.HOME_PAGE
                    || currentPage== NavScreensObject.FEATURES_PAGE){
                    slideInHorizontally(
                        initialOffsetX = {1000},
                        animationSpec = tween(400)
                    )
                }else if (currentPage==NavScreensObject.USER_PROFILE_PAGE) {
                    slideInHorizontally(
                        initialOffsetX = {-1000},
                        animationSpec = tween(400)
                    )
                }
                else{
                    null
                }
            },
            exitTransition = {
                if (targetPage== NavScreensObject.HOME_PAGE
                    || targetPage== NavScreensObject.FEATURES_PAGE){
                    slideOutHorizontally(
                        targetOffsetX = {1000},
                        animationSpec = tween(400)
                    )
                }else if (targetPage==NavScreensObject.USER_PROFILE_PAGE){
                    slideOutHorizontally(
                        targetOffsetX = {-1000},
                        animationSpec = tween(400)
                    )
                }
                else{
                    null
                }
            }
            ) {
            currentPage = NavScreensObject.GLOBAL_CHAT_PAGE
            GlobalChat()
        }
        composable(
            NavScreensObject.USER_PROFILE_PAGE,
            enterTransition = {
                if (currentPage==NavScreensObject.FEATURES_PAGE
                    || currentPage==NavScreensObject.GLOBAL_CHAT_PAGE
                    || currentPage==NavScreensObject.HOME_PAGE){
                    slideInHorizontally(
                        initialOffsetX = {1000},
                        animationSpec = tween(400)
                    )
                }else{
                    null
                }
            },
            exitTransition = {
                if (targetPage== NavScreensObject.FEATURES_PAGE
                    || targetPage==NavScreensObject.GLOBAL_CHAT_PAGE
                    || targetPage==NavScreensObject.HOME_PAGE){
                    slideOutHorizontally(
                        targetOffsetX = {1000},
                        animationSpec = tween(400)
                    )
                }else{
                    null
                }
            }
        ) {
            currentPage = NavScreensObject.USER_PROFILE_PAGE
            UserProfile(
                onSignOutClick = {
                    authViewModel.signOut()
                },
                getSoundPreferences = suspend {
                    dataViewModel.getUserSoundTopics()
                },
                onSoundPreferencesEditClick = {
                    //need to write edit logic
                    NavController.navigate(NavScreensObject.PERSONALIZE_PAGE)
                },
                pomodoroTimerViewModel = pomodoroTimerViewModel,
                sleepTimerViewModel = sleepTimerViewModel,
                requestReview = request_reviewFunction
            )
        }
        composable(NavScreensObject.CONGRATULATIONS_PAGE) {
            currentPage = NavScreensObject.CONGRATULATIONS_PAGE
            CongradulationsPage(
                goto_homepage = { targetPage = NavScreensObject.HOME_PAGE
                    NavController.navigate(NavScreensObject.HOME_PAGE)}
            )
        }
        composable(NavScreensObject.SCREENSAVER_PAGE) {
            currentPage = NavScreensObject.SCREENSAVER_PAGE
            ScreenSaverPage(
                goto_player = {NavController.navigate(NavScreensObject.SCREENSAVER_PLAYER)},
                goto_back = {NavController.popBackStack()}
            )
        }
        composable(NavScreensObject.SCREENSAVER_PLAYER) {
            currentPage = NavScreensObject.SCREENSAVER_PLAYER
            ScreenSaverPlayer()
        }

    }
}