package eu.kanade.tachiyomi.util.system

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.core.content.getSystemService
import logcat.LogPriority
import tachiyomi.core.common.util.system.logcat
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

object DeviceUtil {

    val isMiui: Boolean by lazy {
        getSystemProperty("ro.miui.ui.version.name")?.isNotEmpty() ?: false
    }

    val miuiMajorVersion: Int? by lazy {
        if (!isMiui) return@lazy null
        Build.VERSION.INCREMENTAL
            .substringBefore('.')
            .trimStart('V')
            .toIntOrNull()
    }

    @SuppressLint("PrivateApi")
    fun isMiuiOptimizationDisabled(): Boolean {
        val sysProp = getSystemProperty("persist.sys.miui_optimization")
        if (sysProp == "0" || sysProp == "false") {
            return true
        }
        return try {
            Class.forName("android.miui.AppOpsUtils")
                .getDeclaredMethod("isXOptMode")
                .invoke(null) as Boolean
        } catch (e: Exception) {
            false
        }
    }

    val isSamsung: Boolean by lazy {
        Build.MANUFACTURER.equals("samsung", ignoreCase = true)
    }

    // --- DETEKSI VIVO ---
    val isVivo by lazy {
        val prop = getSystemProperty("ro.vivo.os.name")
        !prop.isNullOrBlank() && prop.contains("funtouch", true)
    }

    // --- DETEKSI OPPO & REALME (FIX BARU) ---
    val isOppo by lazy {
        Build.MANUFACTURER.equals("oppo", ignoreCase = true)
    }

    val isRealme by lazy {
        Build.MANUFACTURER.equals("realme", ignoreCase = true)
    }

    val isOnePlus by lazy {
        Build.MANUFACTURER.equals("oneplus", ignoreCase = true)
    }

    val oneUiVersion: Double? by lazy {
        try {
            val semPlatformIntField = Build.VERSION::class.java.getDeclaredField("SEM_PLATFORM_INT")
            val version = semPlatformIntField.getInt(null) - 90000
            if (version < 0) {
                1.0
            } else {
                ((version / 10000).toString() + "." + version % 10000 / 100).toDouble()
            }
        } catch (e: Exception) {
            null
        }
    }

    val invalidDefaultBrowsers = listOf(
        "android",
        "com.hihonor.android.internal.app",
        "com.huawei.android.internal.app",
        "com.zui.resolver",
        "com.transsion.resolver",
        "com.android.intentresolver",
    )

    /**
    fun isLowRamDevice(context: Context): Boolean {
        val memInfo = ActivityManager.MemoryInfo()
        context.getSystemService<ActivityManager>()!!.getMemoryInfo(memInfo)
        val totalMemBytes = memInfo.totalMem
        return totalMemBytes < 3L * 1024 * 1024 * 1024
    }
    */

    @SuppressLint("PrivateApi")
    private fun getSystemProperty(key: String?): String? {
        return try {
            Class.forName("android.os.SystemProperties")
                .getDeclaredMethod("get", String::class.java)
                .invoke(null, key) as String
        } catch (e: Exception) {
            logcat(LogPriority.WARN, e) { "Unable to use SystemProperties.get()" }
            null
        }
    }

    // --- PERBAIKAN UNTUK ERROR VIVO LEGACY CODE ---
    // Kode di bawah ini memperbaiki error "Unresolved reference Activity/Window/Logger"

    enum class CutoutSupport {
        NONE,
        MODERN,
        LEGACY,
    }

    enum class LegacyCutoutMode {
        SHORT_EDGES,
        NEVER,
    }

    fun hasCutout(activity: Activity): CutoutSupport {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            CutoutSupport.MODERN
        } else if (isVivo && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Deteksi Legacy Vivo Cutout (FuntouchOS lama)
            try {
                val classLoader = activity.classLoader
                @SuppressLint("PrivateApi")
                val ftFeature = classLoader.loadClass("android.util.FtFeature")
                val isFeatureSupport = ftFeature.getMethod("isFeatureSupport", Int::class.javaPrimitiveType)
                // 0x00000020 = common_notch_screen_support
                val result = isFeatureSupport.invoke(ftFeature, 0x00000020) as Boolean
                if (result) CutoutSupport.LEGACY else CutoutSupport.NONE
            } catch (e: Exception) {
                CutoutSupport.NONE
            }
        } else {
            CutoutSupport.NONE
        }
    }

    @SuppressLint("PrivateApi")
    fun setLegacyCutoutMode(window: Window, mode: LegacyCutoutMode) {
        if (isVivo) {
            try {
                // Menggunakan reflection untuk mengakses API privat Vivo
                val method: Method = Class.forName("android.util.FtDeviceInfo")
                    .getMethod("getDeviceType")
                val deviceType = method.invoke(null) as String
                
                // Cek tipe device jika diperlukan, atau langsung eksekusi
                // Catatan: Implementasi di bawah mencoba set flag full screen Vivo
                val params = window.attributes
                val field = params.javaClass.getDeclaredField("mFtCutoutMode")
                field.isAccessible = true
                
                val modeValue = when (mode) {
                    LegacyCutoutMode.SHORT_EDGES -> 1 // Flag untuk fill cutout
                    LegacyCutoutMode.NEVER -> 0
                }
                field.setInt(params, modeValue)
                window.attributes = params
            } catch (e: Exception) {
                // Gagal set mode, abaikan agar tidak crash
                logcat(LogPriority.ERROR, e) { "Failed to set legacy cutout mode" }
            }
        }
    }
}
