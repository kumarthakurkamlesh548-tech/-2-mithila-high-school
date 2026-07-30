package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HomeworkEntity
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue
import com.example.ui.theme.SecondaryBlue

@Composable
fun HomeworkScreen(
    homeworkList: List<HomeworkEntity>
) {
    var submittedHomeworkIds by remember { mutableStateOf(setOf<Int>()) }
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
                        text = "Daily Homework & Assignments",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkBlue
                    )
                    Text(
                        text = "Class Task Tracker & Online Homework Submission",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            if (homeworkList.isEmpty()) {
                item {
                    GlassmorphicCard {
                        Text(text = "No active homework assigned currently.", fontSize = 13.sp, color = Color(0xFF64748B))
                    }
                }
            } else {
                items(homeworkList) { hw ->
                    val isSubmitted = submittedHomeworkIds.contains(hw.id)

                    GlassmorphicCard(cornerRadius = 20.dp) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Assignment,
                                        contentDescription = "HW",
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = hw.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryDarkBlue,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Surface(
                                    color = if (isSubmitted) Color(0xFF10B981).copy(alpha = 0.15f) else SecondaryBlue,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = if (isSubmitted) "Submitted" else "Pending",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSubmitted) Color(0xFF10B981) else PrimaryDarkBlue,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = hw.description,
                                fontSize = 12.sp,
                                color = Color(0xFF334155),
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Subject: ${hw.subject} (${hw.className})", fontSize = 11.sp, color = Color(0xFF64748B))
                                Text(text = "Due: ${hw.dueDate}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (!isSubmitted) {
                                Button(
                                    onClick = {
                                        submittedHomeworkIds = submittedHomeworkIds + hw.id
                                        snackbarMsg = "Homework '${hw.title}' submitted successfully!"
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                ) {
                                    Icon(Icons.Default.FileUpload, contentDescription = "Upload")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Upload & Submit Homework", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {},
                                    enabled = false,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Done", tint = Color(0xFF10B981))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Already Submitted", fontSize = 12.sp, color = Color(0xFF10B981))
                                }
                            }
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
