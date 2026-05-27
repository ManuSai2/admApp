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
        Log.d("DogSyncService", "Dog Finder background sync executed")

        stopSelf()

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}