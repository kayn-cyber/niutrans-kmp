package com.niutrans.app.network

import com.niutrans.app.model.NIUTRANS_MAX_CHARS
import com.niutrans.app.model.NiuTransApiResponse
import com.niutrans.app.model.TranslationResult
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import kotlinx.serialization.json.Json

private const val API_URL = "https://api.niutrans.com/NiuTransServer/translation"

class TranslationException(
    message: String,
    val errorCode: String? = null,
) : Exception(message)

class NiuTransClient(
    private val httpClient: HttpClient = createDefaultHttpClient(),
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun translate(
        text: String,
        apiKey: String,
        fromLang: String = "auto",
        toLang: String = "zh",
    ): TranslationResult {
        val cleaned = text.trim()
        if (cleaned.isEmpty()) {
            throw TranslationException("先输入或粘贴点内容吧")
        }
        val cleanKey = apiKey.trim()
        if (cleanKey.isEmpty()) {
            throw TranslationException("请先配置小牛翻译 API Key")
        }
        if (cleaned.length > NIUTRANS_MAX_CHARS) {
            throw TranslationException("单次最多支持 $NIUTRANS_MAX_CHARS 字符（当前 ${cleaned.length}）")
        }

        val rawResponse = try {
            httpClient.post(API_URL) {
                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("apikey", cleanKey)
                            append("src_text", cleaned)
                            append("from", fromLang)
                            append("to", toLang)
                        }
                    )
                )
            }.bodyAsText()
        } catch (e: Exception) {
            throw TranslationException("网络请求失败: ${e.message ?: "请检查网络连接"}")
        }

        return parseResponse(rawResponse, fromLang, toLang)
    }

    internal fun parseResponse(
        rawJson: String,
        fallbackFrom: String,
        fallbackTo: String,
    ): TranslationResult {
        val parsed = try {
            json.decodeFromString<NiuTransApiResponse>(rawJson)
        } catch (e: Exception) {
            throw TranslationException("接口返回了无效数据")
        }

        if (!parsed.errorCode.isNullOrBlank()) {
            val msg = parsed.errorMsg ?: "未知错误"
            throw TranslationException("翻译失败 [${parsed.errorCode}]: $msg", parsed.errorCode)
        }

        val target = parsed.tgtText
            ?: throw TranslationException("接口未返回译文 (tgt_text)")

        return TranslationResult(
            tgtText = target,
            fromLang = parsed.from ?: fallbackFrom,
            toLang = parsed.to ?: fallbackTo,
        )
    }

    companion object {
        fun createDefaultHttpClient(): HttpClient = HttpClient()
    }
}
