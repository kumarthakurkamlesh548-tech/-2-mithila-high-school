package com.example.data.repository

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

data class GeminiMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: String, // "user" or "model"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class GeminiRole(
    val displayName: String,
    val modelName: String,
    val description: String,
    val systemInstruction: String
) {
    GENERAL_SUPPORT(
        displayName = "Mithila School Support Agent",
        modelName = "gemini-3.5-flash",
        description = "Support agent that remembers conversation context for troubleshooting, admissions & guidelines.",
        systemInstruction = "You are the friendly, empathetic AI Support Agent for Mithila Higher Secondary School. Your job is to assist students, parents, and teachers with school admissions, timetable queries, exam results, homework guidelines, fee structures, and general troubleshooting. Remember conversation history across turns and assist multi-step requests."
    ),
    ACADEMIC_TUTOR(
        displayName = "Advanced Academic Tutor",
        modelName = "gemini-3.1-pro-preview",
        description = "Powered by Gemini 3.1 Pro for complex reasoning, math problems & academic tutoring.",
        systemInstruction = "You are an expert, encouraging Academic Tutor at Mithila Higher Secondary School specializing in Mathematics, Physics, Chemistry, Biology, English, and Computer Science. Provide clear step-by-step explanations, solve complex academic doubts, and guide students through multi-step learning."
    ),
    QUICK_ASSISTANT(
        displayName = "Instant Quick Help",
        modelName = "gemini-3.1-flash-lite-preview",
        description = "Ultra-fast response model for quick FAQs, office timings & campus contacts.",
        systemInstruction = "You are a concise, ultra-fast School FAQ assistant for Mithila School. Provide instant, direct answers to quick questions regarding Mithila School facilities, office hours, campus locations, and emergency contacts."
    )
}

class GeminiRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val apiKey: String
        get() = try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isNullOrBlank() || key == "MY_GEMINI_API_KEY") "" else key
        } catch (e: Exception) {
            ""
        }

    suspend fun generateMultiTurnResponse(
        role: GeminiRole,
        conversationHistory: List<GeminiMessage>
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val key = apiKey
            if (key.isBlank()) {
                return@withContext Result.failure(
                    IllegalStateException("Gemini API key is missing. Please configure GEMINI_API_KEY in Secrets.")
                )
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/${role.modelName}:generateContent?key=$key"

            // System Instruction
            val systemInstructionObj = JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", role.systemInstruction)))
            }

            // Conversation History contents
            val contentsArray = JSONArray()
            for (msg in conversationHistory) {
                val contentObj = JSONObject().apply {
                    put("role", msg.role)
                    put("parts", JSONArray().put(JSONObject().put("text", msg.text)))
                }
                contentsArray.put(contentObj)
            }

            val requestJson = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", systemInstructionObj)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e("GeminiRepository", "Error from Gemini API: ${response.code} $responseString")
                return@withContext Result.failure(
                    Exception("Gemini API error (${response.code})")
                )
            }

            val jsonResponse = JSONObject(responseString)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text", "")
                    if (text.isNotBlank()) {
                        return@withContext Result.success(text)
                    }
                }
            }

            Result.failure(Exception("Received empty response from AI model."))
        } catch (e: Exception) {
            Log.e("GeminiRepository", "Exception calling Gemini API", e)
            Result.failure(e)
        }
    }
}
