package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.BuildConfig

/**
 * LocalStorageHelper provides Android LocalStorage (SharedPreferences) management
 * with seamless fallback to BuildConfig.GEMINI_API_KEY.
 *
 * It gracefully handles:
 * - Empty keys
 * - Placeholder keys (e.g. "MY_GEMINI_API_KEY", "YOUR_API_KEY")
 * - Key clearance and persistence
 */
object LocalStorageHelper {

    private const val PREFS_NAME = "abdulbasit_local_storage"
    private const val KEY_GEMINI_API_KEY = "gemini_api_key"
    private const val KEY_AUTO_RADAR_ENABLED = "auto_radar_enabled"
    private const val KEY_RADAR_INTERVAL = "radar_interval_seconds"
    private const val TAG = "LocalStorageHelper"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Determines if a given key string is a genuine API key rather than a template placeholder.
     */
    fun isUsableApiKey(key: String?): Boolean {
        if (key.isNullOrBlank()) return false
        val trimmed = key.trim()
        val placeholders = listOf(
            "MY_GEMINI_API_KEY",
            "YOUR_GEMINI_API_KEY",
            "YOUR_API_KEY",
            "INSERT_KEY_HERE",
            "PLACEHOLDER",
            "null",
            "undefined"
        )
        if (placeholders.any { trimmed.equals(it, ignoreCase = true) }) {
            return false
        }
        return trimmed.length >= 10
    }

    /**
     * Resolves the effective API key following the strict priority order:
     * 1. In-App User Saved Key in LocalStorage (SharedPreferences)
     * 2. In-App Room Database setting
     * 3. System BuildConfig.GEMINI_API_KEY
     *
     * Returns empty string if no valid key is configured, avoiding invalid API calls.
     */
    fun getEffectiveApiKey(context: Context, dbCustomKey: String? = null): String {
        // 1. Check LocalStorage (SharedPreferences)
        val localKey = getPrefs(context).getString(KEY_GEMINI_API_KEY, "")?.trim().orEmpty()
        if (isUsableApiKey(localKey)) {
            return localKey
        }

        // 2. Check DB custom key passed from ViewModel
        if (!dbCustomKey.isNullOrBlank() && isUsableApiKey(dbCustomKey)) {
            return dbCustomKey.trim()
        }

        // 3. Fallback to BuildConfig.GEMINI_API_KEY
        val buildKey = try {
            BuildConfig.GEMINI_API_KEY.trim()
        } catch (e: Throwable) {
            ""
        }

        if (isUsableApiKey(buildKey)) {
            return buildKey
        }

        return ""
    }

    /**
     * Saves the user-supplied Gemini API key to LocalStorage.
     */
    fun saveApiKey(context: Context, apiKey: String) {
        val trimmed = apiKey.trim()
        getPrefs(context).edit().putString(KEY_GEMINI_API_KEY, trimmed).apply()
        Log.i(TAG, "Gemini API key updated in LocalStorage. Usable: ${isUsableApiKey(trimmed)}")
    }

    /**
     * Clears the API key from LocalStorage.
     */
    fun clearApiKey(context: Context) {
        getPrefs(context).edit().remove(KEY_GEMINI_API_KEY).apply()
        Log.i(TAG, "Gemini API key cleared from LocalStorage.")
    }

    /**
     * Retrieves the raw saved key in LocalStorage (for display in editing fields).
     */
    fun getSavedLocalStorageKey(context: Context): String {
        return getPrefs(context).getString(KEY_GEMINI_API_KEY, "")?.trim().orEmpty()
    }
}
