package com.example.ui.tabs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.data.model.RadarItem
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMuted
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MatteBlack
import com.example.ui.theme.MatteBlackElevated
import com.example.ui.theme.MatteBlackSurface
import com.example.ui.theme.StatusActiveGreen
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RadarTab(
    settings: AppSettings,
    radarItems: List<RadarItem>,
    isScanning: Boolean,
    onToggleAutoRadar: (Boolean) -> Unit,
    onSelectInterval: (Long) -> Unit,
    onManualScan: () -> Unit,
    onDeleteItem: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MatteBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Master Control Card
        item {
            RadarControlCard(
                settings = settings,
                isScanning = isScanning,
                onToggleAutoRadar = onToggleAutoRadar,
                onSelectInterval = onSelectInterval,
                onManualScan = onManualScan
            )
        }

        // Section Title: Live Radar Feed
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (settings.autoRadarEnabled) StatusActiveGreen else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "📡 نوێترین دۆزینەوەکانی AI (Live Free AI Feed)",
                        color = GoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Text(
                    text = "${radarItems.size} ئۆفەر",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        if (radarItems.isEmpty() && !isScanning) {
            item {
                RadarEmptyState(onManualScan = onManualScan)
            }
        }

        // Live Discovered Cards
        items(radarItems, key = { it.id }) { item ->
            RadarToolCard(
                item = item,
                onOpenLink = { url -> openWebLink(context, url) },
                onDelete = { onDeleteItem(item.id) }
            )
        }
    }
}

@Composable
private fun RadarControlCard(
    settings: AppSettings,
    isScanning: Boolean,
    onToggleAutoRadar: (Boolean) -> Unit,
    onSelectInterval: (Long) -> Unit,
    onManualScan: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                Brush.linearGradient(listOf(GoldPrimary.copy(alpha = 0.6f), DarkCardBorder)),
                RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MatteBlackSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Master Toggle Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (settings.autoRadarEnabled) GoldPrimary.copy(alpha = 0.2f) else MatteBlackElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = if (settings.autoRadarEnabled) GoldPrimary else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "🔔 ڕاداری ئۆتۆماتیکی و نۆتیفیکەیشنی بەردەوام",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )
                        Text(
                            text = "Auto AI Radar & Background Alerts",
                            color = GoldLight.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }

                Switch(
                    checked = settings.autoRadarEnabled,
                    onCheckedChange = onToggleAutoRadar,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MatteBlack,
                        checkedTrackColor = GoldPrimary,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = MatteBlackElevated
                    ),
                    modifier = Modifier.testTag("auto_radar_toggle")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interval Selector Label
            Text(
                text = "ماوەی دووبارەبوونەوەی پشکنین (Scan Interval):",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Interval Options Row
            val intervals = listOf(
                30L to "30 چرکە",
                60L to "1 خولەک",
                300L to "5 خولەک",
                1800L to "30 خولەک",
                86400L to "ڕۆژانە"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                intervals.forEach { (sec, label) ->
                    val isSelected = settings.radarIntervalSeconds == sec
                    Surface(
                        onClick = { onSelectInterval(sec) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) GoldPrimary else MatteBlackElevated,
                        border = androidx.compose.foundation.BorderStroke(
                            0.7.dp,
                            if (isSelected) GoldLight else DarkCardBorder
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) MatteBlack else TextWhite,
                            fontSize = 10.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(vertical = 7.dp, horizontal = 2.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Manual Search Button
            Button(
                onClick = onManualScan,
                enabled = !isScanning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = MatteBlack
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("manual_radar_scan_btn")
            ) {
                if (isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MatteBlack,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "دەگەڕێت بە گووگڵ بۆ نوێترین AI...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MatteBlack,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "⚡ ئێستا بگەڕێ بۆ نوێترین ئۆفەری خۆڕایی AI",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RadarToolCard(
    item: RadarItem,
    onOpenLink: (String) -> Unit,
    onDelete: () -> Unit
) {
    val formattedDate = SimpleDateFormat("HH:mm - yyyy/MM/dd", Locale.getDefault()).format(Date(item.discoveredAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MatteBlackSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Category Badge + Tool Name + Delete Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GoldPrimary.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(0.6.dp, GoldPrimary)
                    ) {
                        Text(
                            text = item.category,
                            color = GoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.toolName,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "سڕینەوە",
                        tint = TextDim,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Why 100% Free Box
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MatteBlackElevated,
                border = androidx.compose.foundation.BorderStroke(0.6.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "🎁 بۆچی خۆڕاییە:",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.whyFree,
                        color = TextWhite,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Summary text
            Text(
                text = item.fullSummaryKurdish,
                color = TextMuted,
                fontSize = 12.5.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Action Row: Direct Link Button + Discovered Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onOpenLink(item.directLink) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary.copy(alpha = 0.2f),
                        contentColor = GoldLight
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, GoldPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("open_radar_link_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "سەردانی ماڵپەڕی سەرەکی (Direct Link)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                }

                Text(
                    text = formattedDate,
                    color = TextDim,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun RadarEmptyState(onManualScan: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MatteBlackSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = GoldMuted,
                modifier = Modifier.size(42.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "ڕاداری AI ئامادەیە بۆ دۆزینەوە!",
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "تکایە دوگمەی '⚡ ئێستا بگەڕێ' دابگرە یان ڕاداری ئۆتۆماتیکی هەڵبکە بۆ دۆزینەوەی نوێترین ئامرازە ١٠٠٪ خۆڕاییەکان.",
                color = TextMuted,
                fontSize = 12.5.sp,
                lineHeight = 20.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

private fun openWebLink(context: Context, url: String) {
    try {
        val validUrl = if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "نەتوانرا ماڵپەڕەکە بکرێتەوە", Toast.LENGTH_SHORT).show()
    }
}
