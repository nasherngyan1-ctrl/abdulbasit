package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MatteBlack
import com.example.ui.theme.MatteBlackElevated
import com.example.ui.theme.MatteBlackSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun AddProjectModal(
    type: String, // "IMAGE", "VIDEO", "WEB"
    onAddImage: (List<Uri>, String) -> Unit,
    onAddLink: (String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    // Android 13+ standard zero-permission Photo Picker
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedUris = uris
        }
    }

    val icon = when (type) {
        "IMAGE" -> Icons.Default.AddPhotoAlternate
        "VIDEO" -> Icons.Default.Movie
        else -> Icons.Default.Language
    }

    val dialogTitle = when (type) {
        "IMAGE" -> "زیادکردنی وێنەی دیزاین (Upload Design)"
        "VIDEO" -> "زیادکردنی لینکی ڤیدیۆ (Video Link)"
        else -> "زیادکردنی لینکی وێبسایت یان ئەپ (Web/App Link)"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MatteBlackSurface,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .testTag("add_project_modal"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = dialogTitle,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (type == "IMAGE") {
                    Text(
                        text = "دەتوانیت چەند وێنەیەک لە گەلەری مۆبایلەکەت هەڵبژێریت بۆ دانان لە پۆرتفۆلیۆ و دروستکردنی فایلی PDF.",
                        color = TextMuted,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            pickMediaLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldLight),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("select_gallery_images_btn")
                    ) {
                        Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = GoldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedUris.isEmpty()) "هەڵبژاردنی وێنە لە گەلەری" else "${selectedUris.size} وێنە هەڵبژێردراوە",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("ناونیشانی کارەکە (دڵخوازانە)") },
                        placeholder = { Text("بۆ نموونە: دیزاینی براندینگی کۆمپانیا") },
                        singleLine = true,
                        colors = inputColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("ناوی پڕۆژە / ڤیدیۆ") },
                        placeholder = { Text(if (type == "VIDEO") "مۆنتاژی ڕیکلامی مۆدێرن" else "وێبسایتی بازرگانی فەرمی") },
                        singleLine = true,
                        colors = inputColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("project_title_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        label = { Text("لینکی ڕاستەوخۆ (URL)") },
                        placeholder = { Text(if (type == "VIDEO") "https://tiktok.com/@... یان Drive" else "https://github.com/... یان Gamma") },
                        singleLine = true,
                        colors = inputColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("project_url_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("کورتە پێناسە (دڵخوازانە)") },
                        placeholder = { Text("وەسفێکی کورت بۆ ئەم پڕۆژەیە") },
                        singleLine = false,
                        maxLines = 2,
                        colors = inputColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (type == "IMAGE") {
                        if (selectedUris.isNotEmpty()) {
                            onAddImage(selectedUris, title)
                        }
                    } else {
                        if (title.isNotBlank() && url.isNotBlank()) {
                            onAddLink(type, title, url, description)
                        }
                    }
                },
                enabled = if (type == "IMAGE") selectedUris.isNotEmpty() else (title.isNotBlank() && url.isNotBlank()),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = MatteBlack
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_add_project_btn")
            ) {
                Text("زیادکردن", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("پاشگەزبوونەوە")
            }
        }
    )
}

@Composable
private fun inputColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = GoldPrimary,
    unfocusedBorderColor = DarkCardBorder,
    focusedLabelColor = GoldPrimary,
    unfocusedLabelColor = TextMuted,
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite,
    cursorColor = GoldPrimary,
    focusedContainerColor = MatteBlackElevated,
    unfocusedContainerColor = MatteBlackElevated
)
