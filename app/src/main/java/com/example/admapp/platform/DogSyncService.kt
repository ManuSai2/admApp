package com.example.admapp.platform

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.example.admapp.R

class DogSyncService : Service() {

    companion object {
        private const val CHANNEL_ID   = "dog_sync_channel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("DogSyncService", "Service created")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("DogSyncService", "Background sync executed for Dog Finder")

        // Notificación visible en la barra del sistema
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("DogFinder")
            .setContentText("Sincronizando razas en segundo plano...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)

        stopSelf()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.d("DogSyncService", "Service destroyed")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Sincronización de razas",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notificaciones del servicio de sync de DogFinder"
        }
        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }
}