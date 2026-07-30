package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue
import com.example.ui.theme.SecondaryBlue

@Composable
fun AttendanceScreen(
    userRole: UserRole?,
    onSaveAttendance: (String, Set<String>) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: View Record, 1: Mark Attendance (Teachers)
    var selectedClass by remember { mutableStateOf("Class 10") }

    val studentsRoster = listOf(
        Pair("10042", "Aman Kumar"),
        Pair("10043", "Priya Kumari"),
        Pair("10044", "Rahul Sharma"),
        Pair("10045", "Sita Devi"),
        Pair("10046", "Vikram Singh"),
        Pair("10047", "Anjali Mishra"),
        Pair("10048", "Deepak Roy")
    )

    var presentRolls by remember { mutableStateOf(setOf("10042", "10043", "10044", "10045", "10046")) }
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
                        text = "Attendance Records & Roll Call",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkBlue
                    )
                    Text(
                        text = "Daily & Monthly Attendance Percentage Statistics",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )

                    if (userRole == UserRole.TEACHER || userRole == UserRole.ADMIN) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(SecondaryBlue)
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selectedTab == 0) PrimaryBlue else Color.Transparent)
                                    .clickable { selectedTab = 0 }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Student View", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 0) Color.White else PrimaryDarkBlue)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selectedTab == 1) PrimaryBlue else Color.Transparent)
                                    .clickable { selectedTab = 1 }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Take Class Attendance", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 1) Color.White else PrimaryDarkBlue)
                            }
                        }
                    }
                }
            }

            if (selectedTab == 0) { // Student Attendance Summary View
                item {
                    GlassmorphicCard(cornerRadius = 24.dp) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "Overall Attendance Percentage", fontSize = 13.sp, color = Color(0xFF64748B))
                                    Text(text = "92.4%", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                }
                                CircularProgressIndicator(
                                    progress = { 0.92f },
                                    modifier = Modifier.size(60.dp),
                                    color = PrimaryBlue,
                                    strokeWidth = 6.dp,
                                    trackColor = SecondaryBlue
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = GlassBorder)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatBox(label = "Total Working Days", value = "120 Days")
                                StatBox(label = "Days Present", value = "111 Days")
                                StatBox(label = "Days Absent", value = "9 Days")
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Recent Attendance History",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkBlue
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    GlassmorphicCard(cornerRadius = 20.dp) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                Pair("30 July 2026", true),
                                Pair("29 July 2026", true),
                                Pair("28 July 2026", true),
                                Pair("27 July 2026", false),
                                Pair("26 July 2026", true),
                                Pair("25 July 2026", true)
                            ).forEach { (date, present) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = date, fontSize = 13.sp, color = Color(0xFF334155))
                                    Surface(
                                        color = if (present) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = if (present) "PRESENT" else "ABSENT",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (present) Color(0xFF10B981) else Color(0xFFEF4444),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                Divider(color = GlassBorder)
                            }
                        }
                    }
                }
            } else { // Teacher Mark Attendance Tab
                item {
                    GlassmorphicCard(cornerRadius = 20.dp) {
                        Column {
                            Text(text = "Class Roster Attendance Register", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                            Text(text = "Class: $selectedClass • Date: Today", fontSize = 11.sp, color = Color(0xFF64748B))

                            Spacer(modifier = Modifier.height(14.dp))

                            studentsRoster.forEach { (roll, name) ->
                                val isPresent = presentRolls.contains(roll)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isPresent) Color(0xFF10B981).copy(alpha = 0.1f) else Color(0xFFEF4444).copy(alpha = 0.1f))
                                        .clickable {
                                            presentRolls = if (isPresent) presentRolls - roll else presentRolls + roll
                                        }
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                                        Text(text = "Roll No: $roll", fontSize = 11.sp, color = Color(0xFF64748B))
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(if (isPresent) Color(0xFF10B981) else Color(0xFFEF4444)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isPresent) Icons.Default.Check else Icons.Default.Close,
                                            contentDescription = "Status",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    onSaveAttendance(selectedClass, presentRolls)
                                    snackbarMsg = "Attendance saved for $selectedClass!"
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                            ) {
                                Text("Submit Attendance Register", fontSize = 13.sp, fontWeight = FontWeight.Bold)
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

@Composable
fun StatBox(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = Color(0xFF64748B))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
    }
}
