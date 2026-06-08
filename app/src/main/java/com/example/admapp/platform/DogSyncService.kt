package com.example.admapp.platform

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class DogSyncService : Service() {


    override fun onCreate() {
        super.onCreate()
        Log.d("DogSyncService", "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("DogSyncService", "Background sync executed for Dog Finder")
        stopSelf()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.d("DogSyncService", "Service destroyed")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}