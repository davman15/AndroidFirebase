package com.example.animezone.Notificaciones

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class NotificacionesChat:FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("TAG", "El token es = $token")
    }
}