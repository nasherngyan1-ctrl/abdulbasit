package com.example.data.remote

import com.example.data.model.RadarItem

object FallbackRadarTools {

    private val verifiedTools = listOf(
        FreeAiToolResult(
            toolName = "Krea AI (Real-time Generation)",
            category = "وێنە و دیزاین",
            whyFree = "ڕۆژانە جەنەرەیت و ئەپسکەیڵی بەخۆڕایی بەردەستە بێ کرێ",
            directLink = "https://www.krea.ai",
            fullSummary = "ئامرازێکی ناوازەیە بۆ دروستکردنی وێنە لە کاتی ڕاستەقینەدا (Realtime Gen) لەگەڵ باشترکردنی کوالێتی وێنە و ڤیدیۆ بە زیرەکی دەستکرد بە شێوەیەکی خۆڕایی."
        ),
        FreeAiToolResult(
            toolName = "v0 by Vercel (AI UI Builder)",
            category = "وێبسایت و دیزاین",
            whyFree = "پلانی مانگانەی خۆڕایی بەردەستە بە کرێدتی نوێبووەوە",
            directLink = "https://v0.dev",
            fullSummary = "بەهێزترین ئامرازی دروستکردنی ڕووکاری وێبسایت و دیزاینی کۆد بە بەکارهێنانی تێکست بە تەواوی ئامادەکراو بۆ React و Tailwind."
        ),
        FreeAiToolResult(
            toolName = "CapCut Web AI (Video Suite)",
            category = "مۆنتاژ و ڤیدیۆ",
            whyFree = "سەرجەم ئامرازە زیرەکەکانی وەک سڕینەوەی باکگراوند و کەپشن ١٠٠٪ خۆڕاییە",
            directLink = "https://www.capcut.com/tools",
            fullSummary = "ئامرازێکی مۆدێرنی مۆنتاژی ڤیدیۆیە بە زیرەکی دەستکرد بۆ دروستکردنی کەپشنی ئۆتۆماتیکی، سڕینەوەی باکگراوند و باشترکردنی دەنگ بۆ تیکتۆک و ڕیڵز."
        ),
        FreeAiToolResult(
            toolName = "Ideogram 2.0 (Typography AI)",
            category = "وێنە و دیزاین",
            whyFree = "ڕۆژانە چەندین جەنەرەیشنی بەخۆڕایی پێشکەش دەکات",
            directLink = "https://ideogram.ai",
            fullSummary = "تایبەتترین مۆدێلی دروستکردنی وێنەیە کە بە شێوەیەکی نایاب و ورد دەق و تێکستی دیزاین لە ناو وێنەکان دادەنێت بۆ لۆگۆ و پۆستەر."
        ),
        FreeAiToolResult(
            toolName = "Leonardo.ai Free Tier",
            category = "وێنە و 3D",
            whyFree = "ڕۆژانە ١٥٠ تۆکنی نوێبووەوەی بەخۆڕایی دەدات",
            directLink = "https://leonardo.ai",
            fullSummary = "کۆمەڵەیەکی بەهێزی مۆدێلە مۆدێرنەکانی دروستکردنی وێنەی فۆتۆریالیستیک، گرافیک دیزاین و ڕێندەری سێ ڕەهەندی بە بەخۆڕایی."
        ),
        FreeAiToolResult(
            toolName = "Clipchamp by Microsoft",
            category = "مۆنتاژ و ڤیدیۆ",
            whyFree = "دروستکردنی دەنگی زیرەک (Text-to-Speech) و ئیدیت بە خۆڕایی بە کوالێتی 1080p",
            directLink = "https://clipchamp.com",
            fullSummary = "ئیدیتەری فەرمیی ڤیدیۆی مایکرۆسۆفت لەگەڵ دەیان دەنگی سروشتی بۆ ڤیدیۆ و یوتیوب بە بێ وۆتەرمارک."
        ),
        FreeAiToolResult(
            toolName = "Hugging Face Spaces",
            category = "ئەپڵیکەیشن و کۆد",
            whyFree = "هەزاران مۆدێلی نوێی Open Source بەردەستە بە بەخۆڕایی",
            directLink = "https://huggingface.co/spaces",
            fullSummary = "گەورەترین مەکۆی سەرچاوەکراوە بۆ تاقیکردنەوەی نوێترین مۆدێلەکانی دەنگ، وێنە، تێکست و کۆد بێ هیچ تێچوویەک."
        ),
        FreeAiToolResult(
            toolName = "Gamma App (AI Presentation & Web)",
            category = "وێبسایت و دیزاین",
            whyFree = "کرێدتی دەستپێکی بەخۆڕایی بۆ دروستکردنی وێبپەیج و پرێزنتەیشن",
            directLink = "https://gamma.app",
            fullSummary = "بە یەک کورتە تێکست وێبسایتی تەواو و پرێزنتەیشنی مۆدێرنی بازرگانی دروست دەکات بە دیزاینێکی شاهانە."
        )
    )

    private var currentIndex = 0

    fun getNextFallbackTool(): FreeAiToolResult {
        val tool = verifiedTools[currentIndex % verifiedTools.size]
        currentIndex++
        return tool
    }
}
