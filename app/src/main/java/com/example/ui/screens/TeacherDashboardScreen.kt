package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue

@Composable
fun TeacherDashboardScreen(
    onNavigate: (String) -> Unit,
    onUploadHomeworkClick: () -> Unit,
    onUploadMaterialClick: () -> Unit,
    onUploadResultClick: () -> Unit
) {
    var showHomeworkDialog by remember { mutableStateOf(false) }
    var showMaterialDialog by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlassmorphicCard(cornerRadius = 24.dp) {
                Column {
                    Text(text = "Faculty Teacher Dashboard", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                    Text(text = "Prof. Dr. R. K. Jha • Senior Secondary Faculty", fontSize = 12.sp, color = Color(0xFF64748B))

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricBox("Class Section", "Class 10 A")
                        MetricBox("Total Students", "65 Students")
                        MetricBox("Pending Doubts", "3 Doubts")
                    }
                }
            }
        }

        item {
            Text(text = "Core Teacher Actions", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TeacherActionCard(
                        title = "Take Attendance",
                        subtitle = "Daily Roll Call Register",
                        icon = Icons.Default.FactCheck,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("attendance") }
                    )
                    TeacherActionCard(
                        title = "Upload Homework",
                        subtitle = "Assign Class Work",
                        icon = Icons.Default.Assignment,
                        modifier = Modifier.weight(1f),
                        onClick = { showHomeworkDialog = true }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TeacherActionCard(
                        title = "Upload Study Material",
                        subtitle = "Notes, PDFs & PYQs",
                        icon = Icons.Default.MenuBook,
                        modifier = Modifier.weight(1f),
                        onClick = { showMaterialDialog = true }
                    )
                    TeacherActionCard(
                        title = "Reply Doubts",
                        subtitle = "Student QnA Portal",
                        icon = Icons.Default.QuestionAnswer,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("doubt_section") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TeacherActionCard(
                        title = "Upload Results",
                        subtitle = "Enter Exam Marks",
                        icon = Icons.Default.Assessment,
                        modifier = Modifier.weight(1f),
                        onClick = { showResultDialog = true }
                    )
                    TeacherActionCard(
                        title = "Class Timetable",
                        subtitle = "View Schedule",
                        icon = Icons.Default.Schedule,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("timetable") }
                    )
                }
            }
        }
    }

    if (showHomeworkDialog) {
        AlertDialog(
            onDismissRequest = { showHomeworkDialog = false },
            title = { Text("Create Homework Assignment", fontWeight = FontWeight.Bold, color = PrimaryDarkBlue) },
            text = { Text("Enter details for new class homework assignment for Class 10.") },
            confirmButton = {
                Button(
                    onClick = {
                        onUploadHomeworkClick()
                        showHomeworkDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Publish Homework")
                }
            },
            dismissButton = { TextButton(onClick = { showHomeworkDialog = false }) { Text("Cancel") } }
        )
    }

    if (showMaterialDialog) {
        AlertDialog(
            onDismissRequest = { showMaterialDialog = false },
            title = { Text("Upload Study Material / PDF", fontWeight = FontWeight.Bold, color = PrimaryDarkBlue) },
            text = { Text("Upload new notes or question bank file for Class 10 students.") },
            confirmButton = {
                Button(
                    onClick = {
                        onUploadMaterialClick()
                        showMaterialDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Upload Material")
                }
            },
            dismissButton = { TextButton(onClick = { showMaterialDialog = false }) { Text("Cancel") } }
        )
    }

    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = { Text("Upload Examination Result", fontWeight = FontWeight.Bold, color = PrimaryDarkBlue) },
            text = { Text("Enter student marks for BSEB Evaluation.") },
            confirmButton = {
                Button(
                    onClick = {
                        onUploadResultClick()
                        showResultDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Save Result")
                }
            },
            dismissButton = { TextButton(onClick = { showResultDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun TeacherActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
            Text(text = subtitle, fontSize = 10.sp, color = Color(0xFF64748B))
        }
    }
}
