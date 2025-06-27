package com.psydrite.lofigram.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.psydrite.lofigram.ui.navigation.NavScreensObject
import com.psydrite.lofigram.ui.navigation.currentPage
import com.psydrite.lofigram.utils.isGestureNav
import com.psydrite.lofigram.R

@Composable
fun BottomBar(
    goto_homepage:()-> Unit,
    goto_featurespage:()-> Unit,
    goto_globalchatpage:()-> Unit,
    goto_userprofilepage:()-> Unit
){
    when(currentPage){
        NavScreensObject.HOME_PAGE, NavScreensObject.FEATURES_PAGE, NavScreensObject.GLOBAL_CHAT_PAGE, NavScreensObject.USER_PROFILE_PAGE -> {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(LocalConfiguration.current.screenHeightDp.dp * 0.08f)
                .offset(y = -(LocalConfiguration.current.screenHeightDp.dp * if (isGestureNav) 0.029f else 0.071f))
            ){
                Column (
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Card (
                        Modifier
                            .fillMaxWidth(0.9f)
                            .border(
                                2.dp,
                                MaterialTheme.colorScheme.onBackground.copy(0.3f),
                                RoundedCornerShape(50)
                            ),
                        shape = RoundedCornerShape(50),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    ){
                        Row (
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            BottomBarIcon(
                                goto_homepage,
                                Icons.Default.Home,
                                NavScreensObject.HOME_PAGE)
                            BottomBarIcon(
                                goto_featurespage,
                                Icons.Default.Favorite,
                                NavScreensObject.FEATURES_PAGE)
                            BottomBarIcon(
                                goto_globalchatpage,
                                ImageVector.vectorResource(id = R.drawable.baseline_chat_24),
                                NavScreensObject.GLOBAL_CHAT_PAGE)
                            BottomBarIcon(
                                goto_userprofilepage,
                                Icons.Default.Person,
                                NavScreensObject.USER_PROFILE_PAGE)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun BottomBarIcon(onClick:()->Unit = {}, imageVector: ImageVector, page:String){
    Box(
        contentAlignment = Alignment.Center
    ){
        if (currentPage ==page){
            IconButton(
                onClick = onClick,
                Modifier.zIndex(2f),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = null
                )
            }
        }else{
            IconButton(
                onClick = onClick,
                Modifier.zIndex(2f),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Unspecified,
                    contentColor = MaterialTheme.colorScheme.onBackground.copy(0.7f)
                )
            ) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = null
                )
            }
        }
    }
}