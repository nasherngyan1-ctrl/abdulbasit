package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SocialLinks
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMuted
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MatteBlack
import com.example.ui.theme.MatteBlackElevated
import com.example.ui.theme.MatteBlackSurface
import com.example.ui.theme.StatusActiveGreen
import com.example.ui.theme.StatusInactiveRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun ProfileHeader(
    socialLinks: SocialLinks,
    hasActiveKey: Boolean,
    onApiKeyClick: () -> Unit,
    onEditLinksClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MatteBlack)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Top Action Bar: App Brand & In-App API Key Slot
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Monogram & Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(GoldPrimary, GoldMuted, MatteBlackElevated)
                            )
                        )
                        .border(1.dp, GoldPrimary, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AS",
                        color = MatteBlack,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Abdulbasit Shwan",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "عەبدولباسیت شوان",
                        color = GoldPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // In-App Key Slot Button & Live Status Indicator
            Surface(
                onClick = onApiKeyClick,
                shape = RoundedCornerShape(20.dp),
                color = MatteBlackSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (hasActiveKey) GoldPrimary else DarkCardBorder),
                modifier = Modifier.testTag("api_key_manager_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    // Status Dot Indicator
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(if (hasActiveKey) StatusActiveGreen else StatusInactiveRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (hasActiveKey) "🔑 چالاکە" else "🔑 بێ کلیل",
                        color = if (hasActiveKey) GoldLight else Color.LightGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Luxury Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(GoldPrimary.copy(alpha = 0.7f), DarkCardBorder, GoldMuted.copy(alpha = 0.4f))
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MatteBlackSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header with Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Abdulbasit Shwan | عەبدولباسیت شوان",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        // Badge / Role
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GoldPrimary.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, GoldPrimary)
                        ) {
                            Text(
                                text = "✨ Digital Designer | دیزاینەری دیجیتاڵی",
                                color = GoldLight,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Edit Links Action
                    IconButton(
                        onClick = onEditLinksClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MatteBlackElevated)
                            .testTag("edit_links_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "دەستکاریکردنی لینکەکان",
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Full Kurdish Description
                Text(
                    text = "دیزاینەری دیجیتاڵی و گەشەپێدەری وێب، ئەپڵیکەیشن، گرافیک، مۆنتاژی ڤیدیۆ و زیرەکی دەستکرد لە هەولێر. دروستکردنی پڕۆژەی مۆدێرن بە تێکەڵکردنی کۆد و دیزاین.",
                    color = TextMuted,
                    fontSize = 13.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Social & Contact Links (LinkedIn, Instagram, TikTok, GitHub, WhatsApp, Google Drive)
                SocialLinksBar(
                    links = socialLinks,
                    onLinkClick = { url ->
                        openUrlSafely(context, url)
                    }
                )
            }
        }
    }
}

@Composable
fun SocialLinksBar(
    links: SocialLinks,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        SocialButtonData("LinkedIn", links.linkedin, GoldLight),
        SocialButtonData("Instagram", links.instagram, Color(0xFFE1306C)),
        SocialButtonData("TikTok", links.tiktok, TextWhite),
        SocialButtonData("GitHub", links.github, Color(0xFFB0BEC5)),
        SocialButtonData("WhatsApp", links.whatsapp, Color(0xFF25D366)),
        SocialButtonData("Drive", links.googleDrive, Color(0xFF4285F4))
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            Surface(
                onClick = { onLinkClick(item.url) },
                shape = RoundedCornerShape(8.dp),
                color = MatteBlackElevated,
                border = androidx.compose.foundation.BorderStroke(0.6.dp, DarkCardBorder),
                modifier = Modifier.testTag("social_btn_${item.name.lowercase()}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(item.accentColor)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.name,
                        color = TextWhite,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private data class SocialButtonData(
    val name: String,
    val url: String,
    val accentColor: Color
)

private fun openUrlSafely(context: Context, rawUrl: String) {
    try {
        val validUrl = if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://")) {
            rawUrl
        } else {
            "https://$rawUrl"
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "نەتوانرا لینکەکە بکرێتەوە: $rawUrl", Toast.LENGTH_SHORT).show()
    }
}
