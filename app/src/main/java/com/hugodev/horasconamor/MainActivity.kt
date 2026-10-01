package com.hugodev.horasconamor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hugodev.horasconamor.navigation.AppNavigation
import com.hugodev.horasconamor.ui.theme.HorasConAmorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HorasConAmorTheme {
                AppNavigation()
            }
        }
    }
}
