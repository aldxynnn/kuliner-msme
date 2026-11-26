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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Storefront
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuliner.msme.admin.R
import com.kuliner.msme.admin.data.api.RetrofitClient
import com.kuliner.msme.admin.data.api.TokenManager
import com.kuliner.msme.admin.data.model.LoginRequest
import com.kuliner.msme.admin.ui.components.AromaFeedbackBanner
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

    val loginFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = AromaSurface,
        unfocusedContainerColor = AromaCream,
        focusedBorderColor = AromaTerracotta,
        unfocusedBorderColor = AromaBorder,
        focusedTextColor = AromaCharcoal,
        unfocusedTextColor = AromaCharcoal,
        cursorColor = AromaTerracotta,
        focusedLeadingIconColor = AromaTerracotta,
        unfocusedLeadingIconColor = AromaTextMuted
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AromaSurface)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        // Subtle Top Brand Accent Strip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(AromaTerracotta)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(20.dp))

                // Centered Brand Identity Block
                Surface(
                    modifier = Modifier.size(76.dp),
                    shape = RoundedCornerShape(22.dp),
                    color = AromaTerracottaSoft,
                    border = BorderStroke(1.5.dp, AromaTerracottaBorder)
                ) {
                    Box(
                        modifier = Modifier.padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo_aroma),
                            contentDescription = "Logo AROMA",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(18.dp))
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "AROMA",
                    style = AromaBrandSerifStyle,
                    fontSize = 30.sp,
                    color = AromaTerracotta,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Ayam Goreng Lengkuas",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = AromaTextSecondary
                )

                Spacer(Modifier.height(10.dp))

                Surface(
                    shape = CircleShape,
                    color = AromaTerracottaSoft,
                    border = BorderStroke(1.dp, AromaTerracottaBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Storefront,
                            contentDescription = null,
                            tint = AromaTerracotta,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Kasir & Manajemen POS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AromaTerracotta
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))
                HorizontalDivider(color = AromaDivider, thickness = 1.dp)
                Spacer(Modifier.height(26.dp))

                // Direct Clean Sign-In Form (Zero card-inside-card clutter)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Masuk Shift Operasional",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AromaCharcoal
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Masukkan akun kasir atau pemilik resto untuk mulai melayani.",
                        fontSize = 13.sp,
                        color = AromaTextMuted,
                        lineHeight = 19.sp
                    )

                    Spacer(Modifier.height(22.dp))

                    Text(
                        text = "Email",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
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
                            Text(
                                "contoh@kuliner.com",
                                fontSize = 14.sp,
                                color = AromaTextMuted
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        colors = loginFieldColors
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Password",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
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
                            Text(
                                "Masukkan password",
                                fontSize = 14.sp,
                                color = AromaTextMuted
                            )
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
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { handleLogin() }
                        ),
                        colors = loginFieldColors
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
                        shape = RoundedCornerShape(14.dp),
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
                                text = "Masuk ke Kasir",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Minimal Store Location Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ruko Puri Cempaka · Kota Serang, Banten",
                    fontSize = 11.sp,
                    color = AromaTextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
