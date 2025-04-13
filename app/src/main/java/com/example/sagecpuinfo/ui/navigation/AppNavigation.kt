package com.example.sagecpuinfo.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Battery6Bar
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Phonelink
import androidx.compose.material.icons.filled.SettingsSystemDaydream
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry
import com.example.sagecpuinfo.ui.screens.BatteryScreen
import com.example.sagecpuinfo.ui.screens.CpuScreen
import com.example.sagecpuinfo.ui.screens.DeviceScreen
import com.example.sagecpuinfo.ui.screens.MemoryScreen
import com.example.sagecpuinfo.ui.screens.SystemScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Cpu : Screen("cpu", "CPU", Icons.Filled.Memory)
    object Device : Screen("device", "Device", Icons.Filled.Phonelink)
    object Memory : Screen("memory", "Memory", Icons.Filled.Storage)
    object System : Screen("system", "System", Icons.Filled.SettingsSystemDaydream)
    object Battery : Screen("battery", "Battery", Icons.Filled.Battery6Bar)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Cpu.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Cpu.route) { CpuScreen() }
            composable(Screen.Device.route) { DeviceScreen() }
            composable(Screen.Memory.route) { MemoryScreen() }
            composable(Screen.System.route) { SystemScreen() }
            composable(Screen.Battery.route) { BatteryScreen() }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.Cpu,
        Screen.Device,
        Screen.Memory,
        Screen.System,
        Screen.Battery
    )
    
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}
