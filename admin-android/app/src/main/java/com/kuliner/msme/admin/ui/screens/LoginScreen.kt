package com.kuliner.msme.admin.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
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
import kotlinx.coroutines.launch

private val WarmBg = Color(0xFFFAF8F5)
private val CardWhite = Color(0xFFFFFFFF)
private val Terracotta = Color(0xFFE25822)
private val CharcoalDark = Color(0xFF2B211D)
private val TextMuted = Color(0xFF81766F)
private val FieldBg = Color(0xFFF8F6F3)
private val BorderColor = Color(0xFFE9E1D9)

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmBg)
            .systemBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Top Logo & Branding Header
            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(26.dp),
                color = CardWhite,
                shadowElevation = 6.dp,
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(R.drawable.logo_aroma),
                        contentDescription = "Logo AROMA",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(26.dp))
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "AROMA POS",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                color = Terracotta
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Gurih Rempahnya, Hangat Rasanya.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )

            Spacer(Modifier.height(32.dp))

            // Login Card Surface
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = CardWhite,
                border = BorderStroke(1.dp, BorderColor),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Masuk Kasir POS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        color = CharcoalDark
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Masuk ke akun operasional AROMA POS Anda.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "Email",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalDark
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
                            Text("Email admin atau kasir", fontSize = 13.sp)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = Terracotta,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = FieldBg,
                            unfocusedContainerColor = FieldBg,
                            focusedBorderColor = Terracotta,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = CharcoalDark,
                            unfocusedTextColor = CharcoalDark,
                            cursorColor = Terracotta
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Password",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalDark
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
                            Text("Masukkan password", fontSize = 13.sp)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = Terracotta,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    passwordVisible = !passwordVisible
                                }
                            ) {
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
                                    tint = TextMuted,
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
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { handleLogin() }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = FieldBg,
                            unfocusedContainerColor = FieldBg,
                            focusedBorderColor = Terracotta,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = CharcoalDark,
                            unfocusedTextColor = CharcoalDark,
                            cursorColor = Terracotta
                        )
                    )

                    if (errorMessage != null) {
                        Spacer(Modifier.height(14.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEE2E2),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                        ) {
                            Text(
                                text = errorMessage!!,
                                color = Color(0xFF991B1B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = { handleLogin() },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Terracotta,
                            contentColor = CardWhite,
                            disabledContainerColor = Terracotta.copy(alpha = 0.6f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = CardWhite,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Masuk Kasir POS",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "AROMA POS • Ayam Goreng Lengkuas",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}
