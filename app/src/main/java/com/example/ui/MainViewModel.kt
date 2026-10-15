package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.local.AppDatabase
import com.example.data.model.AppSettings
import com.example.data.model.ChatMessage
import com.example.data.model.PortfolioItem
import com.example.data.model.RadarItem
import com.example.data.model.SocialLinks
import com.example.data.remote.GeminiClient
import com.example.notification.RadarNotificationHelper
import com.example.pdf.PortfolioPdfGenerator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val dao = db.portfolioDao()
    private val geminiClient = GeminiClient()

    val socialLinks: StateFlow<SocialLinks> = dao.getSocialLinks()
        .map { it ?: SocialLinks() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SocialLinks())

    val portfolioItems: StateFlow<List<PortfolioItem>> = dao.getAllPortfolioItems()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val radarItems: StateFlow<List<RadarItem>> = dao.getAllRadarItems()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val appSettings: StateFlow<AppSettings> = dao.getAppSettings()
        .map { it ?: AppSettings() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "AI",
                text = "سڵاو عەبدولباسیت! من یاریدەدەری تایبەتی زیرەکی دەستکردی تۆم. چاودێری نوێترین ئۆفەر و ئامرازە ١٠٠٪ بەخۆڕاییەکانی AI دەکەم بۆ دیزاین، وێبسایت، ڤیدیۆ و ئەپڵیکەیشن. هەروەها هەر کاتێک بنووسیت 'pdf'، ڕاستەوخۆ تەواوی کۆگای کارەکانت دەکەم بە یەک فایلی PDFی شاهانە!"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isSearchingRadar = MutableStateFlow(false)
    val isSearchingRadar: StateFlow<Boolean> = _isSearchingRadar.asStateFlow()

    private val _isGeneratingPdf = MutableStateFlow(false)
    val isGeneratingPdf: StateFlow<Boolean> = _isGeneratingPdf.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    private val _lastGeneratedPdf = MutableStateFlow<File?>(null)
    val lastGeneratedPdf: StateFlow<File?> = _lastGeneratedPdf.asStateFlow()

    private val _apiKeyModalVisible = MutableStateFlow(false)
    val apiKeyModalVisible: StateFlow<Boolean> = _apiKeyModalVisible.asStateFlow()

    private val _editLinksModalVisible = MutableStateFlow(false)
    val editLinksModalVisible: StateFlow<Boolean> = _editLinksModalVisible.asStateFlow()

    private val _addProjectModalType = MutableStateFlow<String?>(null)
    val addProjectModalType: StateFlow<String?> = _addProjectModalType.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private var autoRadarJob: Job? = null

    init {
        RadarNotificationHelper.createNotificationChannel(application)
        seedInitialDataIfEmpty()
        observeAutoRadarSetting()
    }

    private fun seedInitialDataIfEmpty() {
        viewModelScope.launch {
            val existingLinks = dao.getSocialLinksSync()
            if (existingLinks == null) {
                dao.saveSocialLinks(SocialLinks())
            }

            val localKey = com.example.data.local.LocalStorageHelper.getSavedLocalStorageKey(getApplication())
            val existingSettings = dao.getAppSettingsSync()
            if (existingSettings == null) {
                dao.saveAppSettings(AppSettings(customApiKey = localKey))
            } else if (existingSettings.customApiKey.isBlank() && localKey.isNotBlank()) {
                dao.saveAppSettings(existingSettings.copy(customApiKey = localKey))
            } else if (existingSettings.customApiKey.isNotBlank() && localKey.isBlank()) {
                com.example.data.local.LocalStorageHelper.saveApiKey(getApplication(), existingSettings.customApiKey)
            }

            val existingItems = dao.getAllPortfolioItemsSync()
            if (existingItems.isEmpty()) {
                // Seed initial creative items representing Abdulbasit Shwan's works
                dao.insertPortfolioItem(
                    PortfolioItem(
                        type = "WEB",
                        title = "Erbil Creative Studio Platform",
                        urlOrPath = "https://github.com/abdulbasit-shwan/erbil-creative",
                        description = "Next-gen web application for creative digital design & motion showcase"
                    )
                )
                dao.insertPortfolioItem(
                    PortfolioItem(
                        type = "WEB",
                        title = "Kurdish AI Assistant Portal",
                        urlOrPath = "https://github.com/abdulbasit-shwan/kurdish-ai-hub",
                        description = "Sorani Kurdish conversational interface and tool suite"
                    )
                )
                dao.insertPortfolioItem(
                    PortfolioItem(
                        type = "VIDEO",
                        title = "Showreel 2026 — Motion & VFX",
                        urlOrPath = "https://tiktok.com/@abdulbasit.shwan",
                        description = "Cinematic video editing and 3D motion graphics showreel"
                    )
                )
            }
        }
    }

    private fun observeAutoRadarSetting() {
        viewModelScope.launch {
            appSettings.collect { settings ->
                restartAutoRadar(settings.autoRadarEnabled, settings.radarIntervalSeconds)
            }
        }
    }

    fun getEffectiveApiKey(): String {
        return com.example.data.local.LocalStorageHelper.getEffectiveApiKey(
            getApplication(),
            appSettings.value.customApiKey
        )
    }

    val hasActiveApiKey: Boolean
        get() = com.example.data.local.LocalStorageHelper.isUsableApiKey(getEffectiveApiKey())

    fun showApiKeyModal(show: Boolean) {
        _apiKeyModalVisible.value = show
    }

    fun showEditLinksModal(show: Boolean) {
        _editLinksModalVisible.value = show
    }

    fun showAddProjectModal(type: String?) {
        _addProjectModalType.value = type
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun updateApiKey(newKey: String) {
        viewModelScope.launch {
            val trimmed = newKey.trim()
            if (trimmed.isEmpty()) {
                com.example.data.local.LocalStorageHelper.clearApiKey(getApplication())
            } else {
                com.example.data.local.LocalStorageHelper.saveApiKey(getApplication(), trimmed)
            }
            val current = appSettings.value
            dao.saveAppSettings(current.copy(customApiKey = trimmed))
            _apiKeyModalVisible.value = false
            _snackbarMessage.value = if (trimmed.isNotBlank()) "کلیلی API بە سەرکەوتوویی لە LocalStorage خەزنکرا!" else "کلیلی API سڕایەوە."
        }
    }

    fun updateSocialLinks(links: SocialLinks) {
        viewModelScope.launch {
            dao.saveSocialLinks(links)
            _editLinksModalVisible.value = false
            _snackbarMessage.value = "لینکەکان بە سەرکەوتوویی نوێکرانەوە!"
        }
    }

    fun setAutoRadarEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val current = appSettings.value
            dao.saveAppSettings(current.copy(autoRadarEnabled = enabled))
            _snackbarMessage.value = if (enabled) "ڕاداری ئۆتۆماتیکی چالاک کرا 🔔" else "ڕاداری ئۆتۆماتیکی ناچالاک کرا"
        }
    }

    fun setRadarInterval(seconds: Long) {
        viewModelScope.launch {
            val current = appSettings.value
            dao.saveAppSettings(current.copy(radarIntervalSeconds = seconds))
            _snackbarMessage.value = "ماوەی ڕادار گۆڕدرا بۆ: $seconds چرکە"
        }
    }

    private fun restartAutoRadar(enabled: Boolean, intervalSeconds: Long) {
        autoRadarJob?.cancel()
        if (!enabled) return

        autoRadarJob = viewModelScope.launch {
            Log.d("MainViewModel", "Auto Radar loop started with interval: $intervalSeconds sec")
            while (isActive) {
                delay(intervalSeconds * 1000L)
                performRadarScan(isAutomated = true)
            }
        }
    }

    fun performRadarScan(isAutomated: Boolean = false) {
        if (_isSearchingRadar.value) return

        viewModelScope.launch {
            val key = getEffectiveApiKey()
            if (!com.example.data.local.LocalStorageHelper.isUsableApiKey(key)) {
                if (!isAutomated) {
                    _snackbarMessage.value = "تکایە سەرەتا کلیلی API دابنێ (🔑 کلیلی AI)"
                    _apiKeyModalVisible.value = true
                }
                return@launch
            }

            _isSearchingRadar.value = true
            val result = geminiClient.discoverFreeAiTool(key)
            _isSearchingRadar.value = false

            result.onSuccess { tool ->
                val newItem = RadarItem(
                    toolName = tool.toolName,
                    category = tool.category,
                    whyFree = tool.whyFree,
                    directLink = tool.directLink,
                    fullSummaryKurdish = tool.fullSummary
                )
                dao.insertRadarItem(newItem)

                // Trigger mobile notification & sound
                RadarNotificationHelper.showRadarNotification(
                    context = getApplication(),
                    toolName = tool.toolName,
                    category = tool.category,
                    whyFree = tool.whyFree
                )

                _snackbarMessage.value = "✨ ئۆفەری نوێی AI دۆزرایەوە: ${tool.toolName}"
            }.onFailure { err ->
                Log.w("MainViewModel", "Radar scan fallback activated: ${err.message}")
                val fallbackTool = com.example.data.remote.FallbackRadarTools.getNextFallbackTool()
                val newItem = RadarItem(
                    toolName = fallbackTool.toolName,
                    category = fallbackTool.category,
                    whyFree = fallbackTool.whyFree,
                    directLink = fallbackTool.directLink,
                    fullSummaryKurdish = fallbackTool.fullSummary
                )
                dao.insertRadarItem(newItem)
                _snackbarMessage.value = "✨ ئۆفەری نوێی AI دۆزرایەوە: ${fallbackTool.toolName}"
            }
        }
    }

    fun deleteRadarItem(id: Long) {
        viewModelScope.launch {
            dao.deleteRadarItem(id)
            _snackbarMessage.value = "ئەم ئامرازە لە لیستەکە سڕایەوە."
        }
    }

    fun clearAllRadar() {
        viewModelScope.launch {
            dao.clearAllRadarItems()
            _snackbarMessage.value = "تەواوی مێژووی ڕادار سڕایەوە."
        }
    }

    // --- Portfolio Management ---

    fun addImageItem(context: Context, uris: List<Uri>, title: String) {
        viewModelScope.launch {
            try {
                val imagesDir = File(context.filesDir, "portfolio_images").apply {
                    if (!exists()) mkdirs()
                }

                for ((index, uri) in uris.withIndex()) {
                    val fileName = "img_${System.currentTimeMillis()}_${index}.jpg"
                    val destFile = File(imagesDir, fileName)

                    context.contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(destFile).use { output ->
                            input.copyTo(output)
                        }
                    }

                    val finalTitle = if (uris.size > 1) {
                        "${title.ifBlank { "دیزاینی عەبدولباسیت شوان" }} #${index + 1}"
                    } else {
                        title.ifBlank { "دیزاینی عەبدولباسیت شوان" }
                    }

                    dao.insertPortfolioItem(
                        PortfolioItem(
                            type = "IMAGE",
                            title = finalTitle,
                            urlOrPath = destFile.absolutePath,
                            description = "دیزاینی گرافیکی فەرمی لە کۆگای عەبدولباسیت شوان"
                        )
                    )
                }
                _addProjectModalType.value = null
                _snackbarMessage.value = "وێنەکان بە سەرکەوتوویی لە کۆگا خەزنکران! 📸"
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to save portfolio images", e)
                _snackbarMessage.value = "هەڵە لە خەزنکردنی وێنە: ${e.message}"
            }
        }
    }

    fun addLinkItem(type: String, title: String, url: String, description: String) {
        if (title.isBlank() || url.isBlank()) {
            _snackbarMessage.value = "تکایە ناونیشان و لینکی پڕۆژەکە پڕبکەرەوە."
            return
        }

        viewModelScope.launch {
            dao.insertPortfolioItem(
                PortfolioItem(
                    type = type,
                    title = title.trim(),
                    urlOrPath = url.trim(),
                    description = description.trim()
                )
            )
            _addProjectModalType.value = null
            _snackbarMessage.value = "پڕۆژەکە بە سەرکەوتوویی زیادکرا! 🚀"
        }
    }

    fun deletePortfolioItem(item: PortfolioItem) {
        viewModelScope.launch {
            if (item.type == "IMAGE") {
                try {
                    val file = File(item.urlOrPath)
                    if (file.exists()) file.delete()
                } catch (e: Exception) {
                    Log.w("MainViewModel", "Could not delete image file", e)
                }
            }
            dao.deletePortfolioItem(item)
            _snackbarMessage.value = "بڕگەکە لە کۆگای کارەکان سڕایەوە."
        }
    }

    fun compileAndGeneratePdf(context: Context, onComplete: ((File) -> Unit)? = null) {
        if (_isGeneratingPdf.value) return

        viewModelScope.launch {
            _isGeneratingPdf.value = true
            val items = dao.getAllPortfolioItemsSync()
            val links = dao.getSocialLinksSync() ?: SocialLinks()

            val result = PortfolioPdfGenerator.generatePortfolioPDF(context, links, items)
            _isGeneratingPdf.value = false

            result.onSuccess { pdfFile ->
                _lastGeneratedPdf.value = pdfFile
                _snackbarMessage.value = "فایلی PDF بە سەرکەوتوویی ئامادەکرا! 📄"
                onComplete?.invoke(pdfFile)
            }.onFailure { err ->
                Log.e("MainViewModel", "PDF Generation failed", err)
                _snackbarMessage.value = "هەڵە لە دروستکردنی فایلی PDF: ${err.message}"
            }
        }
    }

    // --- Smart Chat System ---

    fun sendChatMessage(context: Context, userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isBlank() || _isChatLoading.value) return

        val userMsg = ChatMessage(sender = "USER", text = trimmed)
        _chatMessages.value = _chatMessages.value + userMsg

        // Check if user is asking for PDF
        val isPdfRequest = isPdfTrigger(trimmed)

        if (isPdfRequest) {
            handlePdfChatCommand(context)
            return
        }

        // Standard Gemini conversation
        viewModelScope.launch {
            val key = getEffectiveApiKey()
            if (!com.example.data.local.LocalStorageHelper.isUsableApiKey(key)) {
                val errorMsg = ChatMessage(
                    sender = "AI",
                    text = "سڵاو عەبدولباسیت! بۆ ئەوەی بتوانم بە نوێترین زانیاری لەسەر ئامرازەکانی AI وەڵامت بدەمەوە، پێویستە کلیلی Gemini API داخڵ بکەیت. دەتوانیت لێرەوە یان لە ڕێگەی دوگمەی سەرەوە کلیلی خۆت دابنێیت.",
                    isApiKeyPrompt = true
                )
                _chatMessages.value = _chatMessages.value + errorMsg
                return@launch
            }

            _isChatLoading.value = true
            val result = geminiClient.generateContent(
                prompt = trimmed,
                apiKey = key,
                customSystemInstruction = GeminiClient.SYSTEM_INSTRUCTION,
                enableSearch = true
            )
            _isChatLoading.value = false

            result.onSuccess { answer ->
                val aiMsg = ChatMessage(sender = "AI", text = answer)
                _chatMessages.value = _chatMessages.value + aiMsg
            }.onFailure { err ->
                val errorText = if (err is com.example.data.remote.GeminiQuotaExceededException) {
                    "سنووری کاتیی داواکارییەکانی Google Gemini پڕبووە (429 Rate Limit). تکایە چەند خولەکێک چاوەڕێ بکە یان لە ڕێگەی دوگمەی خوارەوە کلیلێکی تر داخڵ بکە."
                } else {
                    err.message ?: "کێشەیەک لە پەیوەندی زیرەکی دەستکرد ڕوویدا."
                }
                val errorMsg = ChatMessage(
                    sender = "AI",
                    text = errorText,
                    isApiKeyPrompt = err is com.example.data.remote.GeminiQuotaExceededException
                )
                _chatMessages.value = _chatMessages.value + errorMsg
            }
        }
    }

    private fun isPdfTrigger(text: String): Boolean {
        val lower = text.lowercase()
        return lower.contains("pdf") ||
                lower.contains("فایلی pdf") ||
                lower.contains("نموونەی کارەکانم") ||
                lower.contains("پۆرتفۆلیۆ") ||
                lower.contains("داگرتنی کارەکان") ||
                lower.contains("portfolio")
    }

    private fun handlePdfChatCommand(context: Context) {
        viewModelScope.launch {
            _isChatLoading.value = true
            val items = dao.getAllPortfolioItemsSync()
            val links = dao.getSocialLinksSync() ?: SocialLinks()

            val result = PortfolioPdfGenerator.generatePortfolioPDF(context, links, items)
            _isChatLoading.value = false

            result.onSuccess { pdfFile ->
                _lastGeneratedPdf.value = pdfFile
                val aiReply = ChatMessage(
                    sender = "AI",
                    text = "فایلی PDFی پۆرتفۆلیۆی تایبەتی عەبدولباسیت شوان (${PortfolioPdfGenerator.PDF_FILE_NAME}) بە تەواوی ئامادەکرا! هەموو کارە گرافیکییەکان، بەستەری پڕۆژەی وێب و ڤیدیۆکانت لە خۆ دەگرێت. دەتوانیت ڕاستەوخۆ لێرەوە بیکەیتەوە یان بۆ کڕیارەکانتی بنێریت:",
                    generatedPdfUri = pdfFile.absolutePath,
                    isPdfReady = true
                )
                _chatMessages.value = _chatMessages.value + aiReply
            }.onFailure { err ->
                val errorMsg = ChatMessage(
                    sender = "AI",
                    text = "ببورە کێشەیەک لە دروستکردنی فایلی PDF ڕوویدا: ${err.message}"
                )
                _chatMessages.value = _chatMessages.value + errorMsg
            }
        }
    }
}
