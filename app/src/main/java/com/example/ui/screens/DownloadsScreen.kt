package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.data.model.DownloadEntity
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue
import com.example.utils.DownloadUtils

@Composable
fun DownloadsScreen(
    downloads: List<DownloadEntity>
) {
    val context = LocalContext.current
    var snackbarMsg by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Downloads & Official Forms",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkBlue
                    )
                    Text(
                        text = "Admission Forms, Scholarship Applications & Board Admit Cards",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            items(downloads) { item ->
                GlassmorphicCard(cornerRadius = 20.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = Color(0xFFEF4444), modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = item.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                                Text(text = "Category: ${item.category} • ${item.fileSize}", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }

                        Button(
                            onClick = {
                                val url = item.driveUrl.trim()
                                if (url.startsWith("http://") || url.startsWith("https://")) {
                                    try {
                                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        snackbarMsg = "Unable to open Drive link"
                                    }
                                } else {
                                    DownloadUtils.saveToDownloads(
                                        context = context,
                                        filename = "${item.title.replace(" ", "_")}.pdf",
                                        content = "OFFICIAL DOCUMENT: ${item.title}\nCategory: ${item.category}\nSchool: +2 Govt Mithila High School Balaur, Darbhanga\nDate: 2026\nStatus: Official Verified Form"
                                    )
                                    snackbarMsg = "Saved ${item.title} to Downloads folder!"
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "Download / Drive", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        if (snackbarMsg.isNotEmpty()) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = { TextButton(onClick = { snackbarMsg = "" }) { Text("OK", color = Color.White) } }
            ) {
                Text(snackbarMsg)
            }
        }
    }
}
