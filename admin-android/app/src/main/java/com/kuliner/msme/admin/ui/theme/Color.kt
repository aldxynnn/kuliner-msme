package com.kuliner.msme.admin.ui.theme

import androidx.compose.ui.graphics.Color

// Aroma POS — Clean Orange & White Unified Palette (Matches Customer Web 1:1)
val AromaTerracotta = Color(0xFFEA580C)       // Brand Orange (Primary CTA & active accents)
val AromaTerracottaDark = Color(0xFFC2410C)   // Deep Orange pressed/hover
val AromaTerracottaLight = Color(0xFFFB923C)  // Warm Light Orange accent
val AromaTerracottaSoft = Color(0xFFFFF7ED)   // Soft Orange tint surface
val AromaTerracottaBorder = Color(0xFFFED7AA) // Crisp Orange hairline border

val AromaCream = Color(0xFFF8F9FA)            // Clean Airy Neutral-White canvas
val AromaCreamSoft = Color(0xFFF3F4F6)        // Subtle inset / track surface
val AromaCreamTrack = Color(0xFFEDEFF2)       // Segmented track fill
val AromaReceiptBg = Color(0xFFFFF7ED)        // Soft Orange receipt highlight
val AromaSurface = Color(0xFFFFFFFF)          // Pure White surface

val AromaCharcoal = Color(0xFF1F2937)         // Crisp Slate Text (Typography only — never heavy dark blocks)
val AromaInkSoft = Color(0xFFFFF7ED)          // Soft tint (replaces dark surface)
val AromaInkDivider = Color(0xFFE5E7EB)       // Clean hairline divider
val AromaTextSecondary = Color(0xFF4B5563)    // Secondary body text
val AromaTextMuted = Color(0xFF6B7280)        // Muted metadata text

val AromaBorder = Color(0xFFE5E7EB)           // Crisp hairline border
val AromaDivider = Color(0xFFF1F3F5)          // Subtle inner divider

// Semantic Status Palette (Matches Customer Web)
val AromaSuccess = Color(0xFF15803D)
val AromaSuccessLive = Color(0xFF22C55E)
val AromaSuccessSoft = Color(0xFFF0FDF4)
val AromaSuccessBorder = Color(0xFFBBF7D0)

val AromaWarning = Color(0xFFB45309)
val AromaWarningSoft = Color(0xFFFFFBEB)
val AromaWarningBorder = Color(0xFFFDE68A)

val AromaError = Color(0xFFDC2626)
val AromaErrorSoft = Color(0xFFFEF2F2)
val AromaErrorBorder = Color(0xFFFECACA)

val AromaInfo = Color(0xFF1D4ED8)
val AromaInfoSoft = Color(0xFFEFF6FF)
val AromaInfoBorder = Color(0xFFBFDBFE)

// Legacy aliases mapped cleanly
val PosBg = AromaCream
val PosSurface = AromaSurface
val PosPrimary = AromaTerracotta
val PosPrimaryAccent = AromaTerracottaLight
val PosSoftAccent = AromaTerracottaSoft
val PosMainText = AromaCharcoal
val PosSecondaryText = AromaTextSecondary
val PosBorder = AromaBorder
val PosSuccess = AromaSuccess
val PosWarning = AromaWarning
val PosError = AromaError
