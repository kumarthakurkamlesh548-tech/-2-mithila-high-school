package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.model.HomeworkEntity
import com.example.data.model.UserRole
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue
import com.example.ui.theme.SecondaryBlue

@Composable
fun HomeworkScreen(
    homeworkList: List<HomeworkEntity>,
    userRole: UserRole? = null,
    onAddHomework: (title: String, className: String, subject: String, desc: String, dueDate: String, driveUrl: String) -> Unit = { _, _, _, _, _, _ -> },
    onDeleteHomework: (Int) -> Unit = {}
) {
    val canManage = userRole == UserRole.SUPER_ADMIN || userRole == UserRole.ADMIN || userRole == UserRole.TEACHER
    var submittedHomeworkIds by remember { mutableStateOf(setOf<Int>()) }
    var pinnedIds by remember { mutableStateOf(setOf<Int>()) }
    var archivedIds by remember { mutableStateOf(setOf<Int>()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var snackbarMsg by remember { mutableStateOf("") }

    // Dialog state
    var title by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("Class 10") }
    var subject by remember { mutableStateOf("Mathematics") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("05 Aug 2026") }
    var driveUrl by remember { mutableStateOf("") }

    val visibleList = remember(homeworkList, archivedIds) {
        homeworkList.filter { !archivedIds.contains(it.id) }
            .sortedByDescending { pinnedIds.contains(it.id) }
    }

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
                        text = "Daily Homework & Assignments",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkBlue
                    )
                    Text(
                        text = "Class Task Tracker & Online Homework Submission",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            if (visibleList.isEmpty()) {
                item {
                    GlassmorphicCard {
                        Text(text = "No active homework assigned currently.", fontSize = 13.sp, color = Color(0xFF64748B))
                    }
                }
            } else {
                items(visibleList) { hw ->
                    val isSubmitted = submittedHomeworkIds.contains(hw.id)
                    val isPinned = pinnedIds.contains(hw.id)

                    GlassmorphicCard(cornerRadius = 20.dp) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Assignment,
                                        contentDescription = "HW",
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = hw.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryDarkBlue
                                    )
                                    if (isPinned) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = Color(0xFFFEF3C7),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "PINNED",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFD97706),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Surface(
                                    color = if (isSubmitted) Color(0xFF10B981).copy(alpha = 0.15f) else SecondaryBlue,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = if (isSubmitted) "Submitted" else "Pending",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSubmitted) Color(0xFF10B981) else PrimaryDarkBlue,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = hw.description,
                                fontSize = 12.sp,
                                color = Color(0xFF334155),
                                lineHeight = 18.sp
                            )

                            if (hw.driveUrl.isNotBlank()) {
                                val context = androidx.compose.ui.platform.LocalContext.current
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        val url = hw.driveUrl.trim()
                                        if (url.startsWith("http://") || url.startsWith("https://")) {
                                            try {
                                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                snackbarMsg = "Unable to open Drive link"
                                            }
                                        } else {
                                            snackbarMsg = "Drive Link: $url"
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.FileUpload, contentDescription = "Drive Link", tint = PrimaryBlue)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Open Google Drive Reference File", fontSize = 12.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Subject: ${hw.subject} (${hw.className})", fontSize = 11.sp, color = Color(0xFF64748B))
                                Text(text = "Due: ${hw.dueDate}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (canManage) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            pinnedIds = if (isPinned) pinnedIds - hw.id else pinnedIds + hw.id
                                            snackbarMsg = if (isPinned) "Unpinned homework" else "Homework pinned to top"
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PushPin,
                                            contentDescription = "Pin",
                                            tint = if (isPinned) Color(0xFFD97706) else Color.Gray
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            archivedIds = archivedIds + hw.id
                                            snackbarMsg = "Homework archived"
                                        }
                                    ) {
                                        Icon(Icons.Default.Archive, contentDescription = "Archive", tint = Color.Gray)
                                    }
                                    IconButton(
                                        onClick = {
                                            onDeleteHomework(hw.id)
                                            snackbarMsg = "Homework deleted successfully"
                                        }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                                    }
                                }
                            } else {
                                if (!isSubmitted) {
                                    Button(
                                        onClick = {
                                            submittedHomeworkIds = submittedHomeworkIds + hw.id
                                            snackbarMsg = "Homework '${hw.title}' marked as completed!"
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Complete")
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Mark Completed", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = {},
                                        enabled = false,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Done", tint = Color(0xFF10B981))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Completed", fontSize = 12.sp, color = Color(0xFF10B981))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Show FAB ONLY for SUPER_ADMIN, ADMIN, TEACHER
        if (canManage) {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Assign Homework")
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Assign New Homework", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Homework Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = className,
                            onValueChange = { className = it },
                            label = { Text("Class (e.g. Class 10)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it },
                            label = { Text("Subject") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description / Task Details") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                        OutlinedTextField(
                            value = dueDate,
                            onValueChange = { dueDate = it },
                            label = { Text("Due Date") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = driveUrl,
                            onValueChange = { driveUrl = it },
                            label = { Text("Google Drive File Link (Optional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (title.isNotBlank() && description.isNotBlank()) {
                                onAddHomework(title, className, subject, description, dueDate, driveUrl)
                                showAddDialog = false
                                title = ""
                                description = ""
                                driveUrl = ""
                                snackbarMsg = "Homework assigned successfully!"
                            }
                        }
                    ) {
                        Text("Assign")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
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
