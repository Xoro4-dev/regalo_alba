package com.hugodev.horasconamor

import android.os.Bundle
import androidx.activity.ComponentActivity
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
        enableEdgeToEdge()
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
