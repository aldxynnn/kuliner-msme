package com.kuliner.msme.admin.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuliner.msme.admin.R
import com.kuliner.msme.admin.data.api.RetrofitClient
import com.kuliner.msme.admin.data.api.TokenManager
import com.kuliner.msme.admin.data.model.LoginRequest
import com.kuliner.msme.admin.ui.components.AromaFeedbackBanner
import com.kuliner.msme.admin.ui.components.aromaTextFieldColors
import com.kuliner.msme.admin.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    var email by remember { mutableStateOf("admin@kuliner.com") }
    var password by remember { mutableStateOf("admin123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun handleLogin() {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Email dan password wajib diisi."
            return
        }

        if (isLoading) return

        errorMessage = null
        keyboard?.hide()
        focusManager.clearFocus()
        isLoading = true

        scope.launch {
            try {
                val response = RetrofitClient
                    .getApiService(context)
                    .login(LoginRequest(email.trim(), password.trim()))

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    tokenManager.saveToken(body.token)
                    tokenManager.saveUserEmail(body.user.email)
                    onLoginSuccess()
                } else {
                    errorMessage = "Email atau password tidak sesuai."
                }
            } catch (e: Exception) {
                errorMessage = "Tidak dapat terhubung ke server. Periksa koneksi Anda."
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AromaCharcoal)
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        // Deep Roast Architectural Brand Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = AromaInkSoft,
                    border = BorderStroke(1.dp, AromaInkDivider)
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo_aroma),
                        contentDescription = "Logo AROMA",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(14.dp))
                    )
                }

                Column {
                    Text(
                        text = "POINT OF SALE · SERANG",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp,
                        color = AromaTerracottaLight
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "AROMA",
                        style = AromaBrandSerifStyle,
                        fontSize = 30.sp,
                        color = AromaSurface
                    )
                    Text(
                        text = "Ayam Goreng Lengkuas",
                        fontSize = 12.sp,
                        color = AromaSurface.copy(alpha = 0.72f)
                    )
                }
            }
        }

        // Elevated Warm Alabaster Paper Sign-In Sheet
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = AromaCream
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "TERMINAL KASIR",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = AromaTerracotta
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Masuk ke Shift Operasional",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = AromaCharcoal
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Gunakan akun kasir atau pengelola resto untuk memproses pesanan dan pembayaran.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AromaTextMuted
                    )

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "Email Akun",
                        style = MaterialTheme.typography.labelMedium,
                        color = AromaCharcoal
                    )
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Email admin atau kasir", style = MaterialTheme.typography.bodyMedium)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        colors = aromaTextFieldColors()
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Password",
                        style = MaterialTheme.typography.labelMedium,
                        color = AromaCharcoal
                    )
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Masukkan password", style = MaterialTheme.typography.bodyMedium)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                    contentDescription = if (passwordVisible) {
                                        "Sembunyikan password"
                                    } else {
                                        "Tampilkan password"
                                    },
                                    tint = AromaTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { handleLogin() }
                        ),
                        colors = aromaTextFieldColors()
                    )

                    if (errorMessage != null) {
                        Spacer(Modifier.height(16.dp))
                        AromaFeedbackBanner(
                            message = errorMessage!!,
                            isError = true
                        )
                    }

                    Spacer(Modifier.height(26.dp))

                    Button(
                        onClick = { handleLogin() },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AromaTerracotta,
                            contentColor = AromaSurface,
                            disabledContainerColor = AromaTerracotta.copy(alpha = 0.55f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = AromaSurface,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Masuk Terminal Kasir",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HorizontalDivider(color = AromaBorder)
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "\"Gurih Rempahnya, Hangat Rasanya.\"",
                        fontFamily = FontFamily.Serif,
                        fontSize = 12.sp,
                        color = AromaTextMuted
                    )
                }
            }
        }
    }
}
