package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkCardBorder
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
fun ApiKeyModal(
    currentCustomKey: String,
    hasActiveKey: Boolean,
    onSaveKey: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var keyInput by remember { mutableStateOf(currentCustomKey) }
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MatteBlackSurface,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .testTag("api_key_modal"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ڕێکخستنی کلیلی AI (API Key)",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "دەتوانیت کلیلی تایبەتی خۆت لە Google AI Studio لێرە دابنێیت. ئەگەر بەتاڵ بێت، ئەپڵیکەیشنەکە خۆکارانە لە سیستەمەوە کلیلی باکگراوند بەکاردەهێنێت.",
                    color = TextMuted,
                    fontSize = 12.5.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Status chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MatteBlackElevated,
                    border = androidx.compose.foundation.BorderStroke(0.6.dp, DarkCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    if (hasActiveKey) StatusActiveGreen else StatusInactiveRed,
                                    RoundedCornerShape(5.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (hasActiveKey) "دۆخ: کلیلی AI چالاکە و ئامادەیە بۆ وەڵامدانەوە" else "دۆخ: کلیل نەدۆزرایەوە، تکایە کلیلێک دابنێ",
                            color = if (hasActiveKey) GoldLight else Color(0xFFFF8A80),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = keyInput,
                    onValueChange = { keyInput = it },
                    label = { Text("Gemini API Key (AIzaSy...)") },
                    placeholder = { Text("کلیلی Gemini لێرە دابنێ") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "شاردنەوە" else "پیشاندان",
                                tint = GoldPrimary
                            )
                        }
                    },
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
                        .testTag("api_key_input_field")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSaveKey(keyInput.trim()) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = MatteBlack
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_api_key_btn")
            ) {
                Text("خەزنکردن (Save)", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row {
                if (keyInput.isNotEmpty() || currentCustomKey.isNotEmpty()) {
                    OutlinedButton(
                        onClick = {
                            keyInput = ""
                            onSaveKey("")
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6E6E)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("clear_api_key_btn")
                    ) {
                        Text("سڕینەوە (Clear)")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                OutlinedButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("داخستن")
                }
            }
        }
    )
}
