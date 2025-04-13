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
import com.example.sagecpuinfo.data.SystemInfo
import com.example.sagecpuinfo.ui.components.InfoCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DeviceScreen() {
    val context = LocalContext.current
    val systemInfo = SystemInfo(context)
    val deviceInfo = systemInfo.getDeviceInfo()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Device Information",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        InfoCard(
            title = "Device",
            items = listOf(
                "Manufacturer" to deviceInfo.manufacturer,
                "Model" to deviceInfo.model,
                "Device" to deviceInfo.device,
                "Board" to deviceInfo.board,
                "Hardware" to deviceInfo.hardware
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        InfoCard(
            title = "Android",
            items = listOf(
                "Version" to deviceInfo.androidVersion,
                "API Level" to deviceInfo.sdkVersion,
                "Build ID" to deviceInfo.buildId,
                "Build Time" to formatBuildTime(deviceInfo.buildTime)
            )
        )
    }
}

private fun formatBuildTime(timestamp: String): String {
    return try {
        val time = timestamp.toLong()
        val date = Date(time)
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(date)
    } catch (e: NumberFormatException) {
        timestamp
    }
}
