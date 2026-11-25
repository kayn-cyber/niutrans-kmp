package com.niutrans.app.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class AppPalette(
    val bg: Color,
    val card: Color,
    val cardOut: Color,
    val border: Color,
    val pill: Color,
    val accent: Color,
    val accentFg: Color,
    val pinBg: Color,
    val pinFg: Color,
    val text: Color,
    val muted: Color,
    val error: Color,
)

val LightPalette = AppPalette(
    bg = Color(0xFFF5F7FB),
    card = Color(0xFFFFFFFF),
    cardOut = Color(0xFFFBFDFF),
    border = Color(0xFFE3E8F2),
    pill = Color(0xFFEAEFF8),
    accent = Color(0xFF5B6CFF),
    accentFg = Color.White,
    pinBg = Color(0xFFD1FAE5),
    pinFg = Color(0xFF047857),
    text = Color(0xFF1E293B),
    muted = Color(0xFF64748B),
    error = Color(0xFFE11D48),
)

val DarkPalette = AppPalette(
    bg = Color(0xFF1B1E2B),
    card = Color(0xFF24283B),
    cardOut = Color(0xFF202436),
    border = Color(0xFF323850),
    pill = Color(0xFF2D324A),
    accent = Color(0xFF7C8CFF),
    accentFg = Color.White,
    pinBg = Color(0xFF144337),
    pinFg = Color(0xFF6EE7B7),
    text = Color(0xFFE2E8F0),
    muted = Color(0xFF94A3B8),
    error = Color(0xFFFB7185),
)
