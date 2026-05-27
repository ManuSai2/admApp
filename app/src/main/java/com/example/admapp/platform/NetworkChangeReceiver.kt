package com.example.admapp.platform

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NetworkChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(
            context,
            "Dog Finder detectó un cambio de conectividad",
            Toast.LENGTH_SHORT
        ).show()
    }
}