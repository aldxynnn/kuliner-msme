package com.kuliner.msme.admin.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuliner.msme.admin.notification.AppNotification
import com.kuliner.msme.admin.notification.NotificationCenter
import com.kuliner.msme.admin.notification.NotificationType
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun NotificationsScreen(
    onNavigateTo: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val notifications by NotificationCenter.notifications.collectAsState()

    var isRefreshing by remember { mutableStateOf(false) }
    var lastUpdatedAt by remember { mutableStateOf<Long?>(null) }

    fun refresh() {
        scope.launch {
            isRefreshing = true
            NotificationCenter.refresh(context)
            lastUpdatedAt = System.currentTimeMillis()
            isRefreshing = false
        }
    }

    LaunchedEffect(Unit) {
        NotificationCenter.refresh(context)
        NotificationCenter.markAllRead(context)
        lastUpdatedAt = System.currentTimeMillis()
    }

    val unreadCount = notifications.count { !it.isRead }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF8F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (notifications.isEmpty()) {
                        "Tidak ada notifikasi aktif"
                    } else {
                        "${notifications.size} notifikasi perlu ditindaklanjuti"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2B211D)
                )
                Text(
                    text = lastUpdatedAt?.let { "Diperbarui ${formatClock(it)}" } ?: "Memuat notifikasi...",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF81766F)
                )
            }

            if (unreadCount > 0) {
                TextButton(
                    onClick = { NotificationCenter.markAllRead(context) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Tandai dibaca",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE25822)
                    )
                }
            }

            IconButton(onClick = { refresh() }) {
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFFE25822)
                    )
                } else {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Muat ulang notifikasi",
                        tint = Color(0xFF81766F),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (notifications.isEmpty()) {
            EmptyNotifications()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications, key = { it.id }) { item ->
                    NotificationCard(
                        notification = item,
                        onClick = {
                            NotificationCenter.markRead(context, item.id)
                            onNavigateTo(item.route)
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: AppNotification,
    onClick: () -> Unit
) {
    val style = notificationStyle(notification.type)

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFE9E1D9))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = style.background
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = style.icon,
                        contentDescription = null,
                        tint = style.tint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = notification.title,
                        modifier = Modifier.weight(1f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF2B211D),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!notification.isRead) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(Color(0xFFE25822), CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = notification.message,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF81766F),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = formatNotificationTime(notification.createdAt),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = style.tint
                )
            }
        }
    }
}

@Composable
private fun EmptyNotifications() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = Color(0xFFFFF0E8)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.NotificationsNone,
                        contentDescription = null,
                        tint = Color(0xFFE25822),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Belum ada notifikasi",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF2B211D)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Notifikasi akan muncul di sini saat ada pesanan baru, pembayaran menunggu konfirmasi, atau pesanan yang siap disajikan.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF81766F),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

private data class NotificationStyle(
    val icon: ImageVector,
    val background: Color,
    val tint: Color
)

private fun notificationStyle(type: NotificationType): NotificationStyle = when (type) {
    NotificationType.PAYMENT -> NotificationStyle(
        icon = Icons.Default.Payments,
        background = Color(0xFFFFF0E8),
        tint = Color(0xFFE25822)
    )
    NotificationType.NEW_ORDER -> NotificationStyle(
        icon = Icons.AutoMirrored.Filled.ReceiptLong,
        background = Color(0xFFE6F4EA),
        tint = Color(0xFF166534)
    )
    NotificationType.READY_TO_SERVE -> NotificationStyle(
        icon = Icons.Default.Restaurant,
        background = Color(0xFFFEF3C7),
        tint = Color(0xFFB45309)
    )
}

private fun parseIsoDate(raw: String): Date? {
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'"
    )
    patterns.forEach { pattern ->
        try {
            val format = SimpleDateFormat(pattern, Locale.US)
            format.timeZone = TimeZone.getTimeZone("UTC")
            return format.parse(raw)
        } catch (_: Exception) {
            // Coba pola berikutnya.
        }
    }
    return null
}

private fun formatNotificationTime(raw: String): String {
    val date = parseIsoDate(raw) ?: return ""
    val diff = System.currentTimeMillis() - date.time
    return when {
        diff < 0 -> formatClock(date.time)
        diff < 60_000 -> "Baru saja"
        diff < 3_600_000 -> "${diff / 60_000} menit lalu"
        diff < 86_400_000 -> "${diff / 3_600_000} jam lalu"
        else -> {
            val format = SimpleDateFormat("d MMM, HH:mm", Locale("id", "ID"))
            format.format(date)
        }
    }
}

private fun formatClock(epochMillis: Long): String {
    val format = SimpleDateFormat("HH:mm", Locale("id", "ID"))
    return format.format(Date(epochMillis))
}
