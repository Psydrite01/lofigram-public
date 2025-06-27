package com.psydrite.lofigram.data.remote.models

data class UserModel(
    val userName: String? = null,
    val userEmail: String? = null,
    //initially considered non premium user by default
    val isPremiumUser: Boolean? = false,
    //initially considered as first signin
    val isFirstSignIn: Boolean? = true
)