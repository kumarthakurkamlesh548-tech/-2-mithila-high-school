package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventEntity
import com.example.data.model.UserRole
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue

@Composable
fun EventsScreen(
    events: List<EventEntity>,
    userRole: UserRole? = null,
    onAddEvent: (title: String, date: String, time: String, venue: String, description: String) -> Unit = { _, _, _, _, _ -> },
    onDeleteEvent: (Int) -> Unit = {}
) {
    val canManage = userRole == UserRole.SUPER_ADMIN || userRole == UserRole.ADMIN || userRole == UserRole.TEACHER
    var showAddDialog by remember { mutableStateOf(false) }
    var snackbarMsg by remember { mutableStateOf("") }

    var titleInput by remember { mutableStateOf("") }
    var dateInput by remember { mutableStateOf("15 Aug 2026") }
    var timeInput by remember { mutableStateOf("09:00 AM") }
    var venueInput by remember { mutableStateOf("School Main Playground") }
    var descInput by remember { mutableStateOf("") }

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
                        text = "Upcoming School Events",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkBlue
                    )
                    Text(
                        text = "Sports Meets, Science Fairs & Cultural Programs",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            if (events.isEmpty()) {
                item {
                    GlassmorphicCard {
                        Text(text = "No upcoming school events scheduled.", fontSize = 13.sp, color = Color(0xFF64748B))
                    }
                }
            } else {
                items(events) { ev ->
                    GlassmorphicCard(cornerRadius = 20.dp) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ev.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryDarkBlue,
                                    modifier = Modifier.weight(1f)
                                )

                                if (canManage) {
                                    IconButton(
                                        onClick = {
                                            onDeleteEvent(ev.id)
                                            snackbarMsg = "Event deleted"
                                        }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Event", tint = Color(0xFFEF4444))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Event, contentDescription = "Date", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = ev.date, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)

                                Spacer(modifier = Modifier.width(16.dp))

                                Icon(Icons.Default.Schedule, contentDescription = "Time", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = ev.time, fontSize = 12.sp, color = Color(0xFF475569))
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Place, contentDescription = "Venue", tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Venue: ${ev.venue}", fontSize = 12.sp, color = Color(0xFF64748B))
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = ev.description,
                                fontSize = 12.sp,
                                color = Color(0xFF334155),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        if (canManage) {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Schedule School Event", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            label = { Text("Event Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = dateInput,
                            onValueChange = { dateInput = it },
                            label = { Text("Date (e.g. 15 Aug 2026)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = timeInput,
                            onValueChange = { timeInput = it },
                            label = { Text("Time (e.g. 09:00 AM)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = venueInput,
                            onValueChange = { venueInput = it },
                            label = { Text("Venue / Hall") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = descInput,
                            onValueChange = { descInput = it },
                            label = { Text("Description / Highlights") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (titleInput.isNotBlank()) {
                                onAddEvent(titleInput, dateInput, timeInput, venueInput, descInput)
                                titleInput = ""
                                descInput = ""
                                showAddDialog = false
                                snackbarMsg = "Event scheduled successfully!"
                            }
                        }
                    ) {
                        Text("Schedule")
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
