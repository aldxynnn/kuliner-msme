
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

private val Cream = Color(0xFFFAF8F5)
private val White = Color(0xFFFFFFFF)
private val Orange = Color(0xFFE6531B)
private val Brown = Color(0xFF30241F)
private val Muted = Color(0xFF81766F)
private val Field = Color(0xFFF8F6F3)
private val Border = Color(0xFFEAE4DE)

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
            .background(Cream)
            .systemBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Brand berada di luar dan di atas form
            Spacer(Modifier.height(12.dp))

            Image(
                painter = painterResource(R.drawable.logo_aroma),
                contentDescription = "Logo AROMA",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(104.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "AROMA POS",
                fontSize = 27.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp,
                color = Brown
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "Gurih Rempahnya, Hangat Rasanya.",
                fontSize = 12.sp,
                color = Muted
            )

            Spacer(Modifier.height(38.dp))

            // Login form
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = White,
                border = BorderStroke(1.dp, Border),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Masuk",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                        color = Brown
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Masuk ke akun admin atau kasir Anda.",
                        fontSize = 13.sp,
                        color = Muted
                    )

                    Spacer(Modifier.height(26.dp))

                    Text(
                        text = "Email",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Brown
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Email admin atau kasir")
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = Muted
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(11.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Field,
                            unfocusedContainerColor = Field,
                            focusedBorderColor = Orange,
                            unfocusedBorderColor = Border,
                            focusedTextColor = Brown,
                            unfocusedTextColor = Brown,
                            cursorColor = Orange
                        )
                    )

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = "Password",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Brown
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Masukkan password")
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = Muted
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
                                    tint = Muted
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(11.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { handleLogin() }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Field,
                            unfocusedContainerColor = Field,
                            focusedBorderColor = Orange,
                            unfocusedBorderColor = Border,
                            focusedTextColor = Brown,
                            unfocusedTextColor = Brown,
                            cursorColor = Orange
                        )
                    )

                    if (errorMessage != null) {
                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = errorMessage!!,
                            color = Color(0xFFB9382A),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = { handleLogin() },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(11.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Orange,
                            contentColor = White,
                            disabledContainerColor = Orange.copy(alpha = 0.6f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 0.dp
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Masuk",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            Text(
                text = "Sistem Manajemen Ayam Goreng Lengkuas",
                fontSize = 11.sp,
                color = Muted
            )

            Spacer(Modifier.height(12.dp))
        }
    }
}
