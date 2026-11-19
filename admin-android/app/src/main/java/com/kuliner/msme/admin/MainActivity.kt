package com.kuliner.msme.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.kuliner.msme.admin.data.api.TokenManager
import com.kuliner.msme.admin.notification.NotificationCenter
import com.kuliner.msme.admin.notification.OrderMonitorEffect
import com.kuliner.msme.admin.notification.OrderMonitorService
import com.kuliner.msme.admin.ui.MainScreen
import com.kuliner.msme.admin.ui.screens.LoginScreen
import com.kuliner.msme.admin.ui.theme.KulinerAdminTheme

class MainActivity : ComponentActivity() {
    private val openOrdersSignal = mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)

        val tokenManager = TokenManager(this)

        setContent {
            KulinerAdminTheme {
                var isLoggedIn by remember { mutableStateOf(!tokenManager.getToken().isNullOrBlank()) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isLoggedIn) {
                        OrderMonitorEffect()
                        MainScreen(
                            openOrdersSignal = openOrdersSignal.intValue,
                            onLogout = {
                                TokenManager(this@MainActivity).clear()
                                OrderMonitorService.stop(this@MainActivity)
                                NotificationCenter.clear(this@MainActivity)
                                isLoggedIn = false
                            }
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.getBooleanExtra(EXTRA_OPEN_ORDERS, false) == true) {
            openOrdersSignal.intValue += 1
            intent.removeExtra(EXTRA_OPEN_ORDERS)
        }
    }

    companion object {
        const val EXTRA_OPEN_ORDERS = "open_orders"
    }
}
