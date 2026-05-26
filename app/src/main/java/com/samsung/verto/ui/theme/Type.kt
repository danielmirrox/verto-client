package com.samsung.verto.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.GoogleFont.Provider
import androidx.compose.ui.unit.sp
import com.samsung.verto.R

private val provider = Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val spaceGrotesk = FontFamily(
    Font(GoogleFont("Space Grotesk"), provider, FontWeight.Normal),
    Font(GoogleFont("Space Grotesk"), provider, FontWeight.Medium),
    Font(GoogleFont("Space Grotesk"), provider, FontWeight.SemiBold),
)

private val jetBrainsMono = FontFamily(
    Font(GoogleFont("JetBrains Mono"), provider, FontWeight.Normal),
    Font(GoogleFont("JetBrains Mono"), provider, FontWeight.Medium),
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = spaceGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        letterSpacing = (-0.5).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = spaceGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = spaceGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = spaceGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = spaceGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = spaceGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = jetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        letterSpacing = 0.6.sp,
    ),
)