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
import com.example.data.model.NotificationItemEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenterScreen(
    notifications: List<NotificationItemEntity>,
    onMarkRead: (Int) -> Unit,
    onClearAll: () -> Unit,
    onNavigate: (String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Homework", "Notice", "Results", "Events", "Chat", "Doubt")

    val filteredNotifications = remember(notifications, selectedFilter) {
        if (selectedFilter == "All") notifications
        else notifications.filter { it.type.contains(selectedFilter, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top Header Card
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Notification Center",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${notifications.count { !it.isRead }} unread updates",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    if (notifications.isNotEmpty()) {
                        TextButton(onClick = onClearAll) {
                            Text("Clear", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Tabs
                ScrollableTabRow(
                    selectedTabIndex = filters.indexOf(selectedFilter),
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    edgePadding = 0.dp
                ) {
                    filters.forEach { filter ->
                        Tab(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            text = {
                                Text(
                                    text = filter,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }

        // List Content
        if (filteredNotifications.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    title = "No Notifications",
                    subtitle = "You are all caught up! New homework, notices, and result alerts will appear here.",
                    icon = Icons.Default.NotificationsNone
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredNotifications, key = { it.id }) { notif ->
                    val icon = when {
                        notif.type.contains("Homework", true) -> Icons.Default.Assignment
                        notif.type.contains("Notice", true) -> Icons.Default.Campaign
                        notif.type.contains("Results", true) -> Icons.Default.BarChart
                        notif.type.contains("Events", true) -> Icons.Default.Event
                        notif.type.contains("Chat", true) -> Icons.Default.Forum
                        notif.type.contains("Doubt", true) -> Icons.Default.QuestionAnswer
                        else -> Icons.Default.Announcement
                    }

                    val badgeColor = when {
                        notif.type.contains("Homework", true) -> Color(0xFF0284C7)
                        notif.type.contains("Notice", true) -> Color(0xFFDC2626)
                        notif.type.contains("Results", true) -> Color(0xFF16A34A)
                        notif.type.contains("Events", true) -> Color(0xFFD97706)
                        notif.type.contains("Chat", true) -> Color(0xFF7C3AED)
                        else -> PrimaryBlue
                    }

                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            if (!notif.isRead) onMarkRead(notif.id)
                            when {
                                notif.type.contains("Homework", true) -> onNavigate("homework")
                                notif.type.contains("Notice", true) -> onNavigate("notice_board")
                                notif.type.contains("Results", true) -> onNavigate("results")
                                notif.type.contains("Events", true) -> onNavigate("events")
                                notif.type.contains("Chat", true) -> onNavigate("chat")
                                notif.type.contains("Doubt", true) -> onNavigate("doubt_section")
                                else -> {}
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = badgeColor.copy(alpha = 0.12f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = notif.type,
                                        tint = badgeColor,
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
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = badgeColor.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = notif.type,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = badgeColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text(
                                        text = notif.formattedTime,
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = notif.title,
                                    fontSize = 14.sp,
                                    fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.SemiBold,
                                    color = PrimaryDarkBlue
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = notif.message,
                                    fontSize = 12.sp,
                                    color = Color.DarkGray,
                                    maxLines = 2
                                )
                            }

                            if (!notif.isRead) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = PrimaryBlue,
                                    modifier = Modifier.size(8.dp)
                                ) {}
                            }
                        }
                    }
                }
            }
        }
    }
}
