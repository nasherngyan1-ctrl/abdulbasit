package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "portfolio_items")
data class PortfolioItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "IMAGE", "VIDEO", "WEB"
    val title: String,
    val urlOrPath: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "social_links")
data class SocialLinks(
    @PrimaryKey
    val id: Int = 1,
    val linkedin: String = "https://linkedin.com/in/abdulbasit-shwan",
    val instagram: String = "https://instagram.com/abdulbasit_shwan",
    val tiktok: String = "https://tiktok.com/@abdulbasit.shwan",
    val github: String = "https://github.com/abdulbasit-shwan",
    val whatsapp: String = "https://wa.me/9647500000000",
    val googleDrive: String = "https://drive.google.com"
)

@Entity(tableName = "radar_items")
data class RadarItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val toolName: String,
    val category: String,
    val whyFree: String,
    val directLink: String,
    val fullSummaryKurdish: String,
    val discoveredAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey
    val id: Int = 1,
    val customApiKey: String = "",
    val autoRadarEnabled: Boolean = false,
    val radarIntervalSeconds: Long = 60 // 30, 60, 300, 1800, 86400
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "USER" or "AI"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val generatedPdfUri: String? = null,
    val isPdfReady: Boolean = false,
    val isApiKeyPrompt: Boolean = false
)
