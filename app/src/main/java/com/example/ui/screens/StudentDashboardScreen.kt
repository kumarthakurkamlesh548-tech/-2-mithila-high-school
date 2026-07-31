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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue

@Composable
fun StudentDashboardScreen(
    currentUser: UserEntity?,
    onNavigate: (String) -> Unit
) {
    val student = currentUser ?: return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Card
        item {
            GlassmorphicCard(cornerRadius = 24.dp) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Namaste, ${student.name} 👋", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                            Text(text = "Roll No: ${student.rollNumber} • ${student.className} Section ${student.section}", fontSize = 12.sp, color = Color(0xFF64748B))
                        }

                        Surface(
                            color = PrimaryBlue,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricBox("Attendance", "92.4%")
                        MetricBox("Pre-Board Grade", "530 / 600")
                        MetricBox("Homework", "1 Pending")
                    }
                }
            }
        }

        item {
            Text(text = "Student Quick Services", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TeacherActionCard(
                        title = "View Results",
                        subtitle = "Download Marksheet PDF",
                        icon = Icons.Default.Assessment,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("results") }
                    )
                    TeacherActionCard(
                        title = "View Syllabus",
                        subtitle = "BSEB Course Outline",
                        icon = Icons.Default.MenuBook,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("syllabus") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TeacherActionCard(
                        title = "Study Material",
                        subtitle = "Notes, Question Banks",
                        icon = Icons.Default.Description,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("study_material") }
                    )
                    TeacherActionCard(
                        title = "Daily Homework",
                        subtitle = "Submit Assignments",
                        icon = Icons.Default.Assignment,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("homework") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TeacherActionCard(
                        title = "School Chat",
                        subtitle = "Real-time Messaging",
                        icon = Icons.Default.Forum,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("chat") }
                    )
                    TeacherActionCard(
                        title = "AI Support Agent",
                        subtitle = "Gemini Multi-turn Assistant",
                        icon = Icons.Default.AutoAwesome,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("gemini_chatbot") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TeacherActionCard(
                        title = "Class Timetable",
                        subtitle = "Daily & Exam Routine",
                        icon = Icons.Default.Schedule,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("timetable") }
                    )
                    TeacherActionCard(
                        title = "Ask Doubts",
                        subtitle = "Academic QnA Portal",
                        icon = Icons.Default.QuestionAnswer,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("doubt_section") }
                    )
                }
            }
        }
    }
}
