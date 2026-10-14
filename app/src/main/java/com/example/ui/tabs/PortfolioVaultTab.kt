package com.example.ui.tabs

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.PortfolioItem
import com.example.pdf.PortfolioPdfGenerator
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMuted
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MatteBlack
import com.example.ui.theme.MatteBlackElevated
import com.example.ui.theme.MatteBlackSurface
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import java.io.File

@Composable
fun PortfolioVaultTab(
    portfolioItems: List<PortfolioItem>,
    isGeneratingPdf: Boolean,
    lastGeneratedPdf: File?,
    onOpenAddModal: (String) -> Unit, // "IMAGE", "VIDEO", "WEB"
    onDeleteItem: (PortfolioItem) -> Unit,
    onGeneratePdf: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val images = portfolioItems.filter { it.type == "IMAGE" }
    val webItems = portfolioItems.filter { it.type == "WEB" }
    val videoItems = portfolioItems.filter { it.type == "VIDEO" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MatteBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Top PDF Compiler Hero Card ---
        item {
            PdfCompilerHeroCard(
                isGeneratingPdf = isGeneratingPdf,
                lastGeneratedPdf = lastGeneratedPdf,
                onGeneratePdf = onGeneratePdf,
                onOpenPdf = { file -> PortfolioPdfGenerator.openOrSharePdf(context, file, isShare = false) },
                onSharePdf = { file -> PortfolioPdfGenerator.openOrSharePdf(context, file, isShare = true) }
            )
        }

        // --- 2. Quick Action Buttons Row ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.AddPhotoAlternate,
                    label = "+ وێنەی دیزاین",
                    onClick = { onOpenAddModal("IMAGE") },
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    icon = Icons.Default.Movie,
                    label = "+ لینکی ڤیدیۆ",
                    onClick = { onOpenAddModal("VIDEO") },
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    icon = Icons.Default.Language,
                    label = "+ وێبسایت / ئەپ",
                    onClick = { onOpenAddModal("WEB") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // --- 3. Graphic Design Images Gallery ---
        item {
            VaultSectionHeader(
                title = "🎨 دیزاینە گرافیکییەکان (Design Images)",
                count = images.size,
                onAddClick = { onOpenAddModal("IMAGE") }
            )
        }

        if (images.isEmpty()) {
            item {
                EmptyVaultCard(message = "هێشتا هیچ وێنەیەکی دیزاین بارنەکراوە. کلیک لە دوگمەی سەرەوە بکە بۆ زیادکردن لە گەلەری.")
            }
        } else {
            item {
                ImageGridContainer(
                    images = images,
                    onDelete = onDeleteItem
                )
            }
        }

        // --- 4. Websites & Applications Section ---
        item {
            VaultSectionHeader(
                title = "🌐 پڕۆژەی وێبسایت و ئەپڵیکەیشن (Web & Apps)",
                count = webItems.size,
                onAddClick = { onOpenAddModal("WEB") }
            )
        }

        if (webItems.isEmpty()) {
            item {
                EmptyVaultCard(message = "هیچ پرۆژەیەکی وێب یان ئەپ تۆمار نەکراوە.")
            }
        } else {
            items(webItems, key = { it.id }) { item ->
                LinkItemCard(
                    item = item,
                    icon = Icons.Default.Language,
                    onOpenLink = { openExternalUrl(context, item.urlOrPath) },
                    onDelete = { onDeleteItem(item) }
                )
            }
        }

        // --- 5. Videos & Motion Graphics Section ---
        item {
            VaultSectionHeader(
                title = "🎬 مۆنتاژ و بەرهەمهێنانی ڤیدیۆ (Videos & Reels)",
                count = videoItems.size,
                onAddClick = { onOpenAddModal("VIDEO") }
            )
        }

        if (videoItems.isEmpty()) {
            item {
                EmptyVaultCard(message = "هیچ لینکی ڤیدیۆیەک (TikTok, Reels, Drive) تۆمار نەکراوە.")
            }
        } else {
            items(videoItems, key = { it.id }) { item ->
                LinkItemCard(
                    item = item,
                    icon = Icons.Default.Movie,
                    onOpenLink = { openExternalUrl(context, item.urlOrPath) },
                    onDelete = { onDeleteItem(item) }
                )
            }
        }
    }
}

@Composable
private fun PdfCompilerHeroCard(
    isGeneratingPdf: Boolean,
    lastGeneratedPdf: File?,
    onGeneratePdf: () -> Unit,
    onOpenPdf: (File) -> Unit,
    onSharePdf: (File) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.5.dp,
                Brush.linearGradient(listOf(GoldPrimary, GoldMuted, DarkCardBorder)),
                RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MatteBlackSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary.copy(alpha = 0.2f))
                        .border(1.dp, GoldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "دروستکەری فایلی PDFی شاهانە",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.5.sp
                    )
                    Text(
                        text = "Luxury Multi-Page Portfolio Compiler",
                        color = GoldLight,
                        fontSize = 11.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "کۆکردنەوەی هەموو کارە دیزاینەکان، بەستەری پڕۆژەی وێبسایت و ڤیدیۆکان لە ناو یەک فایلی PDFی فەرمی بە ناوی 'Abdulbasit_Shwan_Portfolio.pdf' بۆ ناردن بۆ کڕیاران.",
                color = TextMuted,
                fontSize = 12.5.sp,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main Gold Button: Download All My Works in One PDF
            Button(
                onClick = onGeneratePdf,
                enabled = !isGeneratingPdf,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = MatteBlack
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("generate_portfolio_pdf_btn")
            ) {
                if (isGeneratingPdf) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MatteBlack,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "فایلی PDF ئامادە دەکرێت...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = MatteBlack,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "📄 داگرتنی هەموو کارەکانم بە یەک PDF",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // If PDF already generated, show Open and Share actions
            if (lastGeneratedPdf != null && lastGeneratedPdf.exists()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onOpenPdf(lastGeneratedPdf) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldLight),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("open_generated_pdf_btn")
                    ) {
                        Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("کردنەوەی PDF", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { onSharePdf(lastGeneratedPdf) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_generated_pdf_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("هاوبەشکردن", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = MatteBlackElevated,
        border = androidx.compose.foundation.BorderStroke(0.8.dp, DarkCardBorder),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = TextWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun VaultSectionHeader(
    title: String,
    count: Int,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                color = GoldLight,
                fontWeight = FontWeight.Bold,
                fontSize = 14.5.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
                shape = CircleShape,
                color = MatteBlackElevated
            ) {
                Text(
                    text = count.toString(),
                    color = GoldPrimary,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        IconButton(
            onClick = onAddClick,
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(MatteBlackElevated)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "زیادکردن",
                tint = GoldPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun ImageGridContainer(
    images: List<PortfolioItem>,
    onDelete: (PortfolioItem) -> Unit
) {
    // 2-column image grid wrapped inside a column with rows to fit inside scrollable parent
    val chunked = images.chunked(2)
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        chunked.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { item ->
                    Box(modifier = Modifier.weight(1f)) {
                        PortfolioImageCard(item = item, onDelete = { onDelete(item) })
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PortfolioImageCard(
    item: PortfolioItem,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MatteBlackSurface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .background(MatteBlackElevated)
            ) {
                AsyncImage(
                    model = File(item.urlOrPath),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Delete Button Overlay
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.65f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "سڕینەوە",
                        tint = Color(0xFFFF6E6E),
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Text(
                text = item.title.ifBlank { "کاری دیزاینی گرافیک" },
                color = TextWhite,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun LinkItemCard(
    item: PortfolioItem,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onOpenLink: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MatteBlackSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MatteBlackElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = item.title,
                        color = TextWhite,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.urlOrPath,
                        color = GoldLight.copy(alpha = 0.7f),
                        fontSize = 10.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onOpenLink,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = "کردنەوە", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "سڕینەوە", tint = TextDim, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun EmptyVaultCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MatteBlackSurface)
    ) {
        Text(
            text = message,
            color = TextMuted,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            modifier = Modifier.padding(14.dp)
        )
    }
}

private fun openExternalUrl(context: Context, rawUrl: String) {
    try {
        val validUrl = if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://")) rawUrl else "https://$rawUrl"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "نەتوانرا بەستەرەکە بکرێتەوە", Toast.LENGTH_SHORT).show()
    }
}
