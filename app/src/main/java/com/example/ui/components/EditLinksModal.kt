package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SocialLinks
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MatteBlack
import com.example.ui.theme.MatteBlackElevated
import com.example.ui.theme.MatteBlackSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun EditLinksModal(
    currentLinks: SocialLinks,
    onSaveLinks: (SocialLinks) -> Unit,
    onDismiss: () -> Unit
) {
    var linkedin by remember { mutableStateOf(currentLinks.linkedin) }
    var instagram by remember { mutableStateOf(currentLinks.instagram) }
    var tiktok by remember { mutableStateOf(currentLinks.tiktok) }
    var github by remember { mutableStateOf(currentLinks.github) }
    var whatsapp by remember { mutableStateOf(currentLinks.whatsapp) }
    var googleDrive by remember { mutableStateOf(currentLinks.googleDrive) }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MatteBlackSurface,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .testTag("edit_links_modal"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "دەستکاریکردنی لینکەکان (Edit Links)",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "لینکە فەرمییەکانی خۆت بنووسە. ئەم بەستانە لە سەرەوەی پرۆفایل و ناو فایلی PDFی پۆرتفۆلیۆکەت دادەنرێن.",
                    color = TextMuted,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                CustomLinkField("LinkedIn", linkedin, { linkedin = it }, "https://linkedin.com/in/...")
                Spacer(modifier = Modifier.height(8.dp))

                CustomLinkField("Instagram", instagram, { instagram = it }, "https://instagram.com/...")
                Spacer(modifier = Modifier.height(8.dp))

                CustomLinkField("TikTok", tiktok, { tiktok = it }, "https://tiktok.com/@...")
                Spacer(modifier = Modifier.height(8.dp))

                CustomLinkField("GitHub", github, { github = it }, "https://github.com/...")
                Spacer(modifier = Modifier.height(8.dp))

                CustomLinkField("WhatsApp", whatsapp, { whatsapp = it }, "https://wa.me/...")
                Spacer(modifier = Modifier.height(8.dp))

                CustomLinkField("Google Drive", googleDrive, { googleDrive = it }, "https://drive.google.com/...")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveLinks(
                        currentLinks.copy(
                            linkedin = linkedin.trim(),
                            instagram = instagram.trim(),
                            tiktok = tiktok.trim(),
                            github = github.trim(),
                            whatsapp = whatsapp.trim(),
                            googleDrive = googleDrive.trim()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = MatteBlack
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_links_btn")
            ) {
                Text("خەزنکردن (Save)", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("داخستن")
            }
        }
    )
}

@Composable
private fun CustomLinkField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.5.sp) },
        placeholder = { Text(placeholder, fontSize = 11.5.sp) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GoldPrimary,
            unfocusedBorderColor = DarkCardBorder,
            focusedLabelColor = GoldPrimary,
            unfocusedLabelColor = TextMuted,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            cursorColor = GoldPrimary,
            focusedContainerColor = MatteBlackElevated,
            unfocusedContainerColor = MatteBlackElevated
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("input_${label.lowercase().replace(" ", "_")}")
    )
}
