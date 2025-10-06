package eu.kanade.tachiyomi

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.NotificationCompat
import eu.kanade.tachiyomi.R
import logcat.LogPriority
import tachiyomi.core.common.util.system.logcat

class ReaderKeepAliveService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start foreground tanpa notification (Android 8+ butuh notification)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = createMinimalNotification()
            startForeground(1001, notification)
        }
        return START_STICKY
    }

    private fun createMinimalNotification(): Notification {
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        } else {
            ""
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("TachiyomiSY")
            .setContentText("Membaca komik")
            .setSmallIcon(R.drawable.ic_glasses_24dp)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "reader_keep_alive"
            val channelName = "Reader Background"
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_MIN)
            channel.lockscreenVisibility = Notification.VISIBILITY_SECRET
            val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(channel)
            return channelId
        }
        return ""
    }
}

// Helper function untuk battery optimization
fun App.checkBatteryOptimization() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val packageName = packageName

        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            } catch (e: Exception) {
                logcat(LogPriority.ERROR, e) { "Failed to request battery optimization" }
            }
        }
    }
}
