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

@Composable
fun MemoryScreen() {
    val context = LocalContext.current
    val systemInfo = SystemInfo(context)
    val memoryInfo = systemInfo.getMemoryInfo()
    val storageInfo = systemInfo.getStorageInfo()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Memory Information",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        InfoCard(
            title = "RAM",
            items = listOf(
                "Total" to formatSize(memoryInfo.totalRam),
                "Available" to formatSize(memoryInfo.availableRam),
                "Free" to formatSize(memoryInfo.freeRam),
                "Cached" to formatSize(memoryInfo.cachedRam),
                "Used" to formatSize(memoryInfo.totalRam - memoryInfo.availableRam),
                "Usage" to formatPercentage(memoryInfo.totalRam, memoryInfo.availableRam)
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        InfoCard(
            title = "Internal Storage",
            items = listOf(
                "Total" to formatSize(storageInfo.internalTotal),
                "Free" to formatSize(storageInfo.internalFree),
                "Used" to formatSize(storageInfo.internalTotal - storageInfo.internalFree),
                "Usage" to formatPercentage(storageInfo.internalTotal, storageInfo.internalFree)
            )
        )
        
        if (storageInfo.externalTotal > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            
            InfoCard(
                title = "External Storage",
                items = listOf(
                    "Total" to formatSize(storageInfo.externalTotal),
                    "Free" to formatSize(storageInfo.externalFree),
                    "Used" to formatSize(storageInfo.externalTotal - storageInfo.externalFree),
                    "Usage" to formatPercentage(storageInfo.externalTotal, storageInfo.externalFree)
                )
            )
        }
    }
}

private fun formatSize(size: Long): String {
    if (size <= 0) return "0 B"
    
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
    
    return String.format("%.2f %s", size / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}

private fun formatPercentage(total: Long, free: Long): String {
    if (total <= 0) return "0%"
    
    val used = total - free
    val percentage = (used.toDouble() / total.toDouble()) * 100
    
    return String.format("%.1f%%", percentage)
}
