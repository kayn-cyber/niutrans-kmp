package com.niutrans.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niutrans.app.model.AppConfig
import com.niutrans.app.model.LanguageOption
import com.niutrans.app.model.NIUTRANS_MAX_CHARS
import com.niutrans.app.model.SUPPORTED_LANGUAGES
import com.niutrans.app.model.TARGET_LANGUAGES
import com.niutrans.app.network.NiuTransClient
import com.niutrans.app.network.TranslationException
import com.niutrans.app.storage.ConfigStore
import com.niutrans.app.storage.createConfigStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class DesktopControls(
    val isMiniMode: Boolean = false,
    val onTogglePin: () -> Unit = {},
    val onCycleOpacity: () -> Unit = {},
    val onToggleMiniMode: () -> Unit = {},
    val onClose: () -> Unit = {},
    val headerContainer: @Composable (content: @Composable () -> Unit) -> Unit = { it() },
)

@Composable
fun TranslatorApp(
    isDesktop: Boolean = false,
    desktopControls: DesktopControls = DesktopControls(),
    configStore: ConfigStore = remember { createConfigStore() },
    client: NiuTransClient = remember { NiuTransClient() },
    onConfigChanged: (AppConfig) -> Unit = {},
) {
    var config by remember { mutableStateOf(configStore.load()) }
    var sourceText by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("") }
    var isTranslating by remember { mutableStateOf(false) }
    var statusMessage by remember {
        mutableStateOf(
            if (config.apiKey.isBlank()) "点右上角 ⚙ Key 配置密钥即可开始" else "准备就绪 ✨"
        )
    }
    var isError by remember { mutableStateOf(config.apiKey.isBlank()) }
    var showSettings by remember { mutableStateOf(false) }
    var copiedFeedback by remember { mutableStateOf(false) }

    val palette = if (config.isDarkTheme) DarkPalette else LightPalette
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    fun updateConfig(newConfig: AppConfig) {
        config = newConfig
        configStore.save(newConfig)
        onConfigChanged(newConfig)
    }

    fun triggerTranslate(input: String = sourceText) {
        val cleaned = input.trim()
        if (cleaned.isEmpty()) {
            statusMessage = "先输入或粘贴点内容吧"
            isError = true
            return
        }
        if (config.apiKey.isBlank()) {
            statusMessage = "请先填写小牛翻译 API Key"
            isError = true
            showSettings = true
            return
        }
        scope.launch {
            isTranslating = true
            isError = false
            statusMessage = "正在翻译中..."
            try {
                val res = client.translate(
                    text = cleaned,
                    apiKey = config.apiKey,
                    fromLang = config.fromLang,
                    toLang = config.toLang,
                )
                targetText = res.tgtText
                val fromName = SUPPORTED_LANGUAGES.find { it.code == res.fromLang }?.label ?: res.fromLang
                val toName = SUPPORTED_LANGUAGES.find { it.code == res.toLang }?.label ?: res.toLang
                statusMessage = "✨ $fromName → $toName"
                isError = false
            } catch (e: TranslationException) {
                statusMessage = e.message ?: "翻译失败"
                isError = true
            } finally {
                isTranslating = false
            }
        }
    }

    LaunchedEffect(copiedFeedback) {
        if (copiedFeedback) {
            delay(1500)
            copiedFeedback = false
        }
    }

    MaterialTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .then(if (!isDesktop) Modifier.safeDrawingPadding() else Modifier),
            shape = if (isDesktop) RoundedCornerShape(16.dp) else RoundedCornerShape(0.dp),
            color = palette.bg.copy(alpha = if (isDesktop) config.opacity else 1f),
            border = if (isDesktop) BorderStroke(1.dp, palette.border) else null,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Header
                desktopControls.headerContainer {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "✨ 小牛翻译",
                            color = palette.text,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.weight(1f))

                        if (isDesktop) {
                            PillChip(
                                text = if (config.isPinned) "📌 已置顶" else "📌 置顶",
                                bg = if (config.isPinned) palette.pinBg else palette.pill,
                                fg = if (config.isPinned) palette.pinFg else palette.muted,
                                onClick = {
                                    updateConfig(config.copy(isPinned = !config.isPinned))
                                    desktopControls.onTogglePin()
                                },
                            )
                            Spacer(Modifier.width(5.dp))
                        }

                        PillChip(
                            text = "⚙ Key",
                            bg = palette.pill,
                            fg = palette.text,
                            onClick = { showSettings = !showSettings },
                        )
                        Spacer(Modifier.width(5.dp))

                        PillChip(
                            text = if (config.isDarkTheme) "☀️" else "🌙",
                            bg = palette.pill,
                            fg = palette.text,
                            onClick = { updateConfig(config.copy(isDarkTheme = !config.isDarkTheme)) },
                        )

                        if (isDesktop) {
                            Spacer(Modifier.width(5.dp))
                            PillChip(
                                text = if (desktopControls.isMiniMode) "▴" else "▾",
                                bg = palette.pill,
                                fg = palette.muted,
                                onClick = desktopControls.onToggleMiniMode,
                            )
                            Spacer(Modifier.width(5.dp))
                            PillChip(
                                text = "✕",
                                bg = palette.pill,
                                fg = palette.muted,
                                onClick = desktopControls.onClose,
                            )
                        }
                    }
                }

                if (!isDesktop || !desktopControls.isMiniMode) {
                    // Inline Settings Drawer
                    AnimatedVisibility(visible = showSettings) {
                        ApiKeyDrawer(
                            initialKey = config.apiKey,
                            palette = palette,
                            onSave = { newKey ->
                                updateConfig(config.copy(apiKey = newKey.trim()))
                                showSettings = false
                                isError = false
                                statusMessage = "API Key 已保存 ✨"
                            },
                        )
                    }

                    // Language & Mode Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LanguageDropdown(
                            selectedCode = config.fromLang,
                            options = SUPPORTED_LANGUAGES,
                            palette = palette,
                            onSelect = { updateConfig(config.copy(fromLang = it)) },
                        )
                        Spacer(Modifier.width(4.dp))
                        PillChip(
                            text = "⇄",
                            bg = palette.pill,
                            fg = palette.text,
                            onClick = {
                                if (config.fromLang != "auto") {
                                    updateConfig(
                                        config.copy(fromLang = config.toLang, toLang = config.fromLang)
                                    )
                                }
                            },
                        )
                        Spacer(Modifier.width(4.dp))
                        LanguageDropdown(
                            selectedCode = config.toLang,
                            options = TARGET_LANGUAGES,
                            palette = palette,
                            onSelect = { updateConfig(config.copy(toLang = it)) },
                        )

                        Spacer(Modifier.weight(1f))

                        PillChip(
                            text = if (config.autoTranslate) "⚡自动开" else "⚡自动",
                            bg = if (config.autoTranslate) palette.pinBg else palette.pill,
                            fg = if (config.autoTranslate) palette.pinFg else palette.muted,
                            onClick = { updateConfig(config.copy(autoTranslate = !config.autoTranslate)) },
                        )

                        if (isDesktop) {
                            Spacer(Modifier.width(4.dp))
                            PillChip(
                                text = "💧${(config.opacity * 100).roundToInt()}%",
                                bg = palette.pill,
                                fg = palette.muted,
                                onClick = desktopControls.onCycleOpacity,
                            )
                        }
                    }

                    // Source Input Card
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = palette.card,
                        border = BorderStroke(1.dp, palette.border),
                    ) {
                        Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                            if (sourceText.isEmpty()) {
                                Text(
                                    text = "输入或粘贴想翻译的内容...",
                                    color = palette.muted.copy(alpha = 0.7f),
                                    fontSize = 14.sp,
                                )
                            }
                            BasicTextField(
                                value = sourceText,
                                onValueChange = { updated ->
                                    sourceText = updated
                                    if (config.autoTranslate) {
                                        debounceJob?.cancel()
                                        if (updated.isNotBlank()) {
                                            debounceJob = scope.launch {
                                                delay(650)
                                                triggerTranslate(updated)
                                            }
                                        }
                                    }
                                },
                                textStyle = TextStyle(
                                    color = palette.text,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                ),
                                cursorBrush = SolidColor(palette.accent),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .onPreviewKeyEvent { event ->
                                        if (
                                            event.type == KeyEventType.KeyDown &&
                                            event.key == Key.Enter &&
                                            (event.isCtrlPressed || event.isMetaPressed)
                                        ) {
                                            triggerTranslate()
                                            true
                                        } else {
                                            false
                                        }
                                    },
                            )
                        }
                    }

                    // Action Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Button(
                            onClick = { triggerTranslate() },
                            enabled = !isTranslating,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.accent,
                                contentColor = palette.accentFg,
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = if (isTranslating) "翻译中..." else "翻译",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        Spacer(Modifier.width(6.dp))
                        PillChip(
                            text = "📋 贴入即译",
                            bg = palette.pill,
                            fg = palette.text,
                            onClick = {
                                val clip = clipboard.getText()?.text.orEmpty()
                                if (clip.isBlank()) {
                                    statusMessage = "剪贴板里没有文本"
                                    isError = true
                                } else {
                                    sourceText = clip
                                    triggerTranslate(clip)
                                }
                            },
                        )

                        Spacer(Modifier.width(6.dp))
                        PillChip(
                            text = "清空",
                            bg = palette.pill,
                            fg = palette.muted,
                            onClick = {
                                sourceText = ""
                                targetText = ""
                                isError = false
                                statusMessage = "已清空"
                            },
                        )

                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "${sourceText.trim().length}/$NIUTRANS_MAX_CHARS",
                            color = if (sourceText.trim().length > NIUTRANS_MAX_CHARS) palette.error else palette.muted,
                            fontSize = 12.sp,
                        )
                    }

                    // Output Card
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = palette.cardOut,
                        border = BorderStroke(1.dp, palette.border),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(10.dp),
                        ) {
                            Text(
                                text = targetText,
                                color = palette.text,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                            )
                        }
                    }

                    // Bottom Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = statusMessage,
                            color = if (isError) palette.error else palette.muted,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(Modifier.width(8.dp))
                        PillChip(
                            text = if (copiedFeedback) "✓ 已复制!" else "📋 复制译文",
                            bg = if (copiedFeedback) palette.pinBg else palette.pill,
                            fg = if (copiedFeedback) palette.pinFg else palette.text,
                            onClick = {
                                if (targetText.isNotBlank()) {
                                    clipboard.setText(AnnotatedString(targetText))
                                    copiedFeedback = true
                                } else {
                                    statusMessage = "还没有可复制的译文"
                                    isError = true
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PillChip(
    text: String,
    bg: Color,
    fg: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(9.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 9.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = fg, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun LanguageDropdown(
    selectedCode: String,
    options: List<LanguageOption>,
    palette: AppPalette,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val currentLabel = options.find { it.code == selectedCode }?.label ?: selectedCode

    Box {
        PillChip(
            text = "$currentLabel ▾",
            bg = palette.card,
            fg = palette.text,
            onClick = { expanded = true },
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(palette.card),
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label, color = palette.text, fontSize = 13.sp) },
                    onClick = {
                        expanded = false
                        onSelect(option.code)
                    },
                )
            }
        }
    }
}

@Composable
private fun ApiKeyDrawer(
    initialKey: String,
    palette: AppPalette,
    onSave: (String) -> Unit,
) {
    var keyDraft by remember(initialKey) { mutableStateOf(initialKey) }
    var showKey by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = palette.card,
        border = BorderStroke(1.dp, palette.border),
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "🔑 小牛翻译 API Key：",
                color = palette.text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(palette.bg)
                        .padding(horizontal = 8.dp, vertical = 7.dp),
                ) {
                    if (keyDraft.isEmpty()) {
                        Text("粘贴你的 NiuTrans apikey", color = palette.muted, fontSize = 12.sp)
                    }
                    BasicTextField(
                        value = keyDraft,
                        onValueChange = { keyDraft = it },
                        singleLine = true,
                        visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                        textStyle = TextStyle(color = palette.text, fontSize = 13.sp),
                        cursorBrush = SolidColor(palette.accent),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                PillChip(
                    text = "👁",
                    bg = palette.pill,
                    fg = palette.text,
                    onClick = { showKey = !showKey },
                )
                PillChip(
                    text = "保存",
                    bg = palette.accent,
                    fg = palette.accentFg,
                    onClick = { onSave(keyDraft) },
                )
            }
        }
    }
}
