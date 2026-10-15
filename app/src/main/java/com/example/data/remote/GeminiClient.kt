package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    companion object {
        private const val TAG = "GeminiClient"
        private const val MODEL_NAME = "gemini-3.8-flash"
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

        const val SYSTEM_INSTRUCTION =
            "تۆ یاریدەدەری تایبەتی عەبدولباسیت شوانی. تاکە ئەرکی تۆ دۆزینەوە و چاودێریکردنی بەردەوامی نوێترین زیرەکییە دەستکردەکان (AI) و ئۆفەرە ١٠٠٪ خۆڕاییەکانە لە هەموو بوارەکانی: دروستکردنی وێنە، دیزاین، مۆنتاج و دروستکردنی ڤیدیۆ، دروستکردنی وێبسایت، و دروستکردنی ئەپڵیکەیشن. هەموو دەقەیەک و ڕۆژانە تەنها زانیاری لەسەر ئامرازە تازە و بێبەرامبەرەکان بدە بە زمانی کوردی سۆرانی بە شێوازێکی ڕوون لەگەڵ دانانی لینکی ڕاستەوخۆی سایتەکە."
    }

    suspend fun generateContent(
        prompt: String,
        apiKey: String,
        customSystemInstruction: String = SYSTEM_INSTRUCTION,
        enableSearch: Boolean = true
    ): Result<String> = withContext(Dispatchers.IO) {
        val resolvedKey = apiKey.trim().ifEmpty {
            try {
                BuildConfig.GEMINI_API_KEY
            } catch (e: Throwable) {
                ""
            }
        }.trim()

        if (!com.example.data.local.LocalStorageHelper.isUsableApiKey(resolvedKey)) {
            return@withContext Result.failure(
                IllegalStateException("تکایە سەرەتا کلیلی Gemini API داخڵ بکە لە سەرەوە (دوگمەی 🔑 کلیلی AI)")
            )
        }

        try {
            val rootJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        put("role", "user")
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                val sysInstructionObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        put(JSONObject().apply { put("text", customSystemInstruction) })
                    }
                    put("parts", partsArray)
                }
                put("systemInstruction", sysInstructionObj)

                if (enableSearch) {
                    val toolsArray = JSONArray().apply {
                        val googleSearchTool = JSONObject().apply {
                            put("googleSearch", JSONObject())
                        }
                        put(googleSearchTool)
                    }
                    put("tools", toolsArray)
                }
            }

            val url = "$BASE_URL/$MODEL_NAME:generateContent?key=$resolvedKey"
            val requestBody = rootJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                if (response.code == 429) {
                    Log.w(TAG, "Gemini search or rate quota reached (429).")
                    if (enableSearch) {
                        Log.i(TAG, "Retrying generation without search grounding...")
                        return@withContext generateContent(
                            prompt = prompt,
                            apiKey = apiKey,
                            customSystemInstruction = customSystemInstruction,
                            enableSearch = false
                        )
                    }
                    val friendlyMessage = "سنووری داواکارییە بێبەرامبەرەکانی گووگڵ (Quota Exceeded 429) لەم دەقەیەدا پڕبووە. سیستەم بەردەوام دەبێت بە ئۆفەرە دۆزراوەکانی تر."
                    return@withContext Result.failure(GeminiQuotaExceededException(friendlyMessage))
                }
                Log.w(TAG, "Gemini API response unsuccessful: code=${response.code}")
                val friendlyMessage = when (response.code) {
                    400, 401, 403 -> "کلیلی Gemini API نادروستە یان ماوەکەی بەسەرچووە. تکایە لە دوگمەی سەرەوە کلیلەکەت نوێ بکەرەوە."
                    else -> "پەیوەندی لەگەڵ گووگڵ سەرکەوتوو نەبوو (کۆدی هەڵە: ${response.code})."
                }
                return@withContext Result.failure(Exception(friendlyMessage))
            }

            val respJson = JSONObject(responseBody)
            val candidates = respJson.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext Result.failure(Exception("هیچ وەڵامێک نەگەڕایەوە."))
            }

            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content")
            val parts = content?.optJSONArray("parts")

            val textBuilder = StringBuilder()
            if (parts != null) {
                for (i in 0 until parts.length()) {
                    val part = parts.getJSONObject(i)
                    val t = part.optString("text")
                    if (t.isNotEmpty()) {
                        textBuilder.append(t)
                    }
                }
            }

            val finalResult = textBuilder.toString().trim()
            if (finalResult.isEmpty()) {
                Result.failure(Exception("وەڵامی بەتاڵ گەڕایەوە."))
            } else {
                Result.success(finalResult)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Exception calling Gemini API: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun discoverFreeAiTool(apiKey: String): Result<FreeAiToolResult> = withContext(Dispatchers.IO) {
        val queryPrompt = """
بگەڕێ لە گووگڵ بۆ نوێترین و بەهێزترین ئامرازی زیرەکی دەستکرد (AI) یان ئۆفەرێکی ١٠٠٪ بەخۆڕایی (100% Free / Open Source / Free Tier) لە بوارەکانی:
١. دروستکردنی وێنە (Image Generation)
٢. گرافیک دیزاین (Graphic Design)
٣. مۆنتاج و دروستکردنی ڤیدیۆ (Video Editing/Generation)
٤. دروستکردنی وێبسایت (Website Builder)
٥. دروستکردنی ئەپڵیکەیشن (App Development)

وەڵامەکەت بە شێوەیەکی ستاندارد و بە زمانی کوردی سۆرانی دابنێ بەم شێوازەی خوارەوە کە بتوانرێت لە ئەپڵیکەیشنەکەدا جیا بکرێتەوە:
TOOL_NAME: [ناوی ئامرازەکە]
CATEGORY: [پۆلێن: وێنە / دیزاین / ڤیدیۆ / وێبسایت / ئەپڵیکەیشن]
WHY_FREE: [بۆچی ١٠٠٪ خۆڕاییە و چۆن کاردەکات بە کورتی]
DIRECT_LINK: [لینکی ڕاستەوخۆی وێبسایتەکە https://...]
SUMMARY: [کورتەیەکی گشتی بە کوردی سۆرانی بۆ عەبدولباسیت شوان دەربارەی تایبەتمەندییە ناوازەکانی ئەم ئامرازە]
        """.trimIndent()

        val responseResult = generateContent(
            prompt = queryPrompt,
            apiKey = apiKey,
            customSystemInstruction = SYSTEM_INSTRUCTION,
            enableSearch = true
        )

        if (responseResult.isFailure) {
            Log.w(TAG, "Live Gemini search failed or hit quota. Providing verified free fallback.")
            val fallback = FallbackRadarTools.getNextFallbackTool()
            return@withContext Result.success(fallback)
        }

        responseResult.map { rawText ->
            parseFreeAiTool(rawText)
        }
    }

    private fun parseFreeAiTool(rawText: String): FreeAiToolResult {
        var toolName = "ئامرازی نوێی AI"
        var category = "دیزاین و تەکنەلۆژیا"
        var whyFree = "پلانێکی بەخۆڕایی یان سەرچاوەکراوە بەردەستە"
        var directLink = "https://google.com"
        var summary = rawText

        val lines = rawText.lines()
        for (line in lines) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("TOOL_NAME:", ignoreCase = true) -> {
                    toolName = trimmed.substringAfter(":").trim().removeSurrounding("[", "]")
                }
                trimmed.startsWith("CATEGORY:", ignoreCase = true) -> {
                    category = trimmed.substringAfter(":").trim().removeSurrounding("[", "]")
                }
                trimmed.startsWith("WHY_FREE:", ignoreCase = true) -> {
                    whyFree = trimmed.substringAfter(":").trim().removeSurrounding("[", "]")
                }
                trimmed.startsWith("DIRECT_LINK:", ignoreCase = true) -> {
                    val linkCandidate = trimmed.substringAfter(":").trim().removeSurrounding("[", "]")
                    val match = Regex("""https?://[^\s)\]]+""").find(linkCandidate)
                    if (match != null) {
                        directLink = match.value
                    }
                }
                trimmed.startsWith("SUMMARY:", ignoreCase = true) -> {
                    summary = trimmed.substringAfter(":").trim()
                }
            }
        }

        // Fallback: look for url in text if directLink is still fallback
        if (directLink == "https://google.com") {
            val urlRegex = Regex("""https?://[a-zA-Z0-9.\-_~:/?#\[\]@!$&'()*+,;=%]+""")
            val found = urlRegex.find(rawText)
            if (found != null) {
                directLink = found.value
            }
        }

        return FreeAiToolResult(
            toolName = toolName,
            category = category,
            whyFree = whyFree,
            directLink = directLink,
            fullSummary = summary.ifBlank { rawText }
        )
    }
}

data class FreeAiToolResult(
    val toolName: String,
    val category: String,
    val whyFree: String,
    val directLink: String,
    val fullSummary: String
)

class GeminiQuotaExceededException(message: String) : Exception(message)
