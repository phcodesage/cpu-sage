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
import com.example.sagecpuinfo.data.CpuInfo
import com.example.sagecpuinfo.data.SystemInfo
import com.example.sagecpuinfo.ui.components.InfoCard

@Composable
fun CpuScreen() {
    val context = LocalContext.current
    val systemInfo = SystemInfo(context)
    val cpuInfo = systemInfo.getCpuInfo()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "CPU Information",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        CpuInfoContent(cpuInfo)
    }
}

@Composable
fun CpuInfoContent(cpuInfo: CpuInfo) {
    InfoCard(
        title = "Processor",
        items = listOf(
            "Model" to cpuInfo.modelName,
            "Hardware" to cpuInfo.hardware,
            "Cores" to cpuInfo.cores.toString(),
            "BogoMIPS" to cpuInfo.bogoMIPS
        )
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    // Split features into a readable format
    val featuresList = cpuInfo.features.split(" ")
    val featuresItems = featuresList.chunked(4).map { chunk ->
        chunk.joinToString(", ")
    }
    
    InfoCard(
        title = "Features",
        items = featuresItems.mapIndexed { index, value -> 
            "Set ${index + 1}" to value 
        }
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    // Additional CPU information from Android API
    InfoCard(
        title = "Architecture",
        items = listOf(
            "ABI" to (android.os.Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown"),
            "All ABIs" to android.os.Build.SUPPORTED_ABIS.joinToString(", ")
        )
    )
}
