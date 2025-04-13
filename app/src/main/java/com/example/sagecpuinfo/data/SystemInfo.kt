package com.example.sagecpuinfo.data

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.Locale

/**
 * Utility class to collect system information
 */
class SystemInfo(private val context: Context) {

    fun getCpuInfo(): CpuInfo {
        val cpuInfo = CpuInfo()
        
        try {
            val reader = BufferedReader(FileReader("/proc/cpuinfo"))
            var line: String?
            
            while (reader.readLine().also { line = it } != null) {
                line?.let {
                    when {
                        it.startsWith("processor") -> {
                            cpuInfo.cores++
                        }
                        it.startsWith("model name") -> {
                            cpuInfo.modelName = it.split(":").getOrNull(1)?.trim() ?: "Unknown"
                        }
                        it.startsWith("Hardware") -> {
                            cpuInfo.hardware = it.split(":").getOrNull(1)?.trim() ?: "Unknown"
                        }
                        it.startsWith("BogoMIPS") -> {
                            cpuInfo.bogoMIPS = it.split(":").getOrNull(1)?.trim() ?: "Unknown"
                        }
                        it.startsWith("Features") -> {
                            cpuInfo.features = it.split(":").getOrNull(1)?.trim() ?: "Unknown"
                        }
                    }
                }
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        
        // Fallback to Android API if /proc/cpuinfo doesn't provide enough information
        if (cpuInfo.cores == 0) {
            cpuInfo.cores = Runtime.getRuntime().availableProcessors()
        }
        
        if (cpuInfo.modelName.isEmpty()) {
            cpuInfo.modelName = Build.HARDWARE
        }
        
        return cpuInfo
    }
    
    fun getDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            manufacturer = Build.MANUFACTURER.capitalize(),
            model = Build.MODEL,
            device = Build.DEVICE,
            board = Build.BOARD,
            hardware = Build.HARDWARE,
            androidVersion = Build.VERSION.RELEASE,
            sdkVersion = Build.VERSION.SDK_INT.toString(),
            buildId = Build.ID,
            buildTime = Build.TIME.toString()
        )
    }
    
    fun getMemoryInfo(): MemoryInfo {
        val memInfo = MemoryInfo()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        memInfo.totalRam = memoryInfo.totalMem
        memInfo.availableRam = memoryInfo.availMem
        
        try {
            val reader = BufferedReader(FileReader("/proc/meminfo"))
            var line: String?
            
            while (reader.readLine().also { line = it } != null) {
                line?.let {
                    when {
                        it.startsWith("MemTotal") -> {
                            val parts = it.split("\\s+".toRegex())
                            if (parts.size >= 2) {
                                try {
                                    memInfo.totalRam = parts[1].toLong() * 1024
                                } catch (e: NumberFormatException) {
                                    // Keep the value from ActivityManager
                                }
                            }
                        }
                        it.startsWith("MemFree") -> {
                            val parts = it.split("\\s+".toRegex())
                            if (parts.size >= 2) {
                                try {
                                    memInfo.freeRam = parts[1].toLong() * 1024
                                } catch (e: NumberFormatException) {
                                    // Ignore
                                }
                            }
                        }
                        it.startsWith("Cached") && !it.startsWith("CachedSwap") -> {
                            val parts = it.split("\\s+".toRegex())
                            if (parts.size >= 2) {
                                try {
                                    memInfo.cachedRam = parts[1].toLong() * 1024
                                } catch (e: NumberFormatException) {
                                    // Ignore
                                }
                            }
                        }
                    }
                }
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        
        return memInfo
    }
    
    fun getStorageInfo(): StorageInfo {
        val storageInfo = StorageInfo()
        
        // Internal storage
        val internalPath = Environment.getDataDirectory()
        val internalStat = StatFs(internalPath.path)
        storageInfo.internalTotal = internalStat.totalBytes
        storageInfo.internalFree = internalStat.freeBytes
        
        // External storage if available
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val externalPath = Environment.getExternalStorageDirectory()
            val externalStat = StatFs(externalPath.path)
            storageInfo.externalTotal = externalStat.totalBytes
            storageInfo.externalFree = externalStat.freeBytes
        }
        
        return storageInfo
    }
    
    fun getBatteryInfo(): BatteryInfo {
        // This would normally use BatteryManager, but for simplicity we'll return a placeholder
        // In a real app, you'd register a BroadcastReceiver for ACTION_BATTERY_CHANGED
        return BatteryInfo(
            level = "Unknown",
            voltage = "Unknown",
            temperature = "Unknown",
            health = "Unknown",
            technology = "Unknown"
        )
    }
    
    private fun String.capitalize(): String {
        return if (this.isNotEmpty()) {
            this.substring(0, 1).uppercase(Locale.getDefault()) + this.substring(1)
        } else {
            this
        }
    }
}

data class CpuInfo(
    var modelName: String = "",
    var cores: Int = 0,
    var hardware: String = "",
    var bogoMIPS: String = "",
    var features: String = ""
)

data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val device: String,
    val board: String,
    val hardware: String,
    val androidVersion: String,
    val sdkVersion: String,
    val buildId: String,
    val buildTime: String
)

data class MemoryInfo(
    var totalRam: Long = 0,
    var availableRam: Long = 0,
    var freeRam: Long = 0,
    var cachedRam: Long = 0
)

data class StorageInfo(
    var internalTotal: Long = 0,
    var internalFree: Long = 0,
    var externalTotal: Long = 0,
    var externalFree: Long = 0
)

data class BatteryInfo(
    val level: String,
    val voltage: String,
    val temperature: String,
    val health: String,
    val technology: String
)
