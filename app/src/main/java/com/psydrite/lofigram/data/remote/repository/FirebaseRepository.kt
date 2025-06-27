package com.psydrite.lofigram.data.remote.repository

import android.accounts.NetworkErrorException
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.TextFieldValue
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.psydrite.lofigram.strings_public
import com.psydrite.lofigram.ui.components.errorMessage
import com.psydrite.lofigram.ui.components.isGlobalChatAlert
import com.psydrite.lofigram.ui.components.showNetworkErrorAlert
import com.psydrite.lofigram.ui.screens.appliedCoupon
import com.psydrite.lofigram.ui.screens.areMessagesLoading
import com.psydrite.lofigram.ui.screens.areSoundsLoading
import com.psydrite.lofigram.ui.screens.cooldownstarter
import com.psydrite.lofigram.ui.screens.isCachedMap
import com.psydrite.lofigram.ui.screens.isLoadingMap
import com.psydrite.lofigram.ui.screens.progressMap
import com.psydrite.lofigram.ui.screens.tempWrittenMessage
import com.psydrite.lofigram.utils.ASMRList
import com.psydrite.lofigram.utils.AddSoundToDefaultLists
import com.psydrite.lofigram.utils.AnimeLoFiList
import com.psydrite.lofigram.utils.CityLifeSoundsList
import com.psydrite.lofigram.utils.ColoredNoisesList
import com.psydrite.lofigram.utils.CrowdsList
import com.psydrite.lofigram.utils.CurrentUserObj
import com.psydrite.lofigram.utils.ElectronicsList
import com.psydrite.lofigram.utils.FavioritesList
import com.psydrite.lofigram.utils.InstrumentsList
import com.psydrite.lofigram.utils.MediaPlayerManager
import com.psydrite.lofigram.utils.NaturalSoundsList
import com.psydrite.lofigram.utils.NatureList
import com.psydrite.lofigram.utils.QuietNoiseSoundsList
import com.psydrite.lofigram.utils.SoundTrack
import com.psydrite.lofigram.utils.RainstormList
import com.psydrite.lofigram.utils.GamingMusicList
import com.psydrite.lofigram.utils.GlobalMessage
import com.psydrite.lofigram.utils.PopularList
import com.psydrite.lofigram.utils.UrbanList
import com.psydrite.lofigram.utils.WaterList
import com.psydrite.lofigram.utils.WindList
import com.psydrite.lofigram.utils.isCurrentUserUpdating
import com.psydrite.lofigram.utils.messageList
import com.psydrite.lofigram.utils.toUpdateSongs
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val _auth: FirebaseAuth,
    private val _db: FirebaseFirestore
) {

    suspend fun GetSoundsData(){
        var count = 0
        while (isCurrentUserUpdating){
            delay(100)
            count ++
            if (count>100){
                errorMessage = "Error getting sounds"
                return
            }
        }
        Log.d("currentuser", "getting sounds from FB")
        Log.d("currentuser", "about to add sounds to liked: ${CurrentUserObj.likedmusic}")

//        var copy = CurrentUserObj.likedmusic

        //empty all the lists
        FavioritesList = emptyList()

        PopularList = emptyList()
        GamingMusicList = emptyList()
        AnimeLoFiList = emptyList()

        WaterList = emptyList()
        ColoredNoisesList = emptyList()
        NatureList = emptyList()
        WindList = emptyList()
        UrbanList = emptyList()
        InstrumentsList = emptyList()
        RainstormList = emptyList()
        ASMRList = emptyList()
        CrowdsList = emptyList()
        ElectronicsList = emptyList()


        var templist = listOf<SoundTrack>()
        areSoundsLoading = true
        try {
            withTimeout(20000) {

                val data = _db.collection("sounds").get().await()

                if (data!=null){
                    templist = data.documents.mapNotNull { doc->
                        val sounds = doc.data
                        sounds.let {
                            SoundTrack(
                                idname = it?.get("idname").toString(),
                                name = it?.get("name").toString(),
                                genre = (it?.get("genre") ?: "") as List<String>,
                                creator = it?.get("creator").toString(),
                                desc = it?.get("desc").toString()
                            )
                        }
                    }
                    templist.forEach { sound->
                        if (sound.idname in CurrentUserObj.likedmusic){
                            FavioritesList = FavioritesList.plus(sound)
                        }else{
                            AddSoundToDefaultLists(sound)
                        }
                    }

                    NaturalSoundsList = (WaterList + RainstormList + NatureList + WindList).toSet().toList().sortedBy { it.name }
                    CityLifeSoundsList = (UrbanList + InstrumentsList + ElectronicsList).toSet().toList().sortedBy { it.name }
                    QuietNoiseSoundsList = (ColoredNoisesList + ASMRList + CrowdsList).toSet().toList().sortedBy { it.name }

                    Log.d("FirebaseRepository", "templist = $templist")
                }
                //now songs

                val data2 = _db.collection("songs").get().await()

                if (data2!=null){
                    var templist2 = listOf<SoundTrack>()
                    templist2 = data2.documents.mapNotNull { doc->
                        val songs = doc.data
                        songs.let {
                            SoundTrack(
                                idname = it?.get("idname").toString(),
                                name = it?.get("name").toString(),
                                genre = (it?.get("genre") ?: "") as List<String>,
                                creator = it?.get("creator").toString(),
                                desc = it?.get("desc").toString()
                            )
                        }
                    }
                    templist2.forEach { sound->
                        if (sound.idname in CurrentUserObj.likedmusic){
                            FavioritesList = FavioritesList.plus(sound)
                        }else{
                            AddSoundToDefaultLists(sound)
                        }
                    }
                    GamingMusicList = GamingMusicList.sortedBy { it.name }
                    AnimeLoFiList = AnimeLoFiList.sortedBy { it.name }
                    PopularList = PopularList.sortedBy { it.name }

                    FavioritesList = FavioritesList.toSet().toList().sortedBy { it.name }

                    toUpdateSongs = false
                }
            }
        } catch (e: NetworkErrorException){
            showNetworkErrorAlert = true
        }
        catch (e: FirebaseNetworkException) {
            showNetworkErrorAlert = true
        }
        catch (e: TimeoutCancellationException) {
            showNetworkErrorAlert = true
        }
        catch (e: Exception) {
            errorMessage = e.message.toString()
        }finally {
            areSoundsLoading = false
        }
    }

    suspend fun saveStringData(collection: String, entry: String, data: String, goto: ()-> Unit = {}){
        _auth.currentUser?.let {
            try {
                val doc = _db.collection(collection).document(it.uid)
                    .get().await()
                if (doc!=null){
                    _db.collection(collection).document(_auth.currentUser!!.uid)
                        .update(entry, data)
                        .addOnSuccessListener {
                            goto()
                        }.addOnFailureListener {
                            errorMessage = it.message.toString()
                        }
                }
            } catch (e: Exception){
                errorMessage = e.message.toString()
            }
        }
    }

    suspend fun checkNullSubscription(goto: () -> Unit){
        _auth.currentUser?.let {
            try {
                val doc = _db.collection("userdata").document(it.uid)
                    .get().await()
                if (doc!=null){
                    val type = doc.data?.get("subscriptionplan") as String?

                    if (type==null){
                        goto()
                    }
                }
            }catch (e: Exception){
                errorMessage = e.message.toString()
            }
        }
    }

    //writing a saperate function for retriving the type of subscription for now, can improvise later

    suspend fun checkSubScriptionType(): String{
        var subscription: String = ""
        _auth.currentUser?.let {
            try {
                val document = _db.collection("userdata").document(it.uid).get().await()

                if(document!=null && document.exists()){
//                    subscription= document.get("subscriptionplan") as String
                    return document.get("subscriptionplan") as String
                }
            }catch (e: Exception){
                errorMessage = e.message.toString()
            }
        }

        return subscription
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun StreamSound(context: Context, name: String, isLoading: MutableState<Boolean>, isPlaying: MutableState<Boolean>, track: SoundTrack) {
        if (isLoading.value){
            //already loading song
        }
        else{
            isLoading.value = true
            Log.d("sounds", "starting download")
            try {
                withTimeout(15000) {
                    val storage = FirebaseStorage.getInstance()
                    val ref = storage.reference

                    val type = if (name[0]=='z') "sounds" else "songs"

                    val soundRef = ref.child("$type/$name")
                    val soundUrl = soundRef.downloadUrl.await().toString()

                    Log.d("sounds", "downloaded: $soundUrl")

                    MediaPlayerManager.PlaySoundFromUrl(context, soundUrl, isPlaying, isLoading, track)
                }
            } catch (e: NetworkErrorException){
                showNetworkErrorAlert = true
            }
            catch (e: FirebaseNetworkException) {
                showNetworkErrorAlert = true
            }
            catch (e: TimeoutCancellationException) {
                showNetworkErrorAlert = true
            }
            catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.e("sounds", "Error: ${e.message}")
            }finally {

            }
        }
    }

    suspend fun DownloadSoundToCache(
        context: Context,
        name: String,
        isLoading: MutableState<Boolean>,
        isExistsInCache: MutableState<Boolean>
    ){
        if (isLoading.value) {
            Log.d("cache", "Already downloading")
            return
        }

        isLoading.value = true
        Log.d("cache", "Starting download")

        try {
            withTimeout(120000) {
                val storage = FirebaseStorage.getInstance()
                val ref = storage.reference
                val type = if (name.startsWith('z')) "sounds" else "songs"
                val soundRef = ref.child("$type/$name")

                // Create a temporary file in cache directory
                val localFile = File(context.cacheDir, "cached_$name")

                // Start the download
                soundRef.getFile(localFile).addOnProgressListener {
                    val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
                    if (progress == 100.0){
                        Log.d("cached", "Downloaded and cached at: ${localFile.absolutePath}")
                        Toast.makeText(context, "Downloaded successfully", Toast.LENGTH_SHORT).show()
                        isExistsInCache.value = true
                    }
                }.await()
            }
        } catch (e: NetworkErrorException){
            //delete corrupted file
            val file = File(context.cacheDir, "cached_$name").delete()
            isExistsInCache.value = false
            showNetworkErrorAlert = true
        }
        catch (e: FirebaseNetworkException) {
            val file = File(context.cacheDir, "cached_$name").delete()
            isExistsInCache.value = false
            showNetworkErrorAlert = true
        }
        catch (e: TimeoutCancellationException) {
            val file = File(context.cacheDir, "cached_$name").delete()
            isExistsInCache.value = false
            showNetworkErrorAlert = true
        }
        catch (e: CancellationException){

        } catch (e: Exception) {
            errorMessage = e.message.toString()
            Toast.makeText(context, "Error downloading, try again", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading.value = false
        }
    }

    suspend fun setUserSoundTopics(soundList: List<String>){
        _auth.currentUser?.let {
            try {
                val document= _db.collection("userdata").document(it.uid).get().await()

                if(document!=null && document.exists()){
                    _db.collection("userdata").document(it.uid).update("soundpreferences", soundList)
                        .addOnSuccessListener {
                            Log.d("DataViewModel", "Sound preferences uploaded to firestore sucessfully")
                        }
                        .addOnFailureListener {
                            errorMessage= it.message.toString()
                            Log.e("DataViewModel", "Failed to upload sound preferences to database")
                        }
                }
            }catch (e: Exception){
                errorMessage = e.message.toString()
            }
        }
    }

    suspend fun getUserSoundTopics(): List<String>{
        _auth.currentUser?.let {
            try {
                val document= _db.collection("userdata").document(it.uid).get().await()

                if(document!=null && document.exists()){
                    val soundPrefernces= document.get("soundpreferences")
                    return when(soundPrefernces){
                        is List<*> -> {
                            soundPrefernces.filterIsInstance<String>()
                        }
                        else -> {
                            Log.w("FirebaseRepository", "soundpreferences is not a List, found: ${soundPrefernces?.javaClass?.simpleName}")
                            emptyList()
                        }
                    }
                }
            }catch (e: Exception){
                errorMessage = e.message.toString()
            }
        }

        return emptyList()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveTimeOut(collection: String, entry: String){
        val timestamp = GetServerTimestamp()?.plusHours(24)

        val futureTimestamp = timestamp?.let {
            java.sql.Timestamp.from(it.atZone(ZoneId.systemDefault()).toInstant())
        }

        _auth.currentUser?.let {
            try {
                val doc = _db.collection(collection).document(it.uid)
                    .get().await()
                if (doc!=null){
                    _db.collection(collection).document(_auth.currentUser!!.uid)
                        .update(entry, futureTimestamp)
                        .addOnSuccessListener {

                        }.addOnFailureListener {
                            errorMessage = it.message.toString()
                        }
                }
            }catch (e: Exception){
                errorMessage = e.message.toString()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun GetServerTimestamp(): LocalDateTime?{
        return try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return null
            val db = FirebaseFirestore.getInstance()

            // Temp write with serverTimestamp
            val tempRef = db.collection("utils").document("server_time")
            tempRef.set(mapOf("timestamp" to FieldValue.serverTimestamp())).await()

            // Read it back after write
            val snapshot = tempRef.get().await()
            val serverTimestamp = snapshot.getTimestamp("timestamp")

            serverTimestamp?.toDate()
                ?.toInstant()
                ?.atZone(ZoneId.systemDefault())
                ?.toLocalDateTime()
        } catch (e: Exception) {
            Log.e("GetServerTimestamp", "Failed to fetch server time", e)
            null
        }
    }

    suspend fun ToggleLikeDislike(context: Context, sound: SoundTrack) {
        _auth.currentUser?.let { user ->
            val docSnapshot = _db.collection("userdata").document(user.uid)
                .get().await()

            //get list (empty if null)
            var liked = docSnapshot.get("likedmusic") as? List<String> ?: emptyList()

            val updatedLiked: List<String>
            val message: String

            if (sound.idname in liked) {
                //remove song
                updatedLiked = liked - sound.idname
                message = "Successfully removed from liked"
            } else {
                //add song
                updatedLiked = liked + sound.idname
                message = "Added to liked"
            }

            _db.collection("userdata").document(user.uid)
                .update("likedmusic", updatedLiked)
                .addOnSuccessListener {
                    toUpdateSongs = true
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    CurrentUserObj.likedmusic = updatedLiked
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Something went wrong, try again", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun NewReq(context: Context, req: String, link: String, todo: () -> Unit){
        val datamap = mapOf(
            "desc" to req,
            "link" to link,
            "username" to CurrentUserObj.username.toString()
        )
        _auth.currentUser?.let { user ->
            _db.collection("requests").document(user.uid)
                .set(datamap, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(context, "Request submitted successfully!", Toast.LENGTH_SHORT).show()
                    todo()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Something went wrong, try again", Toast.LENGTH_SHORT).show()
                }
        }
    }

    suspend fun getAppVersion(): Long{
        return try{
            val document = _db.collection("utils").document("version").get().await()

            if(document!=null && document.exists()){
                document.get("version") as Long
            } else {
                1L
            }
        }catch (e: Exception){
            errorMessage = e.message.toString()
            Log.d("FirebaseRepository","Failed to load app version")
            1L
        }
    }

    suspend fun updateAppVersion(version: Long){
        try{
            val document = _db.collection("utils").document("version").get().await()

            if(document!=null && document.exists()){
                _db.collection("utils").document("version").update("version", version)
            }
        }catch (e: Exception){
//            errorMessage = e.message.toString()
        }
    }

    suspend fun ApplyCoupon(context: Context, code: String){
        var flag = 0
        try {
            val snapshot = _db.collection("utils").document("couponcodes").get().await()

            if (snapshot!= null && snapshot.exists()){
                val couponslist = snapshot.get("codes") as Map<String, String>

                couponslist.forEach { coupon->
                    if (coupon.key == code){
                        appliedCoupon = coupon.value
                        flag = 1
                        Toast.makeText(context, "Coupon applied successfully", Toast.LENGTH_SHORT).show()
                    }
                }
                if (flag == 0){
                    appliedCoupon = "lofigram_"
                    errorMessage = "Oops, the coupon is not valid"
                }
            }else{
                errorMessage = "Error: document does not exist"
            }
        }catch (e: Exception){
            errorMessage = e.message.toString()
        }
    }


    suspend fun DownloadScreenSaverToCache(
        context: Context,
        name: String
    ){
        isLoadingMap[name] = true
        Log.d("cache", "Starting download")

        try {
            withTimeout(300000){
                val storage = FirebaseStorage.getInstance()
                val ref = storage.reference
                val type = "screensavers"
                val soundRef = ref.child("$type/$name")

                // Create a temporary file in cache directory
                val localFile = File(context.cacheDir, "cached_$name")

                // Start the download
                soundRef.getFile(localFile).addOnProgressListener {
                    val progress : Double = (100.0 * it.bytesTransferred) / it.totalByteCount
                    if (progress < 1.0){
                        progressMap[name] = 1.0
                    }else{
                        progressMap[name] = progress
                    }
                    Log.d("cache", "Download progress: $progress%")
                    if (progress == 100.0){
                        isCachedMap[name] = true
                        isLoadingMap[name] = false
                        Toast.makeText(context, "Downloaded successfully", Toast.LENGTH_SHORT).show()
                    }
                }.await()
            }
        }
        catch (e: NetworkErrorException){
            //delete corrupted file
            val file = File(context.cacheDir, "cached_$name").delete()
            isCachedMap[name] = false
            showNetworkErrorAlert = true
        }
        catch (e: FirebaseNetworkException) {
            val file = File(context.cacheDir, "cached_$name").delete()
            isCachedMap[name] = false
            showNetworkErrorAlert = true
        }
        catch (e: TimeoutCancellationException) {
            val file = File(context.cacheDir, "cached_$name").delete()
            isCachedMap[name] = false
            showNetworkErrorAlert = true
        }
        catch (e: CancellationException){

        }
        catch (e: Exception) {
            isLoadingMap[name] = false
            errorMessage = e.message.toString()
            Toast.makeText(context, "Error downloading, try again", Toast.LENGTH_SHORT).show()
        } finally {

        }
    }

    fun SendMessage(message: GlobalMessage){
        try {
            val dbUrl = strings_public.REALTIME_DB_LINK
            val ref = FirebaseDatabase.getInstance(dbUrl).getReference("global_chat")
            val newref = ref.push()
            val messageId = newref.key ?: UUID.randomUUID().toString()

            var messageMap = mutableMapOf(
                "messageId" to messageId,
                "message" to message.message,
                "username" to message.username,
                "time" to ServerValue.TIMESTAMP,
                "isPremium" to message.isPremium,
                "isAnnonymous" to message.isAnnonymous
            )
            newref.setValue(messageMap)
                .addOnSuccessListener {
                    tempWrittenMessage = TextFieldValue("")
                    cooldownstarter = !cooldownstarter
                }
        }catch (e: Exception){
            errorMessage = e.message.toString()
            Log.e("globalchat", "Failed to send message: $errorMessage")
        }
    }

    fun ReceiveMessages() {

        if (areMessagesLoading){
            return
        }

        areMessagesLoading = true
        try {
            val dbUrl = strings_public.REALTIME_DB_LINK
            val ref = FirebaseDatabase.getInstance(dbUrl).getReference("global_chat")

            ref.orderByChild("time").addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val messageId = snapshot.child("messageId").getValue(String::class.java) ?: ""
                    val messageText = snapshot.child("message").getValue(String::class.java) ?: ""
                    val username = snapshot.child("username").getValue(String::class.java) ?: ""
                    val time = snapshot.child("time").getValue(Long::class.java) ?: System.currentTimeMillis()
                    val isPremium = snapshot.child("isPremium").getValue(Boolean::class.java) ?: false
                    val isAnnonymous = snapshot.child("isAnnonymous").getValue(Boolean::class.java) ?: false

                    val message = GlobalMessage(
                        messageId = messageId,
                        message = messageText,
                        username = username,
                        time = time,
                        isPremium = isPremium,
                        isAnnonymous = isAnnonymous
                    )

                    if (message in messageList){
                        //already added
                    }else{
                        messageList = messageList + message
                    }

                    messageList = messageList.sortedBy { it.time }
                    areMessagesLoading = false
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {
                    errorMessage = error.message.toString()
                    areMessagesLoading = false
                }
            })
        }
        catch (e: NetworkErrorException){
            showNetworkErrorAlert = true
        }
        catch (e: FirebaseNetworkException) {
            showNetworkErrorAlert = true
        }
        catch (e: TimeoutCancellationException) {
            showNetworkErrorAlert = true
        }
        catch (e: Exception){
            errorMessage = e.message.toString()
        }
    }

    fun AgreeToChat(){
        try {
            _db.collection("userdata").document(_auth.currentUser!!.uid).update("isAgreedToChat", true)
                .addOnSuccessListener {
                    isGlobalChatAlert = false
                    CurrentUserObj.isAgreedToChat = true
                }
        }catch (e: Exception){
            errorMessage = e.message.toString()
        }
    }
}