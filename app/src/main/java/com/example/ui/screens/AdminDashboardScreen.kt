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
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue

data class AdminTile(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val isEnabled: (UserEntity?) -> Boolean = { true }
)

@Composable
fun AdminDashboardScreen(
    currentUser: UserEntity? = null,
    onNavigate: (String) -> Unit
) {
    val allAdminModules = listOf(
        AdminTile("Manage Notices", "notice_board", Icons.Default.Campaign) { user ->
            user == null || user.role == UserRole.SUPER_ADMIN || user.permissions.manageNotices
        },
        AdminTile("Manage Results", "results", Icons.Default.Assessment) { user ->
            user == null || user.role == UserRole.SUPER_ADMIN || user.permissions.manageResults
        },
        AdminTile("Manage Attendance", "attendance", Icons.Default.FactCheck) { user ->
            user == null || user.role == UserRole.SUPER_ADMIN || user.permissions.manageAttendance
        },
        AdminTile("Manage Study Material", "study_material", Icons.Default.MenuBook) { user ->
            user == null || user.role == UserRole.SUPER_ADMIN || user.permissions.manageStudyMaterial
        },
        AdminTile("Manage Homework", "homework", Icons.Default.Assignment) { user ->
            user == null || user.role == UserRole.SUPER_ADMIN || user.permissions.manageHomework
        },
        AdminTile("Manage Timetable", "timetable", Icons.Default.Schedule) { user ->
            user == null || user.role == UserRole.SUPER_ADMIN || user.permissions.manageTimetable
        },
        AdminTile("Manage Gallery", "gallery", Icons.Default.Collections) { user ->
            user == null || user.role == UserRole.SUPER_ADMIN || user.permissions.manageGallery
        },
        AdminTile("Manage Events", "events", Icons.Default.Event) { user ->
            user == null || user.role == UserRole.SUPER_ADMIN || user.permissions.manageEvents
        },
        AdminTile("Manage Downloads", "downloads", Icons.Default.Download) { user ->
            user == null || user.role == UserRole.SUPER_ADMIN || user.permissions.manageDownloads
        },
        AdminTile("Manage Syllabus", "syllabus", Icons.Default.Book) { user ->
            user == null || user.role == UserRole.SUPER_ADMIN || user.permissions.manageSyllabus
        },
        AdminTile("School Information", "about_school", Icons.Default.School),
        AdminTile("System Settings", "settings", Icons.Default.Settings)
    )

    // Filter modules based on assigned permissions
    val visibleModules = allAdminModules.filter { tile -> tile.isEnabled(currentUser) }

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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Admin Control Center", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                            Text(text = "Logged in as ${currentUser?.name ?: "Administrator"}", fontSize = 12.sp, color = Color(0xFF64748B))
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PrimaryBlue.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "ADMIN ROLE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricBox("Students", "1,240")
                        MetricBox("Teachers", "48")
                        MetricBox("Classes", "4 (9 to 12)")
                    }
                }
            }
        }

        item {
            Text(
                text = "Permitted Administrative Modules (${visibleModules.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkBlue
            )
        }

        if (visibleModules.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No administrative permissions are currently assigned to your account. Please contact Super Admin.",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    visibleModules.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            row.forEach { module ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(Color.White)
                                        .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
                                        .clickable { onNavigate(module.route) }
                                        .padding(14.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(PrimaryBlue.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(module.icon, contentDescription = module.title, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(text = module.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                                    }
                                }
                            }
                            if (row.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricBox(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B))
    }
}
