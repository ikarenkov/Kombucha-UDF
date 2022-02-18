package ru.ikarenkov.teamaker.game.impl.details.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontWeight = FontWeight.W400,
        fontStyle = FontStyle.Normal,
        color = Color(0xB2EEF2FB),
        fontSize = 12.sp,
        lineHeight = 19.sp,
    ),
    body2 = TextStyle(
        fontWeight = FontWeight.W400,
        fontStyle = FontStyle.Normal,
        color = Color(0xB2EEF2FB),
        fontSize = 12.sp,
        letterSpacing = 0.5f.sp,
        lineHeight = 20.sp,
    ),
    h3 = TextStyle(
        fontWeight = FontWeight.W700,
        fontStyle = FontStyle.Normal,
        color = Color.White,
        fontSize = 48.sp,
        lineHeight = 58.sp,
    ),
    h4 = TextStyle(
        fontWeight = FontWeight.W700,
        fontStyle = FontStyle.Normal,
        color = Color.White,
        fontSize = 20.sp,
        lineHeight = 26.sp,
    ),
    h5 = TextStyle(
        fontWeight = FontWeight.W700,
        fontStyle = FontStyle.Normal,
        color = Color(0xFFEEF2FB),
        fontSize = 16.sp,
        lineHeight = 19.2.sp,
        letterSpacing = 0.6f.sp,
    ),
    h6 = TextStyle(
        fontWeight = FontWeight.W400,
        fontStyle = FontStyle.Normal,
        color = Color.White,
        fontSize = 16.sp,
        lineHeight = 19.2.sp,
        letterSpacing = 0.5f.sp
    ),
    subtitle1 = TextStyle(
        fontWeight = FontWeight.W400,
        fontStyle = FontStyle.Normal,
        color = Color.White.copy(alpha = 0.4f),
        fontSize = 12.sp,
        lineHeight = 14.4.sp,
    ),
    button = TextStyle(
        fontWeight = FontWeight.W700,
        fontStyle = FontStyle.Normal,
        color = BackgroundColor,
        fontSize = 20.sp,
        lineHeight = 24.sp,
    ),
    caption = TextStyle(
        fontWeight = FontWeight.W500,
        fontStyle = FontStyle.Normal,
        color = Color(0xFF44A9F4),
        fontSize = 10.sp,
    )
)