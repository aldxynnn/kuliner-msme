package com.kuliner.msme.admin.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuliner.msme.admin.data.api.RetrofitClient
import com.kuliner.msme.admin.data.api.TokenManager
import com.kuliner.msme.admin.data.model.User
import com.kuliner.msme.admin.ui.components.AromaCard
import com.kuliner.msme.admin.ui.components.AromaSectionHeader
import com.kuliner.msme.admin.ui.components.AromaTag
import com.kuliner.msme.admin.ui.theme.*
import com.kuliner.msme.admin.ui.util.friendlyUserName

@Composable
fun ProfileScreen(
    user: User?,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    var profile by remember { mutableStateOf(user) }
    var isLoading by remember { mutableStateOf(user == null) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        if (user != null) {
            profile = user
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = true
        try {
            val response = RetrofitClient.getApiService(context).getMe()
            if (response.isSuccessful) {
                profile = response.body()
            }
        } catch (_: Exception) {
            // Tetap tampilkan data yang tersimpan di perangkat.
        } finally {
            isLoading = false
        }
    }

    val displayName = friendlyUserName(profile?.name)
    val displayEmail = profile?.email?.takeIf { it.isNotBlank() }
        ?: tokenManager.getUserEmail()
        ?: "Belum tersedia"
    val roleLabel = "Kasir"
    val sessionStatus = if (tokenManager.getToken().isNullOrBlank()) "Tidak aktif" else "Sesi aktif"
    val initial = displayName.trim().firstOrNull()?.uppercase() ?: "A"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AromaCream)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Clean White & Soft Orange Operator Identity Card
        AromaCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 16.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = AromaTerracottaSoft
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OPERATOR SHIFT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = AromaTerracotta
                        )
                        AromaTag(
                            label = sessionStatus,
                            backgroundColor = AromaSuccessSoft,
                            textColor = AromaSuccess,
                            borderColor = AromaSuccessBorder
                        )
                    }
                }

                HorizontalDivider(color = AromaTerracottaBorder, thickness = 1.dp)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = AromaTerracotta
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = initial,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AromaSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = displayName,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AromaCharcoal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    AromaTag(
                        label = roleLabel,
                        backgroundColor = AromaTerracottaSoft,
                        textColor = AromaTerracotta,
                        borderColor = AromaTerracottaBorder
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = displayEmail,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = AromaTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = AromaTerracotta,
                trackColor = AromaTerracottaSoft
            )
        }

        // Account Info Section
        AromaSectionHeader(
            overline = "Kredensial",
            title = "Informasi Akun"
        )

        AromaCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 12.dp
        ) {
            Column {
                AccountInfoRow(
                    icon = Icons.Default.Email,
                    label = "Email",
                    value = displayEmail,
                    iconTint = AromaCharcoal,
                    iconBackground = AromaCreamSoft
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = AromaDivider
                )
                AccountInfoRow(
                    icon = Icons.Default.Badge,
                    label = "Peran",
                    value = roleLabel,
                    iconTint = AromaSuccess,
                    iconBackground = AromaSuccessSoft
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = AromaDivider
                )
                AccountInfoRow(
                    icon = Icons.Default.VerifiedUser,
                    label = "Status Sesi",
                    value = sessionStatus,
                    valueColor = if (sessionStatus == "Sesi aktif") AromaSuccess else AromaTextMuted,
                    iconTint = AromaTerracotta,
                    iconBackground = AromaTerracottaSoft
                )
            }
        }

        // Logout Action Section
        AromaSectionHeader(
            overline = "Keamanan",
            title = "Sesi Perangkat"
        )

        Surface(
            onClick = { showLogoutDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = AromaErrorSoft,
            border = BorderStroke(1.dp, AromaErrorBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = AromaError,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Keluar dari Akun",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AromaError
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Anda perlu masuk kembali untuk menggunakan AROMA POS.",
                        fontSize = 12.sp,
                        color = AromaError.copy(alpha = 0.85f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = AromaSurface,
            shape = RoundedCornerShape(18.dp),
            title = {
                Text(
                    text = "Keluar dari akun?",
                    style = MaterialTheme.typography.titleLarge,
                    color = AromaCharcoal
                )
            },
            text = {
                Text(
                    text = "Pastikan semua transaksi sudah tersimpan. Anda akan keluar dari sesi kasir di perangkat ini.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AromaTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AromaError)
                ) {
                    Text("Keluar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", fontWeight = FontWeight.Bold, color = AromaTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun AccountInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color,
    iconBackground: Color,
    valueColor: Color = AromaCharcoal
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(9.dp),
            color = iconBackground
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = AromaTextMuted
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = valueColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
