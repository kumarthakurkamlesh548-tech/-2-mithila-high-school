package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdminPermissions
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue
import com.example.ui.theme.SecondaryBlue

@Composable
fun SuperAdminDashboardScreen(
    users: List<UserEntity>,
    onNavigate: (String) -> Unit,
    onCreateAdmin: (name: String, email: String, password: String, permissions: AdminPermissions, onResult: (Boolean, String) -> Unit) -> Unit,
    onUpdatePermissions: (userId: String, permissions: AdminPermissions) -> Unit,
    onToggleStatus: (userId: String, isEnabled: Boolean) -> Unit,
    onUpdateRole: (userId: String, newRole: UserRole) -> Unit,
    onDeleteUser: (userId: String) -> Unit,
    onSendPasswordReset: (email: String, onResult: (Boolean, String) -> Unit) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Admin Mgmt, 1: All Users, 2: School Modules, 3: Analytics
    var showCreateAdminDialog by remember { mutableStateOf(false) }
    var selectedAdminForPermissions by remember { mutableStateOf<UserEntity?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf<UserRole?>(null) }

    val adminCount = users.count { it.role == UserRole.ADMIN }
    val teacherCount = users.count { it.role == UserRole.TEACHER }
    val studentCount = users.count { it.role == UserRole.STUDENT }

    val filteredUsers = users.filter { user ->
        val matchesQuery = user.name.contains(searchQuery, ignoreCase = true) ||
                user.email.contains(searchQuery, ignoreCase = true) ||
                user.role.name.contains(searchQuery, ignoreCase = true)
        val matchesRole = selectedRoleFilter == null || user.role == selectedRoleFilter
        matchesQuery && matchesRole
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Super Admin Header Card
        item {
            GlassmorphicCard(cornerRadius = 24.dp) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Security,
                                    contentDescription = "Super Admin",
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Super Admin Control Center",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryDarkBlue
                                )
                            }
                            Text(
                                text = "Root Level Application & Security Permissions",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFDC2626).copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "SUPER ADMIN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFDC2626),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricBox("Admins", adminCount.toString())
                        MetricBox("Teachers", teacherCount.toString())
                        MetricBox("Students", studentCount.toString())
                        MetricBox("System Status", "SECURE")
                    }
                }
            }
        }

        // Section Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SecondaryBlue)
                    .padding(4.dp)
            ) {
                listOf("Admin Mgmt", "All Users", "Modules", "Analytics").forEachIndexed { idx, label ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedTab == idx) PrimaryBlue else Color.Transparent)
                            .clickable { selectedTab = idx }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == idx) Color.White else PrimaryDarkBlue,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> { // Admin Management Tab
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Configured Admin Accounts",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkBlue
                        )

                        Button(
                            onClick = { showCreateAdminDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Admin", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Create Admin", fontSize = 12.sp)
                        }
                    }
                }

                val adminUsers = users.filter { it.role == UserRole.ADMIN }
                if (adminUsers.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("No Admin accounts found. Click 'Create Admin' to add one.", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    }
                } else {
                    items(adminUsers) { admin ->
                        AdminUserCard(
                            admin = admin,
                            onEditPermissions = { selectedAdminForPermissions = admin },
                            onToggleStatus = { onToggleStatus(admin.id, !admin.isEnabled) },
                            onDelete = { onDeleteUser(admin.id) },
                            onDemote = { onUpdateRole(admin.id, UserRole.TEACHER) }
                        )
                    }
                }
            }

            1 -> { // User Management Tab
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search by name, email or role...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FilterChip(
                                selected = selectedRoleFilter == null,
                                onClick = { selectedRoleFilter = null },
                                label = { Text("All Roles") }
                            )
                            UserRole.entries.forEach { role ->
                                FilterChip(
                                    selected = selectedRoleFilter == role,
                                    onClick = { selectedRoleFilter = role },
                                    label = { Text(role.name) }
                                )
                            }
                        }
                    }
                }

                items(filteredUsers) { user ->
                    UserManagementCard(
                        user = user,
                        onToggleStatus = { onToggleStatus(user.id, !user.isEnabled) },
                        onPromoteDemote = { newRole -> onUpdateRole(user.id, newRole) },
                        onSendReset = { onSendPasswordReset(user.email) { _, _ -> } },
                        onDelete = { onDeleteUser(user.id) }
                    )
                }
            }

            2 -> { // All School Management Modules Launcher
                item {
                    Text(
                        text = "Full System Administration Modules",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkBlue
                    )
                }

                item {
                    val allModules = listOf(
                        AdminTile("Manage Notices", "notice_board", Icons.Default.Campaign),
                        AdminTile("Manage Results", "results", Icons.Default.Assessment),
                        AdminTile("Manage Attendance", "attendance", Icons.Default.FactCheck),
                        AdminTile("Manage Study Material", "study_material", Icons.Default.MenuBook),
                        AdminTile("Manage Homework", "homework", Icons.Default.Assignment),
                        AdminTile("Manage Timetable", "timetable", Icons.Default.Schedule),
                        AdminTile("Manage Gallery", "gallery", Icons.Default.Collections),
                        AdminTile("Manage Events", "events", Icons.Default.Event),
                        AdminTile("Manage Downloads", "downloads", Icons.Default.Download),
                        AdminTile("Manage Syllabus", "syllabus", Icons.Default.Book),
                        AdminTile("Student Doubts & QnA", "doubt_section", Icons.Default.QuestionAnswer),
                        AdminTile("School Information", "about_school", Icons.Default.School),
                        AdminTile("System Settings", "settings", Icons.Default.Settings)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        allModules.chunked(2).forEach { row ->
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
                            }
                        }
                    }
                }
            }

            3 -> { // System Analytics
                item {
                    GlassmorphicCard(cornerRadius = 20.dp) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("System Analytics & Security Logs", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)

                            AnalyticsRow("Total Registered Accounts", users.size.toString())
                            AnalyticsRow("Active Admins", adminCount.toString())
                            AnalyticsRow("Active Teachers", teacherCount.toString())
                            AnalyticsRow("Enrolled Students", studentCount.toString())
                            AnalyticsRow("Database Rule Status", "FIRESTORE SECURED")
                            AnalyticsRow("Auth Provider", "FIREBASE AUTHENTICATION")
                        }
                    }
                }
            }
        }
    }

    // Create Admin Dialog
    if (showCreateAdminDialog) {
        CreateAdminDialog(
            onDismiss = { showCreateAdminDialog = false },
            onCreate = { name, email, pass, permissions ->
                onCreateAdmin(name, email, pass, permissions) { _, _ -> }
                showCreateAdminDialog = false
            }
        )
    }

    // Edit Permissions Dialog
    selectedAdminForPermissions?.let { admin ->
        EditAdminPermissionsDialog(
            admin = admin,
            onDismiss = { selectedAdminForPermissions = null },
            onSave = { updatedPermissions ->
                onUpdatePermissions(admin.id, updatedPermissions)
                selectedAdminForPermissions = null
            }
        )
    }
}

@Composable
fun AdminUserCard(
    admin: UserEntity,
    onEditPermissions: () -> Unit,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit,
    onDemote: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", tint = PrimaryBlue)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = admin.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                        Text(text = admin.email, fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                }

                Switch(
                    checked = admin.isEnabled,
                    onCheckedChange = { onToggleStatus() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEditPermissions,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Permissions", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onDemote,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Demote", fontSize = 11.sp, color = Color(0xFFD97706))
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                }
            }
        }
    }
}

@Composable
fun UserManagementCard(
    user: UserEntity,
    onToggleStatus: () -> Unit,
    onPromoteDemote: (UserRole) -> Unit,
    onSendReset: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = user.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when(user.role) {
                                UserRole.SUPER_ADMIN -> Color(0xFFDC2626).copy(alpha = 0.15f)
                                UserRole.ADMIN -> PrimaryBlue.copy(alpha = 0.15f)
                                UserRole.TEACHER -> Color(0xFF059669).copy(alpha = 0.15f)
                                UserRole.STUDENT -> Color(0xFF6366F1).copy(alpha = 0.15f)
                            }
                        ) {
                            Text(
                                text = user.role.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when(user.role) {
                                    UserRole.SUPER_ADMIN -> Color(0xFFDC2626)
                                    UserRole.ADMIN -> PrimaryBlue
                                    UserRole.TEACHER -> Color(0xFF059669)
                                    UserRole.STUDENT -> Color(0xFF6366F1)
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(text = user.email, fontSize = 12.sp, color = Color(0xFF64748B))
                }

                if (user.role != UserRole.SUPER_ADMIN) {
                    Switch(
                        checked = user.isEnabled,
                        onCheckedChange = { onToggleStatus() }
                    )
                }
            }

            if (user.role != UserRole.SUPER_ADMIN) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = onSendReset,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                    ) {
                        Text("Reset Pass", fontSize = 10.sp)
                    }

                    if (user.role != UserRole.ADMIN) {
                        Button(
                            onClick = { onPromoteDemote(UserRole.ADMIN) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                        ) {
                            Text("Make Admin", fontSize = 10.sp)
                        }
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CreateAdminDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, email: String, pass: String, permissions: AdminPermissions) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var permissions by remember { mutableStateOf(AdminPermissions()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Administrator", fontWeight = FontWeight.Bold, color = PrimaryDarkBlue) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Admin Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Temporary Password") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Assign Module Permissions:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                PermissionCheckboxes(
                    permissions = permissions,
                    onPermissionsChange = { permissions = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank()) {
                        onCreate(name, email, password, permissions)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Create Account")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditAdminPermissionsDialog(
    admin: UserEntity,
    onDismiss: () -> Unit,
    onSave: (AdminPermissions) -> Unit
) {
    var permissions by remember { mutableStateOf(admin.permissions) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permissions: ${admin.name}", fontWeight = FontWeight.Bold, color = PrimaryDarkBlue) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Enable or disable specific administrative modules for this account:", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(10.dp))
                PermissionCheckboxes(
                    permissions = permissions,
                    onPermissionsChange = { permissions = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(permissions) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PermissionCheckboxes(
    permissions: AdminPermissions,
    onPermissionsChange: (AdminPermissions) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        PermissionRow("Manage Results", permissions.manageResults) { onPermissionsChange(permissions.copy(manageResults = it)) }
        PermissionRow("Manage Notices", permissions.manageNotices) { onPermissionsChange(permissions.copy(manageNotices = it)) }
        PermissionRow("Manage Attendance", permissions.manageAttendance) { onPermissionsChange(permissions.copy(manageAttendance = it)) }
        PermissionRow("Manage Study Material", permissions.manageStudyMaterial) { onPermissionsChange(permissions.copy(manageStudyMaterial = it)) }
        PermissionRow("Manage Homework", permissions.manageHomework) { onPermissionsChange(permissions.copy(manageHomework = it)) }
        PermissionRow("Manage Timetable", permissions.manageTimetable) { onPermissionsChange(permissions.copy(manageTimetable = it)) }
        PermissionRow("Manage Students", permissions.manageStudents) { onPermissionsChange(permissions.copy(manageStudents = it)) }
        PermissionRow("Manage Teachers", permissions.manageTeachers) { onPermissionsChange(permissions.copy(manageTeachers = it)) }
        PermissionRow("Manage Gallery", permissions.manageGallery) { onPermissionsChange(permissions.copy(manageGallery = it)) }
        PermissionRow("Manage Events", permissions.manageEvents) { onPermissionsChange(permissions.copy(manageEvents = it)) }
        PermissionRow("Manage Downloads", permissions.manageDownloads) { onPermissionsChange(permissions.copy(manageDownloads = it)) }
        PermissionRow("Manage Syllabus", permissions.manageSyllabus) { onPermissionsChange(permissions.copy(manageSyllabus = it)) }
    }
}

@Composable
fun PermissionRow(label: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 2.dp)
    ) {
        Checkbox(checked = isChecked, onCheckedChange = onCheckedChange)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 12.sp, color = PrimaryDarkBlue)
    }
}

@Composable
fun AnalyticsRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = Color(0xFF64748B))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
    }
}
