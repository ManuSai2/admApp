package com.example.admapp.platform

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast

class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            Log.d("NetworkChangeReceiver", "Connectivity change detected")
            Toast.makeText(
                context,
                "Cambio de conectividad detectado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}