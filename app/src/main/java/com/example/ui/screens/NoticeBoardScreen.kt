package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NoticeEntity
import com.example.data.model.UserRole
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue
import com.example.ui.theme.SecondaryBlue

@Composable
fun NoticeBoardScreen(
    notices: List<NoticeEntity>,
    userRole: UserRole?,
    onAddNotice: (title: String, content: String, category: String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }

    var titleInput by remember { mutableStateOf("") }
    var contentInput by remember { mutableStateOf("") }
    var catInput by remember { mutableStateOf("Academic") }

    val categories = listOf("All", "Exam", "Event", "Academic", "Urgent")
    val filtered = notices.filter { selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Official Notice Board",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkBlue
                        )
                        Text(
                            text = "School Circulars, Board Updates & Events",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    if (userRole == UserRole.ADMIN || userRole == UserRole.SUPER_ADMIN || userRole == UserRole.TEACHER) {
                        FloatingActionButton(
                            onClick = { showAddDialog = true },
                            containerColor = PrimaryBlue,
                            contentColor = Color.White,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Notice")
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PrimaryBlue else Color.White)
                                .border(1.dp, if (isSelected) PrimaryBlue else GlassBorder, RoundedCornerShape(12.dp))
                                .clickable { selectedCategory = cat }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else PrimaryDarkBlue
                            )
                        }
                    }
                }
            }

            if (filtered.isEmpty()) {
                item {
                    GlassmorphicCard {
                        Text(text = "No notices found for '$selectedCategory'.", fontSize = 13.sp, color = Color(0xFF64748B))
                    }
                }
            } else {
                items(filtered) { notice ->
                    GlassmorphicCard(cornerRadius = 20.dp) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = when(notice.category.lowercase()) {
                                        "urgent" -> Color(0xFFEF4444).copy(alpha = 0.15f)
                                        "exam" -> Color(0xFF3B82F6).copy(alpha = 0.15f)
                                        else -> SecondaryBlue
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = notice.category.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when(notice.category.lowercase()) {
                                            "urgent" -> Color(0xFFEF4444)
                                            "exam" -> Color(0xFF3B82F6)
                                            else -> PrimaryDarkBlue
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Text(text = notice.date, fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = notice.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDarkBlue
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = notice.content,
                                fontSize = 12.sp,
                                color = Color(0xFF334155),
                                lineHeight = 18.sp
                            )

                            if (notice.driveUrl.isNotBlank()) {
                                val context = androidx.compose.ui.platform.LocalContext.current
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        val url = notice.driveUrl.trim()
                                        if (url.startsWith("http://") || url.startsWith("https://")) {
                                            try {
                                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                // ignore
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Open Attached Google Drive Link", fontSize = 11.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Issued by: ${notice.postedBy}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Publish Official Notice", fontWeight = FontWeight.Bold, color = PrimaryDarkBlue) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            label = { Text("Notice Title") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = contentInput,
                            onValueChange = { contentInput = it },
                            label = { Text("Detailed Circular Text") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (titleInput.isNotBlank() && contentInput.isNotBlank()) {
                                onAddNotice(titleInput, contentInput, catInput)
                                titleInput = ""
                                contentInput = ""
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("Publish Notice")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}
