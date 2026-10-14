package com.example.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.example.data.model.PortfolioItem
import com.example.data.model.SocialLinks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object PortfolioPdfGenerator {

    private const val TAG = "PortfolioPdfGenerator"
    const val PDF_FILE_NAME = "Abdulbasit_Shwan_Portfolio.pdf"

    // Standard A4 dimensions in points (72 points per inch)
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842

    suspend fun generatePortfolioPDF(
        context: Context,
        socialLinks: SocialLinks,
        portfolioItems: List<PortfolioItem>
    ): Result<File> = withContext(Dispatchers.IO) {
        val document = PdfDocument()

        try {
            val images = portfolioItems.filter { it.type == "IMAGE" }
            val webItems = portfolioItems.filter { it.type == "WEB" }
            val videoItems = portfolioItems.filter { it.type == "VIDEO" }

            // Estimate total pages: 1 overview page + ceiling(images.size / 2)
            val imagePagesCount = if (images.isEmpty()) 0 else (images.size + 1) / 2
            val totalPages = 1 + imagePagesCount

            var currentPageNum = 1

            // --- PAGE 1: Profile, Bio, Links & Web/Video Projects ---
            val pageInfo1 = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPageNum).create()
            val page1 = document.startPage(pageInfo1)
            drawOverviewPage(page1.canvas, socialLinks, webItems, videoItems, currentPageNum, totalPages)
            document.finishPage(page1)

            // --- PAGES 2+: High-Resolution Graphic Design Artworks ---
            for (chunkIndex in images.indices step 2) {
                currentPageNum++
                val chunk = images.subList(chunkIndex, (chunkIndex + 2).coerceAtMost(images.size))
                val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPageNum).create()
                val imgPage = document.startPage(pageInfo)
                drawImageGalleryPage(context, imgPage.canvas, chunk, currentPageNum, totalPages)
                document.finishPage(imgPage)
            }

            // Save PDF to cache or external files dir
            val outputFile = File(context.filesDir, PDF_FILE_NAME)
            if (outputFile.exists()) {
                outputFile.delete()
            }

            FileOutputStream(outputFile).use { fos ->
                document.writeTo(fos)
            }

            Log.i(TAG, "Successfully generated PDF at: ${outputFile.absolutePath}, size=${outputFile.length()} bytes")
            Result.success(outputFile)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate portfolio PDF", e)
            Result.failure(e)
        } finally {
            document.close()
        }
    }

    private fun drawOverviewPage(
        canvas: Canvas,
        socialLinks: SocialLinks,
        webItems: List<PortfolioItem>,
        videoItems: List<PortfolioItem>,
        pageIndex: Int,
        totalPages: Int
    ) {
        // Luxury Dark Background
        val bgPaint = Paint().apply {
            color = Color.parseColor("#0F0F10")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), bgPaint)

        // Top Gold Accent Bar
        val goldPaint = Paint().apply {
            color = Color.parseColor("#D4AF37")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 8f, goldPaint)

        // Header Title
        val titlePaint = Paint().apply {
            color = Color.parseColor("#FFFFFF")
            textSize = 22f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText("ABDULBASIT SHWAN", 40f, 48f, titlePaint)

        val titleKurdishPaint = Paint().apply {
            color = Color.parseColor("#D4AF37")
            textSize = 17f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText("عەبدولباسیت شوان", 40f, 70f, titleKurdishPaint)

        val rolePaint = Paint().apply {
            color = Color.parseColor("#E0E0E0")
            textSize = 12f
            isAntiAlias = true
        }
        canvas.drawText("Digital Designer & Creative Developer | دیزاینەری دیجیتاڵی", 40f, 88f, rolePaint)

        // Decorative Gold Line
        val linePaint = Paint().apply {
            color = Color.parseColor("#D4AF37")
            strokeWidth = 1.5f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        canvas.drawLine(40f, 98f, (PAGE_WIDTH - 40).toFloat(), 98f, linePaint)

        // Bio Box
        val boxRect = RectF(40f, 110f, (PAGE_WIDTH - 40).toFloat(), 180f)
        val boxBg = Paint().apply {
            color = Color.parseColor("#18181A")
            style = Paint.Style.FILL
        }
        val boxBorder = Paint().apply {
            color = Color.parseColor("#333333")
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }
        canvas.drawRoundRect(boxRect, 6f, 6f, boxBg)
        canvas.drawRoundRect(boxRect, 6f, 6f, boxBorder)

        val bioHeadingPaint = Paint().apply {
            color = Color.parseColor("#D4AF37")
            textSize = 11f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText("ABOUT ME / دەربارەی من", 52f, 128f, bioHeadingPaint)

        val bioTextPaint = Paint().apply {
            color = Color.parseColor("#CCCCCC")
            textSize = 9.5f
            isAntiAlias = true
        }
        canvas.drawText(
            "دیزاینەری دیجیتاڵی و گەشەپێدەری وێب، ئەپڵیکەیشن، گرافیک، مۆنتاژی ڤیدیۆ و زیرەکی دەستکرد لە هەولێر.",
            52f, 146f, bioTextPaint
        )
        canvas.drawText(
            "دروستکردنی پڕۆژەی مۆدێرن بە تێکەڵکردنی کۆد و دیزاین بە بەرزترین ئاستی کوالێتی و متمانە.",
            52f, 162f, bioTextPaint
        )

        // Social & Contact Links Section
        var currentY = 205f
        val secTitlePaint = Paint().apply {
            color = Color.parseColor("#D4AF37")
            textSize = 12f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText("CONNECT & SOCIAL / پەیوەندی و تۆڕە کۆمەڵایەتییەکان", 40f, currentY, secTitlePaint)
        currentY += 16f

        val linkLabelPaint = Paint().apply {
            color = Color.parseColor("#A0A0A0")
            textSize = 9f
            isAntiAlias = true
        }
        val linkValPaint = Paint().apply {
            color = Color.parseColor("#64B5F6")
            textSize = 9f
            isUnderlineText = true
            isAntiAlias = true
        }

        val links = listOf(
            "LinkedIn" to socialLinks.linkedin,
            "Instagram" to socialLinks.instagram,
            "TikTok" to socialLinks.tiktok,
            "GitHub" to socialLinks.github,
            "WhatsApp" to socialLinks.whatsapp,
            "Google Drive" to socialLinks.googleDrive
        )

        // Draw in 2 columns
        val col1X = 40f
        val col2X = (PAGE_WIDTH / 2f) + 10f
        for (i in links.indices) {
            val (label, url) = links[i]
            val x = if (i % 2 == 0) col1X else col2X
            val rowY = currentY + (i / 2) * 22f

            canvas.drawText("$label:", x, rowY, linkLabelPaint)
            val shortened = if (url.length > 34) url.take(31) + "..." else url
            canvas.drawText(shortened, x + 55f, rowY, linkValPaint)
        }

        currentY += (links.size / 2 + 1) * 22f + 10f

        // Websites & Apps Section
        canvas.drawLine(40f, currentY - 8f, (PAGE_WIDTH - 40).toFloat(), currentY - 8f, linePaint)
        canvas.drawText("WEBSITES & APPLICATIONS / وێبسایت و ئەپڵیکەیشنەکان", 40f, currentY + 10f, secTitlePaint)
        currentY += 28f

        val itemTitlePaint = Paint().apply {
            color = Color.parseColor("#FFFFFF")
            textSize = 10f
            isFakeBoldText = true
            isAntiAlias = true
        }

        if (webItems.isEmpty()) {
            val emptyPaint = Paint().apply { color = Color.parseColor("#777777"); textSize = 9.5f; isAntiAlias = true }
            canvas.drawText("• هێشتا هیچ پڕۆژەیەکی وێب یان ئەپ زیاد نەکراوە (No web/app projects added yet)", 40f, currentY, emptyPaint)
            currentY += 22f
        } else {
            for (item in webItems.take(5)) {
                canvas.drawText("• ${item.title}", 40f, currentY, itemTitlePaint)
                canvas.drawText(item.urlOrPath, 220f, currentY, linkValPaint)
                currentY += 18f
            }
        }

        // Video & Motion Section
        currentY += 10f
        canvas.drawLine(40f, currentY - 8f, (PAGE_WIDTH - 40).toFloat(), currentY - 8f, linePaint)
        canvas.drawText("VIDEO & MOTION DESIGN / مۆنتاژ و بەرهەمهێنانی ڤیدیۆ", 40f, currentY + 10f, secTitlePaint)
        currentY += 28f

        if (videoItems.isEmpty()) {
            val emptyPaint = Paint().apply { color = Color.parseColor("#777777"); textSize = 9.5f; isAntiAlias = true }
            canvas.drawText("• هێشتا هیچ لینکی ڤیدیۆیەک زیاد نەکراوە (No video links added yet)", 40f, currentY, emptyPaint)
            currentY += 22f
        } else {
            for (item in videoItems.take(5)) {
                canvas.drawText("• ${item.title}", 40f, currentY, itemTitlePaint)
                canvas.drawText(item.urlOrPath, 220f, currentY, linkValPaint)
                currentY += 18f
            }
        }

        // Permanent Footer
        drawPageFooter(canvas, pageIndex, totalPages)
    }

    private fun drawImageGalleryPage(
        context: Context,
        canvas: Canvas,
        items: List<PortfolioItem>,
        pageIndex: Int,
        totalPages: Int
    ) {
        // Luxury Dark Background
        val bgPaint = Paint().apply {
            color = Color.parseColor("#0A0A0A")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), bgPaint)

        // Top Gold Accent Bar
        val goldPaint = Paint().apply {
            color = Color.parseColor("#D4AF37")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 6f, goldPaint)

        // Page Header
        val headerPaint = Paint().apply {
            color = Color.parseColor("#FFFFFF")
            textSize = 14f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText("ABDULBASIT SHWAN — GRAPHIC DESIGN & ARTWORKS", 40f, 38f, headerPaint)

        val headerSubPaint = Paint().apply {
            color = Color.parseColor("#D4AF37")
            textSize = 10f
            isAntiAlias = true
        }
        canvas.drawText("کۆگای دیزاین و بەرهەمە گرافیکییەکان", 40f, 52f, headerSubPaint)

        val linePaint = Paint().apply {
            color = Color.parseColor("#333333")
            strokeWidth = 1f
        }
        canvas.drawLine(40f, 60f, (PAGE_WIDTH - 40).toFloat(), 60f, linePaint)

        // Render up to 2 images on this page
        val availableHeight = PAGE_HEIGHT - 120f
        val itemHeight = availableHeight / 2f
        val itemWidth = (PAGE_WIDTH - 80).toFloat()

        for ((idx, item) in items.withIndex()) {
            val startY = 75f + (idx * (itemHeight + 15f))
            drawImageCard(context, canvas, item, 40f, startY, itemWidth, itemHeight - 20f)
        }

        drawPageFooter(canvas, pageIndex, totalPages)
    }

    private fun drawImageCard(
        context: Context,
        canvas: Canvas,
        item: PortfolioItem,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ) {
        // Background card
        val cardRect = RectF(x, y, x + width, y + height)
        val cardPaint = Paint().apply {
            color = Color.parseColor("#141414")
            style = Paint.Style.FILL
        }
        val borderPaint = Paint().apply {
            color = Color.parseColor("#2E2E2E")
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }
        canvas.drawRoundRect(cardRect, 8f, 8f, cardPaint)
        canvas.drawRoundRect(cardRect, 8f, 8f, borderPaint)

        // Load Bitmap
        val bitmap = loadOptimizedBitmap(item.urlOrPath, width.toInt(), (height - 35f).toInt())

        if (bitmap != null) {
            val imgDestRect = RectF(
                x + 8f,
                y + 8f,
                x + width - 8f,
                y + height - 32f
            )

            // Calculate scaled rect preserving aspect ratio
            val srcRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
            val destRatio = imgDestRect.width() / imgDestRect.height()

            val finalDest = if (srcRatio > destRatio) {
                val h = imgDestRect.width() / srcRatio
                val top = imgDestRect.top + (imgDestRect.height() - h) / 2f
                RectF(imgDestRect.left, top, imgDestRect.right, top + h)
            } else {
                val w = imgDestRect.height() * srcRatio
                val left = imgDestRect.left + (imgDestRect.width() - w) / 2f
                RectF(left, imgDestRect.top, left + w, imgDestRect.bottom)
            }

            canvas.drawBitmap(bitmap, null, finalDest, Paint(Paint.FILTER_BITMAP_FLAG))
        }

        // Caption title
        val titlePaint = Paint().apply {
            color = Color.parseColor("#D4AF37")
            textSize = 10.5f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText(
            if (item.title.isBlank()) "کاری گرافیکی دیزاینەر عەبدولباسیت شوان" else item.title,
            x + 14f,
            y + height - 12f,
            titlePaint
        )
    }

    private fun loadOptimizedBitmap(filePath: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(filePath, options)

            var sampleSize = 1
            if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
                val halfHeight = options.outHeight / 2
                val halfWidth = options.outWidth / 2
                while ((halfHeight / sampleSize) >= reqHeight && (halfWidth / sampleSize) >= reqWidth) {
                    sampleSize *= 2
                }
            }

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            BitmapFactory.decodeFile(filePath, decodeOptions)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading bitmap: $filePath", e)
            null
        }
    }

    private fun drawPageFooter(canvas: Canvas, pageIndex: Int, totalPages: Int) {
        val footerY = PAGE_HEIGHT - 25f
        val linePaint = Paint().apply {
            color = Color.parseColor("#2A2A2A")
            strokeWidth = 0.8f
        }
        canvas.drawLine(40f, footerY - 12f, (PAGE_WIDTH - 40).toFloat(), footerY - 12f, linePaint)

        val footerTextPaint = Paint().apply {
            color = Color.parseColor("#888888")
            textSize = 8.5f
            isAntiAlias = true
        }
        canvas.drawText("دروستکراوە لەلایەن عەبدولباسیت شوان © | Created by Abdulbasit Shwan", 40f, footerY, footerTextPaint)

        val pageNumPaint = Paint().apply {
            color = Color.parseColor("#D4AF37")
            textSize = 8.5f
            isFakeBoldText = true
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("Page $pageIndex of $totalPages", (PAGE_WIDTH - 40).toFloat(), footerY, pageNumPaint)
    }

    fun openOrSharePdf(context: Context, pdfFile: File, isShare: Boolean = false) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val intent = Intent(if (isShare) Intent.ACTION_SEND else Intent.ACTION_VIEW).apply {
                if (isShare) {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "Abdulbasit Shwan - Digital Designer Portfolio")
                } else {
                    setDataAndType(uri, "application/pdf")
                }
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(
                intent,
                if (isShare) "هاوبەشکردنی پۆرتفۆلیۆی PDF" else "کردنەوەی پۆرتفۆلیۆ"
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open or share PDF", e)
        }
    }
}
