package com.kuliner.msme.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.kuliner.msme.admin.data.api.TokenManager
import com.kuliner.msme.admin.ui.MainScreen
import com.kuliner.msme.admin.ui.screens.LoginScreen
import com.kuliner.msme.admin.ui.theme.KulinerAdminTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = TokenManager(this)

        setContent {
            KulinerAdminTheme {
                var isLoggedIn by remember { mutableStateOf(!tokenManager.getToken().isNullOrBlank()) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isLoggedIn) {
                        MainScreen(
                            onLogout = { isLoggedIn = false }
                        )
                    } else {
                        LoginScreen(
                            onLoginSuccess = { isLoggedIn = true }
                        )
                    }
                }
            }
        }
    }
}
