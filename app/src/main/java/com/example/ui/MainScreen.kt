package com.example.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AddProjectModal
import com.example.ui.components.ApiKeyModal
import com.example.ui.components.EditLinksModal
import com.example.ui.components.PermanentFooter
import com.example.ui.components.ProfileHeader
import com.example.ui.tabs.ChatAssistantTab
import com.example.ui.tabs.PortfolioVaultTab
import com.example.ui.tabs.RadarTab
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MatteBlack
import com.example.ui.theme.MatteBlackElevated
import com.example.ui.theme.MatteBlackSurface
import com.example.ui.theme.TextMuted

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val socialLinks by viewModel.socialLinks.collectAsState()
    val portfolioItems by viewModel.portfolioItems.collectAsState()
    val radarItems by viewModel.radarItems.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isSearchingRadar by viewModel.isSearchingRadar.collectAsState()
    val isGeneratingPdf by viewModel.isGeneratingPdf.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    val lastGeneratedPdf by viewModel.lastGeneratedPdf.collectAsState()
    val apiKeyModalVisible by viewModel.apiKeyModalVisible.collectAsState()
    val editLinksModalVisible by viewModel.editLinksModalVisible.collectAsState()
    val addProjectModalType by viewModel.addProjectModalType.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Request notification permission for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    // Full RTL layout support for Kurdish Sorani
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MatteBlack,
            bottomBar = {
                PermanentFooter()
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MatteBlack)
            ) {
                // Top Branding & Profile Header
                ProfileHeader(
                    socialLinks = socialLinks,
                    hasActiveKey = viewModel.hasActiveApiKey,
                    onApiKeyClick = { viewModel.showApiKeyModal(true) },
                    onEditLinksClick = { viewModel.showEditLinksModal(true) }
                )

                // Tab Row: Radar, Portfolio, Chat
                val tabTitles = listOf(
                    "📡 ڕاداری AI" to Icons.Default.Radar,
                    "📁 کۆگای پۆرتفۆلیۆ" to Icons.Default.Folder,
                    "💬 چاتی زیرەک" to Icons.AutoMirrored.Filled.Chat
                )

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MatteBlackSurface,
                    contentColor = GoldPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = GoldPrimary,
                            height = 3.dp
                        )
                    },
                    divider = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DarkCardBorder)
                                .padding(vertical = 0.5.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabTitles.forEachIndexed { index, (title, icon) ->
                        val selected = selectedTabIndex == index
                        Tab(
                            selected = selected,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.5.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selected) GoldLight else TextMuted
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (selected) GoldPrimary else TextMuted,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            },
                            modifier = Modifier.testTag("main_tab_$index")
                        )
                    }
                }

                // Tab Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedTabIndex) {
                        0 -> RadarTab(
                            settings = appSettings,
                            radarItems = radarItems,
                            isScanning = isSearchingRadar,
                            onToggleAutoRadar = { viewModel.setAutoRadarEnabled(it) },
                            onSelectInterval = { viewModel.setRadarInterval(it) },
                            onManualScan = { viewModel.performRadarScan(isAutomated = false) },
                            onDeleteItem = { viewModel.deleteRadarItem(it) }
                        )
                        1 -> PortfolioVaultTab(
                            portfolioItems = portfolioItems,
                            isGeneratingPdf = isGeneratingPdf,
                            lastGeneratedPdf = lastGeneratedPdf,
                            onOpenAddModal = { viewModel.showAddProjectModal(it) },
                            onDeleteItem = { viewModel.deletePortfolioItem(it) },
                            onGeneratePdf = { viewModel.compileAndGeneratePdf(context) }
                        )
                        2 -> ChatAssistantTab(
                            messages = chatMessages,
                            isLoading = isChatLoading,
                            onSendMessage = { prompt -> viewModel.sendChatMessage(context, prompt) },
                            onOpenApiKeyModal = { viewModel.showApiKeyModal(true) }
                        )
                    }
                }
            }

            // Modals
            if (apiKeyModalVisible) {
                ApiKeyModal(
                    currentCustomKey = appSettings.customApiKey,
                    hasActiveKey = viewModel.hasActiveApiKey,
                    onSaveKey = { viewModel.updateApiKey(it) },
                    onDismiss = { viewModel.showApiKeyModal(false) }
                )
            }

            if (editLinksModalVisible) {
                EditLinksModal(
                    currentLinks = socialLinks,
                    onSaveLinks = { viewModel.updateSocialLinks(it) },
                    onDismiss = { viewModel.showEditLinksModal(false) }
                )
            }

            addProjectModalType?.let { type ->
                AddProjectModal(
                    type = type,
                    onAddImage = { uris, title -> viewModel.addImageItem(context, uris, title) },
                    onAddLink = { pType, title, url, desc -> viewModel.addLinkItem(pType, title, url, desc) },
                    onDismiss = { viewModel.showAddProjectModal(null) }
                )
            }
        }
    }
}
