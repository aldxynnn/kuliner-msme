package com.kuliner.msme.admin.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuliner.msme.admin.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(amount: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}

data class StatusVisual(
    val label: String,
    val bg: Color,
    val text: Color,
    val border: Color
)

fun orderStatusVisual(orderStatus: String): StatusVisual = when (orderStatus) {
    "MENUNGGU_PEMBAYARAN" -> StatusVisual(
        label = "Belum Bayar",
        bg = AromaWarningSoft,
        text = AromaWarning,
        border = AromaWarningBorder
    )
    "MENUNGGU_DIPROSES" -> StatusVisual(
        label = "Masuk Antrean",
        bg = AromaInfoSoft,
        text = AromaInfo,
        border = AromaInfoBorder
    )
    "DIPROSES" -> StatusVisual(
        label = "Sedang Dimasak",
        bg = AromaTerracottaSoft,
        text = AromaTerracotta,
        border = AromaTerracottaBorder
    )
    "SIAP_DISAJIKAN" -> StatusVisual(
        label = "Siap Disajikan",
        bg = AromaSuccessSoft,
        text = AromaSuccess,
        border = AromaSuccessBorder
    )
    "SELESAI" -> StatusVisual(
        label = "Selesai",
        bg = AromaCreamSoft,
        text = AromaCharcoal,
        border = AromaBorder
    )
    "DIBATALKAN" -> StatusVisual(
        label = "Dibatalkan",
        bg = AromaErrorSoft,
        text = AromaError,
        border = AromaErrorBorder
    )
    else -> StatusVisual(
        label = orderStatus,
        bg = AromaCreamSoft,
        text = AromaCharcoal,
        border = AromaBorder
    )
}

fun paymentStatusVisual(paymentStatus: String): StatusVisual = when (paymentStatus) {
    "PAID" -> StatusVisual(
        label = "Lunas",
        bg = AromaSuccessSoft,
        text = AromaSuccess,
        border = AromaSuccessBorder
    )
    "CANCELLED" -> StatusVisual(
        label = "Dibatalkan",
        bg = AromaErrorSoft,
        text = AromaError,
        border = AromaErrorBorder
    )
    else -> StatusVisual(
        label = "Belum Bayar",
        bg = AromaWarningSoft,
        text = AromaWarning,
        border = AromaWarningBorder
    )
}

@Composable
fun AromaCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = AromaSurface,
    borderColor: Color = AromaBorder,
    cornerRadius: Dp = 12.dp,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val border = BorderStroke(1.dp, borderColor)
    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            color = containerColor,
            border = border,
            shadowElevation = 0.dp,
            content = content
        )
    } else {
        Surface(
            modifier = modifier,
            shape = shape,
            color = containerColor,
            border = border,
            shadowElevation = 0.dp,
            content = content
        )
    }
}

@Composable
fun AromaStatusBadge(
    visual: StatusVisual,
    modifier: Modifier = Modifier,
    showDot: Boolean = false
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = visual.bg,
        border = BorderStroke(1.dp, visual.border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (showDot) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(visual.text, CircleShape)
                )
            }
            Text(
                text = visual.label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = visual.text,
                maxLines = 1
            )
        }
    }
}

@Composable
fun AromaTag(
    label: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = AromaCreamSoft,
    textColor: Color = AromaCharcoal,
    borderColor: Color? = AromaBorder
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = backgroundColor,
        border = borderColor?.let { BorderStroke(1.dp, it) }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun AromaFilterPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 36.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) AromaCharcoal else AromaSurface,
        border = if (selected) null else BorderStroke(1.dp, AromaBorder)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) AromaSurface else AromaTextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun AromaSegmentedControl(
    options: List<Pair<String, ImageVector?>>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = AromaCreamTrack,
        border = BorderStroke(1.dp, AromaBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            options.forEachIndexed { index, (label, icon) ->
                val isSelected = selectedIndex == index
                Surface(
                    onClick = { onSelect(index) },
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) AromaCharcoal else Color.Transparent,
                    shadowElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (icon != null) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) AromaSurface else AromaTextSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) AromaSurface else AromaTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AromaDashedDivider(
    modifier: Modifier = Modifier,
    color: Color = AromaBorder
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
        )
    }
}

@Composable
fun AromaSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    overline: String? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            if (!overline.isNullOrBlank()) {
                Text(
                    text = overline.uppercase(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp,
                    color = AromaTerracotta
                )
                Spacer(modifier = Modifier.height(2.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = AromaCharcoal
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = AromaTextMuted
                )
            }
        }
        if (trailing != null) {
            trailing()
        }
    }
}

@Composable
fun AromaLoadingState(
    message: String = "Memuat data...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(
                color = AromaCharcoal,
                trackColor = AromaCreamTrack,
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = AromaTextMuted
            )
        }
    }
}

@Composable
fun AromaEmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    iconBackground: Color = AromaCreamSoft,
    iconTint: Color = AromaCharcoal
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(14.dp),
                color = iconBackground,
                border = BorderStroke(1.dp, AromaBorder)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = AromaCharcoal,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AromaTextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AromaFeedbackBanner(
    message: String,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    val bg = if (isError) AromaErrorSoft else AromaSuccessSoft
    val border = if (isError) AromaErrorBorder else AromaSuccessBorder
    val contentColor = if (isError) AromaError else AromaSuccess
    val icon = if (isError) Icons.Default.ErrorOutline else Icons.Default.CheckCircle

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = bg,
        border = BorderStroke(1.dp, border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = message,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun aromaTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = AromaSurface,
    unfocusedContainerColor = AromaCreamSoft,
    focusedBorderColor = AromaCharcoal,
    unfocusedBorderColor = AromaBorder,
    focusedLabelColor = AromaCharcoal,
    unfocusedLabelColor = AromaTextSecondary,
    focusedTextColor = AromaCharcoal,
    unfocusedTextColor = AromaCharcoal,
    cursorColor = AromaTerracotta,
    focusedLeadingIconColor = AromaCharcoal,
    unfocusedLeadingIconColor = AromaTextMuted
)
