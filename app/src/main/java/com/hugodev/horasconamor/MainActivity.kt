package com.hugodev.horasconamor

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.hugodev.horasconamor.navigation.AppNavigation
import com.hugodev.horasconamor.ui.OvertimeViewModel
import com.hugodev.horasconamor.ui.theme.HorasConAmorTheme

class MainActivity : ComponentActivity() {
    private lateinit var overtimeViewModel: OvertimeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overtimeViewModel = ViewModelProvider(this)[OvertimeViewModel::class.java]
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            HorasConAmorTheme {
                AppNavigation(overtimeViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::overtimeViewModel.isInitialized) {
            overtimeViewModel.refreshToday()
        }
    }
}
