package com.niutrans.app

import com.niutrans.app.network.NiuTransClient
import com.niutrans.app.network.TranslationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NiuTransClientTest {
    private val client = NiuTransClient()

    @Test
    fun parseSuccessResponse() {
        val raw = """{"from":"en","to":"zh","tgt_text":"你好世界"}"""
        val res = client.parseResponse(raw, "auto", "zh")
        assertEquals("你好世界", res.tgtText)
        assertEquals("en", res.fromLang)
        assertEquals("zh", res.toLang)
    }

    @Test
    fun parseErrorCodeResponse() {
        val raw = """{"error_code":"13001","error_msg":"apikey invalid"}"""
        val err = assertFailsWith<TranslationException> {
            client.parseResponse(raw, "auto", "zh")
        }
        assertEquals("13001", err.errorCode)
    }
}
