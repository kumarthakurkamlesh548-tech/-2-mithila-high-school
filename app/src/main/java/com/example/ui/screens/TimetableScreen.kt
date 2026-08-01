package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TimetableEntity
import com.example.data.model.UserRole
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue
import com.example.ui.theme.SecondaryBlue

@Composable
fun TimetableScreen(
    timetables: List<TimetableEntity>,
    userRole: UserRole? = null,
    onAddTimetable: (className: String, dayOfWeek: String, periodNumber: Int, timeSlot: String, subject: String, teacherName: String) -> Unit = { _, _, _, _, _, _ -> },
    onDeleteTimetable: (Int) -> Unit = {}
) {
    val canManage = userRole == UserRole.SUPER_ADMIN || userRole == UserRole.ADMIN || userRole == UserRole.TEACHER
    var selectedTab by remember { mutableStateOf(0) } // 0: Daily Class Routine, 1: Exam Timetable
    var selectedDay by remember { mutableStateOf("Monday") }
    var showAddDialog by remember { mutableStateOf(false) }
    var snackbarMsg by remember { mutableStateOf("") }

    // Dialog inputs
    var classNameInput by remember { mutableStateOf("Class 10") }
    var dayInput by remember { mutableStateOf("Monday") }
    var periodInput by remember { mutableStateOf("1") }
    var timeSlotInput by remember { mutableStateOf("10:00 AM - 10:45 AM") }
    var subjectInput by remember { mutableStateOf("Mathematics") }
    var teacherInput by remember { mutableStateOf("R. K. Sharma") }

    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val filtered = timetables.filter { it.dayOfWeek.equals(selectedDay, ignoreCase = true) }

    val examSchedule = listOf(
        Triple("Mathematics", "18 Feb 2027", "09:30 AM - 12:45 PM"),
        Triple("Science (Phy/Chem/Bio)", "20 Feb 2027", "09:30 AM - 12:45 PM"),
        Triple("Social Science", "22 Feb 2027", "09:30 AM - 12:45 PM"),
        Triple("Hindi / Maithili", "24 Feb 2027", "09:30 AM - 12:45 PM"),
        Triple("English", "26 Feb 2027", "09:30 AM - 12:45 PM")
    )

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
                        text = "Timetable & Schedules",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkBlue
                    )
                    Text(
                        text = "Class Period Routine & BSEB Board Exam Schedule",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )

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
                            Text("Daily Class Routine", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 0) Color.White else PrimaryDarkBlue)
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
                            Text("Board Exam Schedule", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 1) Color.White else PrimaryDarkBlue)
                        }
                    }
                }
            }

            if (selectedTab == 0) { // Daily Routine Tab
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        days.forEach { day ->
                            val isSel = selectedDay == day
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) PrimaryBlue else Color.White)
                                    .border(1.dp, if (isSel) PrimaryBlue else GlassBorder, RoundedCornerShape(10.dp))
                                    .clickable { selectedDay = day }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.take(3),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else PrimaryDarkBlue
                                )
                            }
                        }
                    }
                }

                if (filtered.isEmpty()) {
                    item {
                        GlassmorphicCard {
                            Text(text = "No classes scheduled for $selectedDay.", fontSize = 12.sp, color = Color(0xFF64748B))
                        }
                    }
                } else {
                    items(filtered) { item ->
                        GlassmorphicCard(cornerRadius = 18.dp) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        color = PrimaryBlue,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = "P${item.periodNumber}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = item.subject, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                                        Text(text = "Teacher: ${item.teacherName} • ${item.timeSlot}", fontSize = 11.sp, color = Color(0xFF64748B))
                                    }
                                }

                                if (canManage) {
                                    IconButton(
                                        onClick = {
                                            onDeleteTimetable(item.id)
                                            snackbarMsg = "Timetable entry deleted"
                                        }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                                    }
                                }
                            }
                        }
                    }
                }
            } else { // Exam Schedule Tab
                items(examSchedule) { (sub, date, time) ->
                    GlassmorphicCard(cornerRadius = 18.dp) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = sub, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                                Surface(
                                    color = Color(0xFF3B82F6).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(text = date, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, contentDescription = "Time", tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = time, fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }
                    }
                }
            }
        }

        if (canManage && selectedTab == 0) {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Period Routine")
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Class Routine Period", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = classNameInput,
                            onValueChange = { classNameInput = it },
                            label = { Text("Class Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = dayInput,
                            onValueChange = { dayInput = it },
                            label = { Text("Day of Week (Monday, Tuesday...)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = periodInput,
                            onValueChange = { periodInput = it },
                            label = { Text("Period Number (1, 2, 3...)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = timeSlotInput,
                            onValueChange = { timeSlotInput = it },
                            label = { Text("Time Slot (e.g. 10:00 AM - 10:45 AM)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = subjectInput,
                            onValueChange = { subjectInput = it },
                            label = { Text("Subject") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = teacherInput,
                            onValueChange = { teacherInput = it },
                            label = { Text("Teacher Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val pNum = periodInput.toIntOrNull() ?: 1
                            if (subjectInput.isNotBlank()) {
                                onAddTimetable(classNameInput, dayInput, pNum, timeSlotInput, subjectInput, teacherInput)
                                showAddDialog = false
                                snackbarMsg = "Period routine added!"
                            }
                        }
                    ) {
                        Text("Add Period")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                }
            )
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
