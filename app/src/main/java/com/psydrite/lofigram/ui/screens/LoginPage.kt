package com.psydrite.lofigram.ui.screens

import com.psydrite.lofigram.R
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.psydrite.lofigram.data.remote.viewmodel.AuthState
import com.psydrite.lofigram.data.remote.viewmodel.AuthViewModel
import com.psydrite.lofigram.ui.components.showNetworkErrorAlert
import com.psydrite.lofigram.utils.NetworkChecker



@Composable
fun LoginPage(
    authState: AuthState,
    onSignInClick: ()-> Unit,
    goto_homepage: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
){
    val context = LocalContext.current.applicationContext
    when (authState){
        is AuthState.SignedIn ->{
        }

        is AuthState.SignedOut -> {

        }

        is AuthState.Error -> {
            //handling network error
            LaunchedEffect(authState.message) {
                Toast.makeText(context, authState.message, Toast.LENGTH_LONG).show()
            }
        }

        is AuthState.Initial -> {

        }

        is AuthState.Loading -> {

        }
    }

    if(NetworkChecker.isNetworkAvailable(context)==false){
        showNetworkErrorAlert=true
    }

    Box(modifier = Modifier
        .fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 40.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                ){
                    Text(
                        "Welcome",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(30.dp, 30.dp, 0.dp, 10.dp)
                    )
                    Text(
                        "Continue to some mindful experiences 🎶",
                        color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                        modifier = Modifier
                            .padding(horizontal = 30.dp)
                            .fillMaxWidth(0.6f)
                    )

                }

                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.15f)
                        .zIndex(-1f)
                        .align(Alignment.BottomEnd),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ){
                    Image(
                        painter = painterResource(R.drawable.loginpage_art),
                        contentDescription = "music",
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(end = 10.dp)
                    )
                }
            }


            Card (modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 20.dp),
                shape = RoundedCornerShape(5),
                colors = CardDefaults.cardColors(
                    containerColor = lerp(
                        MaterialTheme.colorScheme.tertiary,
                        MaterialTheme.colorScheme.background,
                        0.5f
                    )
                )
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Text(
                        "Starting in June 2025, only Google sign-in is supported to ensure account protection and billing. We appreciate your understanding.",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 9.dp),
                shape = RoundedCornerShape(5),
                colors = CardDefaults.cardColors(
                    containerColor = lerp(
                        MaterialTheme.colorScheme.tertiary,
                        MaterialTheme.colorScheme.background,
                        0.5f
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        "Unlock these features by logging in:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                    )

                    FeatureComposable("Personalized recommendations and favorites", "PremiumTrialpackBasic")
                    FeatureComposable("Join global community chat", "PremiumTrialpackBasic")
                    FeatureComposable("Request your favorite content directly", "PremiumTrialpackBasic")
                }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 25.dp)
                    .shadow(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(15)
                    ),
                shape = RoundedCornerShape(15),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                onClick = {
                    //logic
                    onSignInClick()
                }
            ) {
                Text("Personalize your experience  ")
                Icon(
                    painter = painterResource(id = R.drawable.google_icon),
                    contentDescription = "google login",
                    modifier = Modifier.size(30.dp),
                    tint = Color.Unspecified
                )
            }

            IconButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 5.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground.copy(0.7f)
                ),
                onClick = {
                    authViewModel.updateDatastoreForLocalLogin()
                    goto_homepage()
                }
            ) {
                Text("Continue as guest")
            }
        }
    }
}