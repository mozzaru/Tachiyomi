package eu.kanade.tachiyomi.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

class KeepAliveService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Bisa tambah log, logika ringan, atau ping keep-alive di sini
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        // Bersihkan apa pun jika perlu
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, KeepAliveService::class.java)
            context.startService(intent) // Tanpa foreground = tanpa notif
        }

        fun stop(context: Context) {
            val intent = Intent(context, KeepAliveService::class.java)
            context.stopService(intent)
        }
    }
}
