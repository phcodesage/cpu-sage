package com.example.sagecpuinfo.ui.screens

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
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

@Composable
fun BatteryScreen() {
    val context = LocalContext.current
    val batteryInfo = getBatteryInfo(context)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Battery Information",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        InfoCard(
            title = "Battery Status",
            items = listOf(
                "Level" to batteryInfo.level,
                "Status" to batteryInfo.status,
                "Health" to batteryInfo.health,
                "Temperature" to batteryInfo.temperature,
                "Voltage" to batteryInfo.voltage,
                "Technology" to batteryInfo.technology,
                "Charging" to batteryInfo.isCharging,
                "Source" to batteryInfo.chargingSource
            )
        )
    }
}

data class BatteryInfoData(
    val level: String,
    val status: String,
    val health: String,
    val temperature: String,
    val voltage: String,
    val technology: String,
    val isCharging: String,
    val chargingSource: String
)

private fun getBatteryInfo(context: Context): BatteryInfoData {
    val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    val batteryStatus = context.registerReceiver(null, intentFilter)
    
    // Battery level
    val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
    val batteryPct = if (level != -1 && scale != -1) {
        (level * 100 / scale.toFloat()).toInt()
    } else {
        -1
    }
    
    // Battery status
    val status = when (batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1) {
        BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
        BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
        BatteryManager.BATTERY_STATUS_FULL -> "Full"
        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
        BatteryManager.BATTERY_STATUS_UNKNOWN -> "Unknown"
        else -> "Unknown"
    }
    
    // Battery health
    val health = when (batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1) {
        BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
        BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
        BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
        BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified Failure"
        BatteryManager.BATTERY_HEALTH_UNKNOWN -> "Unknown"
        else -> "Unknown"
    }
    
    // Temperature
    val temp = batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
    val temperature = if (temp != -1) {
        String.format("%.1f°C", temp / 10.0)
    } else {
        "Unknown"
    }
    
    // Voltage
    val volt = batteryStatus?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1
    val voltage = if (volt != -1) {
        String.format("%.2fV", volt / 1000.0)
    } else {
        "Unknown"
    }
    
    // Technology
    val technology = batteryStatus?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unknown"
    
    // Charging state
    val isCharging = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) != 0
    
    // Charging source
    val chargingSource = when (batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1) {
        BatteryManager.BATTERY_PLUGGED_AC -> "AC"
        BatteryManager.BATTERY_PLUGGED_USB -> "USB"
        BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
        else -> "Not Charging"
    }
    
    return BatteryInfoData(
        level = "$batteryPct%",
        status = status,
        health = health,
        temperature = temperature,
        voltage = voltage,
        technology = technology,
        isCharging = if (isCharging) "Yes" else "No",
        chargingSource = chargingSource
    )
}
