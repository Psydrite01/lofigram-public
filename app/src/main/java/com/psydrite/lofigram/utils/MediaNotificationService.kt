package com.psydrite.lofigram.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.psydrite.lofigram.MainActivity
import com.psydrite.lofigram.R

class MediaNotificationService : Service() {

    companion object{
        const val CHANNEL_ID = "media_playback_channel"
        const val NOTIFICATION_ID = 1
        const val TAG = "MediaNotificationService"

        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_PREVIOUS = "ACTION_PREVIOUS"
        const val ACTION_NEXT = "ACTION_NEXT"

        var isServiceRunning = false
        private var instance: MediaNotificationService? = null

        @RequiresApi(Build.VERSION_CODES.O)
        fun start(context: Context, track: SoundTrack, isPlaying: Boolean){
            val intent = Intent(context, MediaNotificationService::class.java).apply {
                putExtra("track_name", track.name)
                putExtra("track_desc", track.desc)
                putExtra("track_id", track.idname)
                putExtra("is_playing", isPlaying)
                putExtra("track_genres", track.genre.toTypedArray())
            }

            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, MediaNotificationService::class.java)
            context.stopService(intent)
        }

        fun updatePlaybackState(isPlaying: Boolean) {
            instance?.updatePlaybackState(isPlaying)
        }
    }

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var notificationManager: NotificationManager

    private var currentTrack: SoundTrack? = null
    private var isPlaying = false
    private var albumArtBitmap: Bitmap? = null

    private val mediaButtonReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_PLAY -> handlePlayPause()
                ACTION_PAUSE -> handlePlayPause()
                ACTION_STOP -> handleStop()
                ACTION_PREVIOUS -> handlePrevious()
                ACTION_NEXT -> handleNext()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()
        instance = this
        isServiceRunning = true

        createNotificationChannel()
        initializeMediaSession()

        val filter = IntentFilter().apply {
            addAction(ACTION_PLAY)
            addAction(ACTION_PAUSE)
            addAction(ACTION_STOP)
            addAction(ACTION_PREVIOUS)
            addAction(ACTION_NEXT)
        }
        registerReceiver(mediaButtonReceiver, filter, RECEIVER_NOT_EXPORTED)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val trackName = it.getStringExtra("track_name") ?: ""
            val trackDesc = it.getStringExtra("track_desc") ?: ""
            val trackId = it.getStringExtra("track_id") ?: "0"
            val trackGenres = it.getStringArrayExtra("track_genres")?.toList() ?: emptyList()
            isPlaying = it.getBooleanExtra("is_playing", false)

            currentTrack = SoundTrack(
                idname = trackId,
                name = trackName,
                desc = trackDesc,
                genre = trackGenres,
                creator = "", //need to attribute creators here
            )

            //loading album art
            currentTrack?.let { track ->
                albumArtBitmap = loadAlbumArtBitmap(track)
            }

            startForeground(NOTIFICATION_ID, createNotification())
            updateMediaSessionMetadata()
            updatePlaybackState(isPlaying)
        }

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        isServiceRunning = false
        mediaSession.release()
        unregisterReceiver(mediaButtonReceiver)
        albumArtBitmap?.recycle() //cleaning bitmap on destroy
        albumArtBitmap = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Media Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Media playback controls"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initializeMediaSession() {
        mediaSession = MediaSessionCompat(this, "LofiGramMediaSession").apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    handlePlayPause()
                }

                override fun onPause() {
                    handlePlayPause()
                }

                override fun onStop() {
                    handleStop()
                }

                override fun onSkipToPrevious() {
                    handlePrevious()
                }

                override fun onSkipToNext() {
                    handleNext()
                }
            })

            isActive = true
        }
    }

    private fun loadAlbumArtBitmap(soundTrack: SoundTrack): Bitmap? {
        return try {
            val resourceId = getAlbumArtResourceId(soundTrack)
            Log.d(TAG, "Loading resource ID: $resourceId for genres: ${soundTrack.genre}")

            val originalBitmap = BitmapFactory.decodeResource(resources, resourceId)

            if (originalBitmap == null) {
                Log.e(TAG, "Failed to decode resource ID: $resourceId")
                return null
            }

            //resizig bitmap to 512*512
            val targetSize = 512
            val scaledBitmap = if (originalBitmap.width > targetSize || originalBitmap.height > targetSize) {
                Bitmap.createScaledBitmap(originalBitmap, targetSize, targetSize, true).also {
                    if (it != originalBitmap) originalBitmap.recycle()
                }
            } else {
                originalBitmap
            }
            scaledBitmap

        } catch (e: Exception) {
            Log.e(TAG, "Error loading album art bitmap", e)
            null
        }
    }

    private fun createNotification(): Notification {
        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action(
                R.drawable.ic_pause,
                "Pause",
                createPendingIntent(ACTION_PAUSE)
            )
        } else {
            NotificationCompat.Action(
                R.drawable.ic_play,
                "Play",
                createPendingIntent(ACTION_PLAY)
            )
        }

        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(currentTrack?.name ?: "Unknown Track")
            .setContentText(currentTrack?.desc ?: "Unknown Artist")
            .setSmallIcon(R.drawable.lofigram_logo)
            .setContentIntent(contentIntent)
            .setDeleteIntent(createPendingIntent(ACTION_STOP))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true) //making notification persistent while playing
            .addAction(playPauseAction)


        //adding album art
        albumArtBitmap?.let { bitmap ->
            Log.d(TAG, "Setting large icon with bitmap: ${bitmap.width}x${bitmap.height}")
            notificationBuilder.setLargeIcon(bitmap)
        } ?: Log.w(TAG, "No album art bitmap available")

        //media style
        notificationBuilder.setStyle(
            androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession.sessionToken)
//                .setShowActionsInCompactView(0, 1, 2)
                .setShowActionsInCompactView(0)
                .setShowCancelButton(true)
                .setCancelButtonIntent(createPendingIntent(ACTION_STOP))
        )

        return notificationBuilder.build()
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(action)
        return PendingIntent.getBroadcast(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun updateMediaSessionMetadata() {
        val metadataBuilder = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentTrack?.name)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentTrack?.desc)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "LofiGram")

        albumArtBitmap?.let { bitmap ->
            metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
        } ?: Log.w(TAG, "No album art for media session")

        mediaSession.setMetadata(metadataBuilder.build())
    }

    fun updatePlaybackState(isPlaying: Boolean) {
        this.isPlaying = isPlaying

        val state = if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
        val playbackState = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_STOP
//                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
//                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            )
            .setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1.0f)
            .build()

        mediaSession.setPlaybackState(playbackState)

        //updating notification
        val notification = createNotification()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun handlePlayPause() {
        //to communicate with media player manager
        if (isPlaying) {
            MediaPlayerManager.StopSong()
            stopForeground(true)
            stopSelf()
        } else {
            //need to find a way to pass context so implement play song
        }
    }

    private fun handleStop() {
        MediaPlayerManager.StopSong()
        stopSelf()
    }

    private fun handlePrevious() {
        //need to implement previous logic
    }

    private fun handleNext() {
        //need to implement next logic
    }
}