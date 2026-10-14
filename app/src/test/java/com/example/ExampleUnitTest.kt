package com.example

import com.example.data.local.LocalStorageHelper
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `isUsableApiKey correctly detects missing and placeholder keys`() {
        assertFalse(LocalStorageHelper.isUsableApiKey(null))
        assertFalse(LocalStorageHelper.isUsableApiKey(""))
        assertFalse(LocalStorageHelper.isUsableApiKey("   "))
        assertFalse(LocalStorageHelper.isUsableApiKey("MY_GEMINI_API_KEY"))
        assertFalse(LocalStorageHelper.isUsableApiKey("YOUR_API_KEY"))
        assertFalse(LocalStorageHelper.isUsableApiKey("placeholder"))
        assertFalse(LocalStorageHelper.isUsableApiKey("short"))

        // Genuine API key format test (starts with AIzaSy... or >= 10 chars)
        assertTrue(LocalStorageHelper.isUsableApiKey("AIzaSyB1234567890abcdef"))
    }
}
