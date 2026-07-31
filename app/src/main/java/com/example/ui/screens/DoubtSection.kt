package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DoubtEntity
import com.example.data.model.UserRole
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue
import com.example.ui.theme.SecondaryBlue
import com.example.util.ContentModerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoubtSection(
    doubts: List<DoubtEntity>,
    userRole: UserRole?,
    currentUserId: String = "",
    currentUserName: String = "",
    onAskDoubt: (subject: String, question: String) -> Unit,
    onReplyDoubt: (doubtId: Int, replyText: String) -> Unit,
    onUpdateStatus: (doubtId: Int, newStatus: String) -> Unit = { _, _ -> }
) {
    var subjectInput by remember { mutableStateOf("Mathematics") }
    var questionInput by remember { mutableStateOf("") }
    var replyInputs by remember { mutableStateOf(mutableMapOf<Int, String>()) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedSubjectFilter by remember { mutableStateOf("All Subjects") }
    var selectedClassFilter by remember { mutableStateOf("All Classes") }
    var selectedStatusFilter by remember { mutableStateOf("All Statuses") }

    var snackbarMsg by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }

    val subjectsList = listOf("All Subjects", "Mathematics", "Science", "Physics", "Chemistry", "Biology", "English", "Hindi", "Social Science")
    val classesList = listOf("All Classes", "Class 9", "Class 10", "Class 11", "Class 12")
    val statusesList = listOf("All Statuses", "Open", "In Progress", "Solved")

    // Role-based visibility filtering:
    // Students can view ONLY their own doubts and replies.
    // Admins, Teachers, Super Admin can view ALL doubts.
    val roleFilteredDoubts = remember(doubts, userRole, currentUserId, currentUserName) {
        if (userRole == UserRole.STUDENT) {
            doubts.filter {
                (currentUserId.isNotBlank() && it.studentId == currentUserId) ||
                (currentUserName.isNotBlank() && it.studentName.equals(currentUserName, ignoreCase = true))
            }
        } else {
            doubts
        }
    }

    // Apply search and dropdown filters
    val filteredDoubts = remember(
        roleFilteredDoubts, searchQuery, selectedSubjectFilter, selectedClassFilter, selectedStatusFilter
    ) {
        roleFilteredDoubts.filter { doubt ->
            val matchesSearch = searchQuery.isBlank() ||
                    doubt.question.contains(searchQuery, ignoreCase = true) ||
                    doubt.subject.contains(searchQuery, ignoreCase = true) ||
                    doubt.studentName.contains(searchQuery, ignoreCase = true)

            val matchesSubject = selectedSubjectFilter == "All Subjects" ||
                    doubt.subject.equals(selectedSubjectFilter, ignoreCase = true)

            val matchesClass = selectedClassFilter == "All Classes" ||
                    doubt.className.equals(selectedClassFilter, ignoreCase = true)

            val matchesStatus = selectedStatusFilter == "All Statuses" ||
                    doubt.status.equals(selectedStatusFilter, ignoreCase = true) ||
                    (selectedStatusFilter == "Open" && (doubt.status == "Pending" || doubt.status == "Open")) ||
                    (selectedStatusFilter == "Solved" && (doubt.status == "Answered" || doubt.status == "Solved"))

            matchesSearch && matchesSubject && matchesClass && matchesStatus
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Academic Doubts & QnA",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDarkBlue
                            )
                            Text(
                                text = if (userRole == UserRole.STUDENT) "Post academic questions and view official replies" else "Manage and answer student academic queries",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        if (userRole == UserRole.STUDENT) {
                            Button(
                                onClick = { showCreateDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Ask", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ask Doubt", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Academic notice bar
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.School, contentDescription = "Academic", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Academic queries only. All posts are moderated for standard school language.",
                                fontSize = 11.sp,
                                color = PrimaryDarkBlue
                            )
                        }
                    }
                }
            }

            // Search & Filters Box
            item {
                GlassmorphicCard(cornerRadius = 16.dp) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = "Search & Filter Doubts", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)

                        // Search Input
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search by subject, doubt description...", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Subject Filter Chips
                        Text(text = "Filter Subject:", fontSize = 11.sp, color = Color.Gray)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(subjectsList) { subj ->
                                FilterChip(
                                    selected = selectedSubjectFilter == subj,
                                    onClick = { selectedSubjectFilter = subj },
                                    label = { Text(subj, fontSize = 11.sp) }
                                )
                            }
                        }

                        // Class Filter Chips
                        Text(text = "Filter Class:", fontSize = 11.sp, color = Color.Gray)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(classesList) { cls ->
                                FilterChip(
                                    selected = selectedClassFilter == cls,
                                    onClick = { selectedClassFilter = cls },
                                    label = { Text(cls, fontSize = 11.sp) }
                                )
                            }
                        }

                        // Status Filter Chips
                        Text(text = "Filter Status:", fontSize = 11.sp, color = Color.Gray)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(statusesList) { st ->
                                FilterChip(
                                    selected = selectedStatusFilter == st,
                                    onClick = { selectedStatusFilter = st },
                                    label = { Text(st, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            }

            // Quick Create Inline Card for Students if list empty or desired
            if (userRole == UserRole.STUDENT && doubts.isEmpty()) {
                item {
                    GlassmorphicCard(cornerRadius = 20.dp) {
                        Column {
                            Text(text = "Ask an Academic Doubt", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = subjectInput,
                                onValueChange = { subjectInput = it },
                                label = { Text("Subject (e.g. Mathematics, Science)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = questionInput,
                                onValueChange = { questionInput = it },
                                label = { Text("Describe your academic doubt...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    if (ContentModerator.containsProhibitedLanguage(questionInput) ||
                                        ContentModerator.containsProhibitedLanguage(subjectInput)) {
                                        snackbarMsg = ContentModerator.PROHIBITED_LANGUAGE_MESSAGE
                                    } else if (questionInput.isNotBlank()) {
                                        onAskDoubt(subjectInput, questionInput)
                                        questionInput = ""
                                        snackbarMsg = "Academic doubt posted successfully!"
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Submit Doubt", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Doubts Header Count
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (userRole == UserRole.STUDENT) "My Academic Doubts (${filteredDoubts.size})" else "All Student Doubts (${filteredDoubts.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkBlue
                    )
                }
            }

            if (filteredDoubts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.HelpOutline, contentDescription = "Empty", tint = Color.Gray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No academic doubts found.", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
            }

            items(filteredDoubts) { doubt ->
                val currentReplyText = replyInputs[doubt.id] ?: ""

                val displayStatus = when (doubt.status) {
                    "Answered" -> "Solved"
                    "Pending" -> "Open"
                    else -> doubt.status
                }

                val statusColor = when (displayStatus) {
                    "Solved" -> Color(0xFF10B981)
                    "In Progress" -> Color(0xFF3B82F6)
                    else -> Color(0xFFF59E0B)
                }

                GlassmorphicCard(cornerRadius = 20.dp) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = PrimaryBlue.copy(alpha = 0.1f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = doubt.studentName.take(1).uppercase(),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryBlue
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = doubt.studentName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                                    Text(text = "${doubt.subject} • ${doubt.className} • ${doubt.date}", fontSize = 10.sp, color = Color(0xFF64748B))
                                }
                            }

                            // Status Badge
                            Surface(
                                color = statusColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = displayStatus,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Doubt Question
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = doubt.question,
                                fontSize = 13.sp,
                                color = Color(0xFF1E293B),
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        // Answer Box if replied
                        if (doubt.replyText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.1f))
                                    .border(1.dp, Color(0xFF10B981).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Answer", tint = Color(0xFF047857), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Official Answer by ${if (doubt.repliedBy.isNotBlank()) doubt.repliedBy else "Teacher/Admin"}:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF047857)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = doubt.replyText, fontSize = 12.sp, color = Color(0xFF065F46))
                            }
                        }

                        // Admin / Teacher / Super Admin Moderation & Reply Options
                        if (userRole == UserRole.TEACHER || userRole == UserRole.ADMIN || userRole == UserRole.SUPER_ADMIN) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SecondaryBlue.copy(alpha = 0.3f))
                                    .padding(10.dp)
                            ) {
                                Text(text = "Admin & Teacher Moderation Portal", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)

                                Spacer(modifier = Modifier.height(6.dp))

                                // Status controls
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "Set Status:", fontSize = 10.sp, color = Color.Gray)
                                    AssistChip(
                                        onClick = { onUpdateStatus(doubt.id, "Open") },
                                        label = { Text("Open", fontSize = 10.sp) },
                                        colors = AssistChipDefaults.assistChipColors(containerColor = if (displayStatus == "Open") Color(0xFFF59E0B).copy(alpha = 0.3f) else Color.White)
                                    )
                                    AssistChip(
                                        onClick = { onUpdateStatus(doubt.id, "In Progress") },
                                        label = { Text("In Progress", fontSize = 10.sp) },
                                        colors = AssistChipDefaults.assistChipColors(containerColor = if (displayStatus == "In Progress") Color(0xFF3B82F6).copy(alpha = 0.3f) else Color.White)
                                    )
                                    AssistChip(
                                        onClick = { onUpdateStatus(doubt.id, "Solved") },
                                        label = { Text("Solved", fontSize = 10.sp) },
                                        colors = AssistChipDefaults.assistChipColors(containerColor = if (displayStatus == "Solved") Color(0xFF10B981).copy(alpha = 0.3f) else Color.White)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = currentReplyText,
                                    onValueChange = { text ->
                                        replyInputs = replyInputs.toMutableMap().apply { put(doubt.id, text) }
                                    },
                                    placeholder = { Text("Write official academic response...", fontSize = 12.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Button(
                                    onClick = {
                                        if (ContentModerator.containsProhibitedLanguage(currentReplyText)) {
                                            snackbarMsg = ContentModerator.PROHIBITED_LANGUAGE_MESSAGE
                                        } else if (currentReplyText.isNotBlank()) {
                                            onReplyDoubt(doubt.id, currentReplyText)
                                            onUpdateStatus(doubt.id, "Solved")
                                            replyInputs = replyInputs.toMutableMap().apply { remove(doubt.id) }
                                            snackbarMsg = "Answer submitted & marked Solved!"
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = "Reply", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Post Solution", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Student Ask Doubt Floating Dialog
        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Post Academic Doubt", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = subjectInput,
                            onValueChange = { subjectInput = it },
                            label = { Text("Subject (e.g. Science, Maths)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = questionInput,
                            onValueChange = { questionInput = it },
                            label = { Text("Question Detail") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (ContentModerator.containsProhibitedLanguage(questionInput) ||
                                ContentModerator.containsProhibitedLanguage(subjectInput)) {
                                snackbarMsg = ContentModerator.PROHIBITED_LANGUAGE_MESSAGE
                            } else if (questionInput.isNotBlank()) {
                                onAskDoubt(subjectInput, questionInput)
                                questionInput = ""
                                showCreateDialog = false
                                snackbarMsg = "Academic doubt submitted!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("Post Question")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
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
