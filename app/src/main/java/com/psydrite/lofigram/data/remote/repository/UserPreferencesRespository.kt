package com.psydrite.lofigram.data.remote.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRespository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    //this will work as the replacement of room for checking if logged in or not
    private val dataStore = context.dataStore

    //object to store the keys
    companion object {
        private val IS_USER_LOGGED_IN = booleanPreferencesKey("is_user_logged_in")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val GOOGLE_LOG_IN = booleanPreferencesKey("google_log_in")
    }

    val isUserLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_USER_LOGGED_IN] ?: false
    }

    val isGoogleLogIn: Flow<Boolean> = dataStore.data.map {
        preferences->
        //we assume that Google log in is false by default, as it will be stored in datastore
        preferences[GOOGLE_LOG_IN] ?: false
    }
    val userID: Flow<String> = dataStore.data.map { preferences ->
        preferences[USER_ID] ?: ""
    }

    val userName: Flow<String> = dataStore.data.map { preferences ->
        preferences[USER_NAME] ?: ""
    }

    val userEmail: Flow<String> = dataStore.data.map { preferences ->
        preferences[USER_EMAIL] ?: ""
    }

    suspend fun setUserLoggedIn(isUserLoggedIn: Boolean){
        dataStore.edit { preferences ->
            preferences[IS_USER_LOGGED_IN] = isUserLoggedIn
        }
    }

    suspend fun setGoogleLogIn(isGoogleLogIn: Boolean){
        dataStore.edit {
            it[GOOGLE_LOG_IN]= isGoogleLogIn
        }
    }

    suspend fun saveUser(
        userId: String,
        userName: String,
        userEmail: String = ""
    ){
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[USER_NAME] = userName
            preferences[USER_EMAIL] = userEmail
        }
    }

    suspend fun clearUserData() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID)
            preferences.remove(USER_NAME)
            preferences.remove(USER_EMAIL)
        }
    }

    suspend fun nukeAllPreferences(){
        //as it says, use with caution
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}