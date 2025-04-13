package com.example.sagecpuinfo.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sagecpuinfo.ui.components.InfoCard
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

@Composable
fun SystemScreen() {
    val context = LocalContext.current
    val kernelInfo = getKernelInfo()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "System Information",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        InfoCard(
            title = "Kernel",
            items = listOf(
                "Version" to kernelInfo.version,
                "Architecture" to (System.getProperty("os.arch") ?: "Unknown"),
                "Hostname" to (System.getProperty("os.name") ?: "Unknown"),
                "Uptime" to formatUptime(kernelInfo.uptime)
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        InfoCard(
            title = "System Properties",
            items = listOf(
                "Java VM" to (System.getProperty("java.vm.version") ?: "Unknown"),
                "OpenGL ES" to (android.opengl.GLES20.glGetString(android.opengl.GLES20.GL_VERSION) ?: "Unknown"),
                "Bootloader" to android.os.Build.BOOTLOADER,
                "SELinux" to getSELinuxStatus()
            )
        )
    }
}

private data class KernelInfo(
    val version: String = "Unknown",
    val uptime: Long = 0
)

private fun getKernelInfo(): KernelInfo {
    var version = "Unknown"
    var uptime: Long = 0
    
    // Get kernel version
    try {
        val process = Runtime.getRuntime().exec("uname -a")
        val reader = process.inputStream.bufferedReader()
        version = reader.readLine() ?: "Unknown"
        reader.close()
    } catch (e: IOException) {
        // Fallback to system property
        version = System.getProperty("os.version") ?: "Unknown"
    }
    
    // Get uptime
    try {
        val reader = BufferedReader(FileReader("/proc/uptime"))
        val line = reader.readLine()
        reader.close()
        
        if (line != null) {
            val uptimeValue = line.split("\\s+".toRegex())[0].toDoubleOrNull() ?: 0.0
            uptime = uptimeValue.toLong()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    
    return KernelInfo(version, uptime)
}

private fun formatUptime(uptime: Long): String {
    if (uptime <= 0) return "Unknown"
    
    val days = uptime / (60 * 60 * 24)
    val hours = (uptime % (60 * 60 * 24)) / (60 * 60)
    val minutes = (uptime % (60 * 60)) / 60
    val seconds = uptime % 60
    
    return when {
        days > 0 -> "$days days, $hours hours, $minutes minutes"
        hours > 0 -> "$hours hours, $minutes minutes"
        minutes > 0 -> "$minutes minutes, $seconds seconds"
        else -> "$seconds seconds"
    }
}

private fun getSELinuxStatus(): String {
    return try {
        val process = Runtime.getRuntime().exec("getenforce")
        val reader = process.inputStream.bufferedReader()
        val status = reader.readLine() ?: "Unknown"
        reader.close()
        status
    } catch (e: IOException) {
        "Unknown"
    }
}
