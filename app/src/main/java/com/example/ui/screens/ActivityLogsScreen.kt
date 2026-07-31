package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.model.ActivityLogEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityLogsScreen(
    logs: List<ActivityLogEntity>
) {
    var selectedType by remember { mutableStateOf("All") }
    val types = listOf("All", "Login", "Logout", "Attendance", "Homework", "Result", "Notice", "User Management")

    val filteredLogs = remember(logs, selectedType) {
        if (selectedType == "All") logs
        else logs.filter { it.actionType.contains(selectedType, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top Header
        Surface(
            color = PrimaryDarkBlue,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = "Activity Logs",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Super Admin Audit Logs",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Track administrative & portal events",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                ScrollableTabRow(
                    selectedTabIndex = types.indexOf(selectedType),
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    edgePadding = 0.dp
                ) {
                    types.forEach { type ->
                        Tab(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            text = {
                                Text(
                                    text = type,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedType == type) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }

        if (filteredLogs.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    title = "No Activity Logs",
                    subtitle = "System activity events will be recorded here automatically.",
                    icon = Icons.Default.ManageHistory
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredLogs, key = { it.id }) { log ->
                    val icon = when {
                        log.actionType.contains("Login", true) -> Icons.Default.Login
                        log.actionType.contains("Logout", true) -> Icons.Default.Logout
                        log.actionType.contains("Attendance", true) -> Icons.Default.FactCheck
                        log.actionType.contains("Homework", true) -> Icons.Default.Assignment
                        log.actionType.contains("Result", true) -> Icons.Default.BarChart
                        log.actionType.contains("Notice", true) -> Icons.Default.Campaign
                        else -> Icons.Default.GroupAdd
                    }

                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = PrimaryBlue.copy(alpha = 0.12f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = log.actionType,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${log.actorName} (${log.actorRole})",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryDarkBlue
                                    )

                                    Text(
                                        text = log.formattedTime,
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFEFF6FF)
                                ) {
                                    Text(
                                        text = log.actionType,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = log.details,
                                    fontSize = 12.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
