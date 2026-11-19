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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuliner.msme.admin.data.api.RetrofitClient
import com.kuliner.msme.admin.data.api.TokenManager
import com.kuliner.msme.admin.data.model.User
import com.kuliner.msme.admin.ui.util.friendlyUserName

private val WarmBg = Color(0xFFFAF8F5)
private val CardWhite = Color(0xFFFFFFFF)
private val Terracotta = Color(0xFFE25822)
private val CharcoalDark = Color(0xFF2B211D)
private val TextMuted = Color(0xFF81766F)
private val SoftOrange = Color(0xFFFFF0E8)
private val BorderColor = Color(0xFFE9E1D9)

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

    val heroGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFE25822), Color(0xFFF27B3D))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Kartu identitas akun
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 5.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(heroGradient)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape,
                        color = CardWhite,
                        shadowElevation = 3.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = initial,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = Terracotta
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = displayName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White.copy(alpha = 0.18f)
                    ) {
                        Text(
                            text = roleLabel,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = displayEmail,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.85f),
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
                color = Terracotta,
                trackColor = SoftOrange
            )
        }

        // Informasi akun
        Text(
            text = "Informasi Akun",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = CharcoalDark
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = CardWhite,
            shadowElevation = 2.dp,
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column {
                AccountInfoRow(
                    icon = Icons.Default.Email,
                    label = "Email",
                    value = displayEmail,
                    iconTint = Terracotta,
                    iconBackground = SoftOrange
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFF1EAE3)
                )
                AccountInfoRow(
                    icon = Icons.Default.Badge,
                    label = "Peran",
                    value = roleLabel,
                    iconTint = Color(0xFF166534),
                    iconBackground = Color(0xFFE6F4EA)
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFF1EAE3)
                )
                AccountInfoRow(
                    icon = Icons.Default.VerifiedUser,
                    label = "Status Sesi",
                    value = sessionStatus,
                    valueColor = if (sessionStatus == "Sesi aktif") Color(0xFF166534) else TextMuted,
                    iconTint = Color(0xFFB45309),
                    iconBackground = Color(0xFFFEF3C7)
                )
            }
        }

        // Aksi keluar
        Text(
            text = "Sesi Perangkat",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = CharcoalDark,
            modifier = Modifier.padding(top = 4.dp)
        )

        Surface(
            onClick = { showLogoutDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFFEF2F2),
            border = BorderStroke(1.dp, Color(0xFFFECACA))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = Color(0xFF991B1B),
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Keluar dari Akun",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF991B1B)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Anda perlu masuk kembali untuk menggunakan AROMA POS.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFB45454)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    "Keluar dari akun?",
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = CharcoalDark
                )
            },
            text = {
                Text(
                    "Pastikan semua transaksi sudah tersimpan. Anda akan keluar dari sesi kasir di perangkat ini.",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC94136))
                ) {
                    Text("Keluar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", fontWeight = FontWeight.Bold, color = Terracotta)
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
    valueColor: Color = CharcoalDark
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(11.dp),
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
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
