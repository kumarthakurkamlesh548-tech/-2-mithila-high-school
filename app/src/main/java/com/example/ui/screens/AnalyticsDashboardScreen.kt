package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue

@Composable
fun AnalyticsDashboardScreen(
    users: List<UserEntity>,
    homeworkList: List<HomeworkEntity>,
    results: List<ResultEntity>,
    doubts: List<DoubtEntity>,
    notices: List<NoticeEntity>,
    events: List<EventEntity>
) {
    val studentCount = users.count { it.role == UserRole.STUDENT }
    val teacherCount = users.count { it.role == UserRole.TEACHER }
    val adminCount = users.count { it.role == UserRole.ADMIN || it.role == UserRole.SUPER_ADMIN }
    val totalHomework = homeworkList.size
    val totalResults = results.size
    val totalNotices = notices.size
    val totalDoubts = doubts.size
    val solvedDoubts = doubts.count { it.status.equals("Answered", true) || it.status.equals("Solved", true) }

    val passPercentage = if (results.isNotEmpty()) {
        val passed = results.count { it.percentage >= 33.0 }
        (passed.toDouble() / results.size * 100).toInt()
    } else 92

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = PrimaryBlue,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Analytics, contentDescription = "Analytics", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("School Analytics & Insights", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                    Text("Real-time operational statistics", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        // Row 1: Key Figures
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsStatCard(
                    title = "Students",
                    value = "$studentCount",
                    sub = "Enrolled",
                    icon = Icons.Default.School,
                    color = Color(0xFF0284C7),
                    modifier = Modifier.weight(1f)
                )
                AnalyticsStatCard(
                    title = "Teachers",
                    value = "$teacherCount",
                    sub = "Faculty Staff",
                    icon = Icons.Default.Group,
                    color = Color(0xFF16A34A),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsStatCard(
                    title = "Pass Rate",
                    value = "$passPercentage%",
                    sub = "Exam Results",
                    icon = Icons.Default.Verified,
                    color = Color(0xFF7C3AED),
                    modifier = Modifier.weight(1f)
                )
                AnalyticsStatCard(
                    title = "Doubts Solved",
                    value = "$solvedDoubts / $totalDoubts",
                    sub = "Academic QnA",
                    icon = Icons.Default.QuestionAnswer,
                    color = Color(0xFFEA580C),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section: Detailed Academic Metrics
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Academic Operational Volume", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                    Spacer(modifier = Modifier.height(12.dp))

                    MetricProgressRow("Homework Assignments Posted", totalHomework, 50, Color(0xFF0284C7))
                    Spacer(modifier = Modifier.height(10.dp))
                    MetricProgressRow("Exam Results Published", totalResults, 100, Color(0xFF16A34A))
                    Spacer(modifier = Modifier.height(10.dp))
                    MetricProgressRow("Official Notices Broadcasted", totalNotices, 30, Color(0xFFD97706))
                    Spacer(modifier = Modifier.height(10.dp))
                    MetricProgressRow("Upcoming Events Scheduled", events.size, 20, Color(0xFF7C3AED))
                }
            }
        }

        // Section: Demographics Breakdown
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("User Role Breakdown", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                    Spacer(modifier = Modifier.height(12.dp))

                    val totalUsers = (studentCount + teacherCount + adminCount).coerceAtLeast(1)
                    val studentRatio = studentCount.toFloat() / totalUsers
                    val teacherRatio = teacherCount.toFloat() / totalUsers
                    val adminRatio = adminCount.toFloat() / totalUsers

                    Text("Students (${(studentRatio * 100).toInt()}%)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    LinearProgressIndicator(
                        progress = { studentRatio },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Color(0xFF0284C7),
                        trackColor = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Teachers (${(teacherRatio * 100).toInt()}%)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    LinearProgressIndicator(
                        progress = { teacherRatio },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Color(0xFF16A34A),
                        trackColor = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Administrators (${(adminRatio * 100).toInt()}%)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    LinearProgressIndicator(
                        progress = { adminRatio },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Color(0xFF7C3AED),
                        trackColor = Color(0xFFE2E8F0)
                    )
                }
            }
        }
    }
}

@Composable
fun AnalyticsStatCard(
    title: String,
    value: String,
    sub: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                Surface(
                    shape = CircleShape,
                    color = color.copy(alpha = 0.15f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
            Text(sub, fontSize = 10.sp, color = color)
        }
    }
}

@Composable
fun MetricProgressRow(
    label: String,
    count: Int,
    targetMax: Int,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
            Text("$count", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        val progress = (count.toFloat() / targetMax).coerceIn(0.05f, 1f)
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = color,
            trackColor = Color(0xFFE2E8F0)
        )
    }
}
