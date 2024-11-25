package com.example.jetdrivedemoapi.common.utils.manager

import android.Manifest
import android.app.ActivityManager
import android.app.KeyguardManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Context.KEYGUARD_SERVICE
import android.content.Context.POWER_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.PowerManager
import androidx.annotation.RequiresPermission
import com.example.jetdrivedemoapi.common.custom_class.MyLogger

object SoftwareManager {

    private const val TAG = "SoftwareManager"

    private var wakeLock: PowerManager.WakeLock?=null

    fun isScreenLock(context: Context):Boolean{
        return (context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager).isDeviceLocked
    }

    fun makeScreenOffTakingWakeLock(context: Context){
        MyLogger.v(isFunctionCall = true)
        val pm = context.getSystemService(POWER_SERVICE) as PowerManager

        if (wakeLock == null) {
            wakeLock = pm.newWakeLock(
                PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "YourTag:ProximityWakeLock"
            )
        }

        MyLogger.d(msg = "Now taking wake lock for screen on !")
        MyLogger.v(msg = "wakelock is ${if (wakeLock ==null) "null and so that wake lock is not acquire !" else if (wakeLock?.isHeld == true) "not null and wake lock is acquire already !" else "not null and wake lock not acquire !"}")
        wakeLock?.takeIf { !it.isHeld }?.acquire()

    }
    fun removeWakeLock(){
        wakeLock?.takeIf { it.isHeld }?.release()
    }

    fun isForeground(myPackage: String, context: Context): Boolean {
        val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfo = manager.getRunningTasks(1)

        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
            val componentInfo = runningTaskInfo[0].topActivity
            componentInfo!!.packageName == myPackage
        } else {
            if (runningTaskInfo.isEmpty()) false
            else {
                val taskInfo = runningTaskInfo[0]
                taskInfo.isRunning && taskInfo.isVisible
            }
        }
    }

    fun isScreenOn(context: Context): Boolean {
        val pm = context.getSystemService(POWER_SERVICE) as PowerManager
        return pm.isInteractive // check if screen is on
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isNetworkAvailable(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }

        return result
    }

}