package com.niutrans.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val NIUTRANS_MAX_CHARS = 5000

@Serializable
data class LanguageOption(
    val label: String,
    val code: String,
)

val SUPPORTED_LANGUAGES: List<LanguageOption> = listOf(
    LanguageOption("自动检测", "auto"),
    LanguageOption("中文", "zh"),
    LanguageOption("英语", "en"),
    LanguageOption("日语", "ja"),
    LanguageOption("韩语", "ko"),
    LanguageOption("法语", "fr"),
    LanguageOption("德语", "de"),
    LanguageOption("西班牙语", "es"),
    LanguageOption("俄语", "ru"),
    LanguageOption("繁体中文", "cht"),
)

val TARGET_LANGUAGES: List<LanguageOption> = SUPPORTED_LANGUAGES.filter { it.code != "auto" }

@Serializable
data class NiuTransApiResponse(
    val from: String? = null,
    val to: String? = null,
    @SerialName("src_text") val srcText: String? = null,
    @SerialName("tgt_text") val tgtText: String? = null,
    @SerialName("error_code") val errorCode: String? = null,
    @SerialName("error_msg") val errorMsg: String? = null,
)

data class TranslationResult(
    val tgtText: String,
    val fromLang: String,
    val toLang: String,
)

data class AppConfig(
    val apiKey: String = "",
    val fromLang: String = "auto",
    val toLang: String = "zh",
    val isPinned: Boolean = true,
    val isDarkTheme: Boolean = false,
    val opacity: Float = 0.96f,
    val autoTranslate: Boolean = false,
)
