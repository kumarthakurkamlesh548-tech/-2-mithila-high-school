package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import com.example.data.model.ResultEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue
import com.example.ui.theme.SecondaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    results: List<ResultEntity>,
    currentUser: UserEntity? = null,
    onSearchExact: (String, String, String, String, (ResultEntity?, String?) -> Unit) -> Unit = { _, _, _, _, _ -> },
    onSaveResult: (ResultEntity, (Boolean, String) -> Unit) -> Unit = { _, _ -> },
    onTogglePublish: (ResultEntity, Boolean) -> Unit = { _, _ -> },
    onDeleteResult: (ResultEntity) -> Unit = {}
) {
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf(0) } // 0 = Public Search, 1 = Admin Management

    val isAdmin = currentUser?.role == UserRole.ADMIN ||
            currentUser?.role == UserRole.SUPER_ADMIN ||
            (currentUser?.permissions?.manageResults == true)

    // Public Search State
    var searchClass by remember { mutableStateOf("Class 10") }
    var searchStream by remember { mutableStateOf("Science") }
    var searchExam by remember { mutableStateOf("Annual Examination") }
    var searchRollNumber by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var searchedResult by remember { mutableStateOf<ResultEntity?>(null) }
    var searchErrorMessage by remember { mutableStateOf<String?>(null) }

    // Dropdown expanded states
    var classExpanded by remember { mutableStateOf(false) }
    var streamExpanded by remember { mutableStateOf(false) }
    var examExpanded by remember { mutableStateOf(false) }

    // Official Marksheet Dialog / Viewer State
    var activeMarksheetModal by remember { mutableStateOf<ResultEntity?>(null) }

    // Admin Upload / Edit Dialog State
    var showAddResultModal by remember { mutableStateOf(false) }
    var editingResult by remember { mutableStateOf<ResultEntity?>(null) }

    // Admin Filters
    var adminFilterClass by remember { mutableStateOf("All") }
    var adminFilterExam by remember { mutableStateOf("All") }

    val classesList = listOf("Class 9", "Class 10", "Class 11", "Class 12")
    val streamsList = listOf("Science", "Commerce", "Arts")
    val examsList = listOf(
        "Annual Examination",
        "Half Yearly",
        "Unit Test",
        "Monthly Test",
        "Pre Board",
        "Board Examination"
    )

    var snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf("") }

    LaunchedEffect(snackbarMessage) {
        if (snackbarMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(snackbarMessage)
            snackbarMessage = ""
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Tabs (Public Search vs Admin Management)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (activeTab == 0) PrimaryBlue else Color.Transparent)
                        .clickable { activeTab = 0 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (activeTab == 0) Color.White else PrimaryDarkBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Result Search",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == 0) Color.White else PrimaryDarkBlue
                        )
                    }
                }

                if (isAdmin) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (activeTab == 1) PrimaryBlue else Color.Transparent)
                            .clickable { activeTab = 1 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin",
                                tint = if (activeTab == 1) Color.White else PrimaryDarkBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Admin Portal",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeTab == 1) Color.White else PrimaryDarkBlue
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (activeTab == 0) {
                // =====================================
                // PUBLIC RESULT SEARCH PAGE
                // =====================================
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        GlassmorphicCard(cornerRadius = 24.dp) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = PrimaryBlue.copy(alpha = 0.15f),
                                        shape = CircleShape,
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.School,
                                            contentDescription = "Official Results",
                                            tint = PrimaryBlue,
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .fillMaxSize()
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Official Result Portal",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryDarkBlue
                                        )
                                        Text(
                                            text = "+2 Govt Mithila High School Balaur (BSEB)",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                // 1. CLASS SELECTION *
                                Text(
                                    text = "1. Select Class *",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryDarkBlue
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                ExposedDropdownMenuBox(
                                    expanded = classExpanded,
                                    onExpandedChange = { classExpanded = !classExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = searchClass,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryBlue,
                                            unfocusedBorderColor = GlassBorder
                                        )
                                    )
                                    ExposedDropdownMenu(
                                        expanded = classExpanded,
                                        onDismissRequest = { classExpanded = false }
                                    ) {
                                        classesList.forEach { cls ->
                                            DropdownMenuItem(
                                                text = { Text(cls, fontWeight = FontWeight.SemiBold) },
                                                onClick = {
                                                    searchClass = cls
                                                    classExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                // 2. STREAM SELECTION * (Visible ONLY for Class 11 and Class 12)
                                if (searchClass == "Class 11" || searchClass == "Class 12") {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = "Stream * (Class 11 & 12)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryDarkBlue
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    ExposedDropdownMenuBox(
                                        expanded = streamExpanded,
                                        onExpandedChange = { streamExpanded = !streamExpanded }
                                    ) {
                                        OutlinedTextField(
                                            value = searchStream,
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = streamExpanded) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor(),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = PrimaryBlue,
                                                unfocusedBorderColor = GlassBorder
                                            )
                                        )
                                        ExposedDropdownMenu(
                                            expanded = streamExpanded,
                                            onDismissRequest = { streamExpanded = false }
                                        ) {
                                            streamsList.forEach { st ->
                                                DropdownMenuItem(
                                                    text = { Text(st, fontWeight = FontWeight.SemiBold) },
                                                    onClick = {
                                                        searchStream = st
                                                        streamExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // 3. EXAM SELECTION *
                                Text(
                                    text = "2. Select Examination *",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryDarkBlue
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                ExposedDropdownMenuBox(
                                    expanded = examExpanded,
                                    onExpandedChange = { examExpanded = !examExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = searchExam,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = examExpanded) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryBlue,
                                            unfocusedBorderColor = GlassBorder
                                        )
                                    )
                                    ExposedDropdownMenu(
                                        expanded = examExpanded,
                                        onDismissRequest = { examExpanded = false }
                                    ) {
                                        examsList.forEach { ex ->
                                            DropdownMenuItem(
                                                text = { Text(ex, fontWeight = FontWeight.SemiBold) },
                                                onClick = {
                                                    searchExam = ex
                                                    examExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // 4. ROLL NUMBER *
                                Text(
                                    text = "3. Enter Roll Number *",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryDarkBlue
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = searchRollNumber,
                                    onValueChange = { searchRollNumber = it },
                                    placeholder = { Text("e.g. 10042") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Badge,
                                            contentDescription = null,
                                            tint = PrimaryBlue
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryBlue,
                                        unfocusedBorderColor = GlassBorder
                                    )
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                // SEARCH BUTTON
                                Button(
                                    onClick = {
                                        if (searchRollNumber.isBlank()) {
                                            snackbarMessage = "Please enter student Roll Number"
                                            return@Button
                                        }
                                        isSearching = true
                                        searchedResult = null
                                        searchErrorMessage = null

                                        val activeStream = if (searchClass == "Class 11" || searchClass == "Class 12") searchStream else ""

                                        onSearchExact(searchClass, activeStream, searchExam, searchRollNumber) { match, err ->
                                            isSearching = false
                                            if (match != null) {
                                                searchedResult = match
                                                searchErrorMessage = null
                                            } else {
                                                // Check local results list as extra fallback
                                                val localMatch = results.firstOrNull { res ->
                                                    res.className == searchClass &&
                                                            res.examName == searchExam &&
                                                            res.rollNumber.trim() == searchRollNumber.trim() &&
                                                            res.isPublished &&
                                                            (activeStream.isBlank() || res.stream.equals(activeStream, ignoreCase = true))
                                                }
                                                if (localMatch != null) {
                                                    searchedResult = localMatch
                                                    searchErrorMessage = null
                                                } else {
                                                    searchedResult = null
                                                    searchErrorMessage = "No Result Found."
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                    contentPadding = PaddingValues(vertical = 14.dp)
                                ) {
                                    if (isSearching) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Searching Official Records...", fontWeight = FontWeight.Bold)
                                    } else {
                                        Icon(Icons.Default.Search, contentDescription = "Search")
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Search Result", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // SEARCH RESULT DISPLAY
                    if (isSearching) {
                        item {
                            ResultSearchSkeletonCard()
                        }
                    } else if (searchErrorMessage != null) {
                        item {
                            GlassmorphicCard(
                                cornerRadius = 20.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Cancel,
                                        contentDescription = "Not Found",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No Result Found.",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEF4444)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Please verify your Class, Exam Name, and Roll Number.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else if (searchedResult != null) {
                        val match = searchedResult!!
                        item {
                            GlassmorphicCard(cornerRadius = 24.dp) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = match.studentName,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryDarkBlue
                                            )
                                            Text(
                                                text = "Roll No: ${match.rollNumber} | ${match.className}${if (match.stream.isNotBlank()) " (${match.stream})" else ""}",
                                                fontSize = 12.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }

                                        Surface(
                                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Verified",
                                                    tint = Color(0xFF10B981),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "OFFICIAL RESULT",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF047857)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(SecondaryBlue.copy(alpha = 0.4f))
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = match.examName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryDarkBlue
                                        )
                                        Text(
                                            text = "Academic Session: ${match.academicSession}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Divider(color = GlassBorder)
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text("Grand Total", fontSize = 11.sp, color = Color(0xFF64748B))
                                                Text("${match.totalMarks} / ${match.maxMarks}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                            }
                                            Column {
                                                Text("Percentage", fontSize = 11.sp, color = Color(0xFF64748B))
                                                Text("${String.format("%.2f", match.percentage)}%", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                                            }
                                            Column {
                                                Text("Division", fontSize = 11.sp, color = Color(0xFF64748B))
                                                Text(match.remarks, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = { activeMarksheetModal = match },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                    ) {
                                        Icon(Icons.Default.Visibility, contentDescription = "View")
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Open Official Marksheet PDF", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (isAdmin && activeTab == 1) {
                // =====================================
                // ADMIN RESULT MANAGEMENT PORTAL
                // =====================================
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Result Management",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryDarkBlue
                                )
                                Text(
                                    text = "Upload, Publish & Manage School Marksheets",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Button(
                                onClick = {
                                    editingResult = null
                                    showAddResultModal = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Upload Result", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // FILTER BAR
                    item {
                        GlassmorphicCard(cornerRadius = 16.dp) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = adminFilterClass == "All",
                                    onClick = { adminFilterClass = "All" },
                                    label = { Text("All Classes", fontSize = 11.sp) }
                                )
                                classesList.forEach { cls ->
                                    FilterChip(
                                        selected = adminFilterClass == cls,
                                        onClick = { adminFilterClass = cls },
                                        label = { Text(cls, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }
                    }

                    val filteredAdminResults = results.filter {
                        (adminFilterClass == "All" || it.className == adminFilterClass)
                    }

                    if (filteredAdminResults.isEmpty()) {
                        item {
                            GlassmorphicCard {
                                Text(
                                    text = "No result records uploaded for $adminFilterClass.",
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    } else {
                        items(filteredAdminResults) { res ->
                            GlassmorphicCard(cornerRadius = 20.dp) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column {
                                            Text(
                                                text = res.studentName,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryDarkBlue
                                            )
                                            Text(
                                                text = "Roll No: ${res.rollNumber} • ${res.className}${if (res.stream.isNotBlank()) " (${res.stream})" else ""}",
                                                fontSize = 12.sp,
                                                color = Color(0xFF64748B)
                                            )
                                            Text(
                                                text = "${res.examName} (${res.academicSession})",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = PrimaryBlue
                                            )
                                        }

                                        // PUBLISH STATUS TOGGLE
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (res.isPublished) "Published" else "Unpublished",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (res.isPublished) Color(0xFF10B981) else Color(0xFFF59E0B)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Switch(
                                                checked = res.isPublished,
                                                onCheckedChange = { isChecked ->
                                                    onTogglePublish(res, isChecked)
                                                    snackbarMessage = if (isChecked) "Result published to students!" else "Result unpublished!"
                                                },
                                                modifier = Modifier.scale(0.8f)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Aggregate Marks: ${res.totalMarks}/${res.maxMarks}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Text("Upload Date: ${res.uploadDate}", fontSize = 11.sp, color = Color(0xFF64748B))
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Divider(color = GlassBorder)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // ADMIN ACTION BUTTONS
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { activeMarksheetModal = res },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Preview", fontSize = 11.sp)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                editingResult = res
                                                showAddResultModal = true
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Edit / Replace", fontSize = 11.sp)
                                        }

                                        IconButton(
                                            onClick = {
                                                onDeleteResult(res)
                                                snackbarMessage = "Result deleted successfully."
                                            }
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // =====================================
        // OFFICIAL MARKSHEET VIEWER MODAL
        // =====================================
        activeMarksheetModal?.let { res ->
            OfficialMarksheetDialog(
                result = res,
                onDismiss = { activeMarksheetModal = null },
                onToast = { msg -> snackbarMessage = msg }
            )
        }

        // =====================================
        // ADMIN ADD / EDIT RESULT MODAL
        // =====================================
        if (showAddResultModal) {
            AddEditResultDialog(
                initialResult = editingResult,
                onDismiss = { showAddResultModal = false },
                onSave = { newRes ->
                    onSaveResult(newRes) { success, msg ->
                        snackbarMessage = msg
                        showAddResultModal = false
                    }
                }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

/**
 * Official School Marksheet Viewer Dialog
 */
@Composable
fun OfficialMarksheetDialog(
    result: ResultEntity,
    onDismiss: () -> Unit,
    onToast: (String) -> Unit
) {
    var zoomScale by remember { mutableStateOf(1.0f) }
    var isFullscreen by remember { mutableStateOf(false) }
    var isPdfLoading by remember { mutableStateOf(true) }

    LaunchedEffect(result) {
        isPdfLoading = true
        delay(750)
        isPdfLoading = false
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = !isFullscreen)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(if (isFullscreen) 1.0f else 0.95f)
                .fillMaxHeight(if (isFullscreen) 1.0f else 0.90f)
                .clip(RoundedCornerShape(20.dp)),
            color = Color.White
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // TOOLBAR / CONTROLS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryDarkBlue)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "PDF",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPdfLoading) "Loading Result PDF..." else "Official Result PDF",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // ZOOM OUT
                        IconButton(onClick = { if (zoomScale > 0.8f) zoomScale -= 0.15f }) {
                            Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = Color.White)
                        }
                        // ZOOM IN
                        IconButton(onClick = { if (zoomScale < 1.6f) zoomScale += 0.15f }) {
                            Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = Color.White)
                        }
                        // FULLSCREEN TOGGLE
                        IconButton(onClick = { isFullscreen = !isFullscreen }) {
                            Icon(
                                imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = "Fullscreen",
                                tint = Color.White
                            )
                        }
                        // CLOSE
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // SCROLLABLE OFFICIAL MARKSHEET CONTENT
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9))
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (isPdfLoading) {
                        PdfPreviewSkeleton(scale = zoomScale)
                    } else {
                        Card(
                            modifier = Modifier
                                .padding(16.dp)
                                .scale(zoomScale)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(2.dp, PrimaryDarkBlue, RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        ) {
                            // OFFICIAL SCHOOL HEADER
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = "Logo",
                                    tint = PrimaryDarkBlue,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "+2 GOVT MITHILA HIGH SCHOOL",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PrimaryDarkBlue,
                                        fontFamily = FontFamily.Serif
                                    )
                                    Text(
                                        text = "Balaur, Manigachhi, Darbhanga, Bihar - 847422",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF475569)
                                    )
                                    Text(
                                        text = "BIHAR SCHOOL EXAMINATION BOARD (BSEB) PATNA",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = PrimaryDarkBlue, thickness = 2.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "OFFICIAL STATEMENT OF MARKS",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDarkBlue,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "${result.examName.uppercase()} (${result.academicSession})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF64748B),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // CANDIDATE DETAILS GRID
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .padding(10.dp)
                            ) {
                                CandidateDetailRow("Student Name:", result.studentName.uppercase())
                                CandidateDetailRow("Roll Number:", result.rollNumber)
                                if (result.registrationNumber.isNotBlank()) {
                                    CandidateDetailRow("Registration No:", result.registrationNumber)
                                }
                                CandidateDetailRow("Class & Section:", "${result.className} - Section ${result.section}")
                                if (result.stream.isNotBlank()) {
                                    CandidateDetailRow("Stream:", result.stream)
                                }
                                CandidateDetailRow("Academic Session:", result.academicSession)
                                CandidateDetailRow("Issue Date:", result.uploadDate)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // MARKS TABLE
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFF94A3B8), RoundedCornerShape(6.dp))
                            ) {
                                // Table Header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(PrimaryDarkBlue)
                                        .padding(vertical = 6.dp, horizontal = 8.dp)
                                ) {
                                    Text("Subject", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(2f))
                                    Text("Max", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
                                    Text("Pass", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
                                    Text("Obtained", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                }

                                MarkTableRow("Mathematics", 100, 30, result.mathMarks)
                                MarkTableRow("Science / Physics", 100, 30, result.scienceMarks)
                                MarkTableRow("Social Science / Chem", 100, 30, result.socialScienceMarks)
                                MarkTableRow("Hindi", 100, 30, result.hindiMarks)
                                MarkTableRow("English", 100, 30, result.englishMarks)
                                MarkTableRow("Maithili / Optional", 100, 30, result.maithiliMarks)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // AGGREGATE SUMMARY BOX
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SecondaryBlue.copy(alpha = 0.5f))
                                    .border(1.dp, PrimaryBlue.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Grand Total: ${result.totalMarks} / ${result.maxMarks}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                                    Text("Percentage: ${String.format("%.2f", result.percentage)}%", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
                                }

                                Surface(
                                    color = Color(0xFF10B981),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = result.remarks.uppercase(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // OFFICIAL SEAL & SIGNATURE FOOTER
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .border(2.dp, PrimaryDarkBlue, CircleShape)
                                            .padding(4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("OFFICIAL\nSEAL", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue, textAlign = TextAlign.Center)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Controller of Exams", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Dr. R. K. Jha", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Cursive, color = PrimaryDarkBlue)
                                    Divider(modifier = Modifier.width(90.dp), color = PrimaryDarkBlue, thickness = 1.dp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Principal Signature", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                                }
                            }
                        }
                    }
                }
            }

                // BOTTOM ACTION BAR (PRINT, DOWNLOAD, SHARE)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onToast("Preparing official print preview...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = "Print", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Print", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { onToast("Official Marksheet PDF downloaded successfully!") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Download", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Download PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = { onToast("Result link copied to clipboard!") }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = PrimaryDarkBlue)
                    }
                }
            }
        }
    }
}

@Composable
fun CandidateDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
    }
}

@Composable
fun MarkTableRow(subject: String, max: Int, pass: Int, obtained: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, Color(0xFFE2E8F0))
            .padding(vertical = 5.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(subject, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155), modifier = Modifier.weight(2f))
        Text("$max", fontSize = 10.sp, color = Color(0xFF64748B), modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
        Text("$pass", fontSize = 10.sp, color = Color(0xFF64748B), modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
        Text("$obtained", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
    }
}

/**
 * Admin Upload / Edit Result Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditResultDialog(
    initialResult: ResultEntity? = null,
    onDismiss: () -> Unit,
    onSave: (ResultEntity) -> Unit
) {
    var studentName by remember { mutableStateOf(initialResult?.studentName ?: "") }
    var rollNumber by remember { mutableStateOf(initialResult?.rollNumber ?: "") }
    var regNumber by remember { mutableStateOf(initialResult?.registrationNumber ?: "") }
    var className by remember { mutableStateOf(initialResult?.className ?: "Class 10") }
    var section by remember { mutableStateOf(initialResult?.section ?: "A") }
    var stream by remember { mutableStateOf(initialResult?.stream ?: "Science") }
    var examName by remember { mutableStateOf(initialResult?.examName ?: "Annual Examination") }
    var session by remember { mutableStateOf(initialResult?.academicSession ?: "2025-2026") }
    var isPublished by remember { mutableStateOf(initialResult?.isPublished ?: true) }

    var mathMarks by remember { mutableStateOf(initialResult?.mathMarks?.toString() ?: "90") }
    var sciMarks by remember { mutableStateOf(initialResult?.scienceMarks?.toString() ?: "88") }
    var sstMarks by remember { mutableStateOf(initialResult?.socialScienceMarks?.toString() ?: "85") }
    var hindiMarks by remember { mutableStateOf(initialResult?.hindiMarks?.toString() ?: "82") }
    var engMarks by remember { mutableStateOf(initialResult?.englishMarks?.toString() ?: "80") }
    var maithiliMarks by remember { mutableStateOf(initialResult?.maithiliMarks?.toString() ?: "86") }

    var attachedFileName by remember { mutableStateOf(if (initialResult?.pdfUrl?.isNotBlank() == true) "Official_Marksheet.pdf" else "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (initialResult == null) "Upload Student Result" else "Edit / Replace Result",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkBlue
                )
                Text(
                    text = "Official record will be saved to Firebase and Room Database.",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = studentName,
                    onValueChange = { studentName = it },
                    label = { Text("Student Full Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = rollNumber,
                        onValueChange = { rollNumber = it },
                        label = { Text("Roll No *") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = regNumber,
                        onValueChange = { regNumber = it },
                        label = { Text("Reg No (Optional)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = className,
                        onValueChange = { className = it },
                        label = { Text("Class *") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = section,
                        onValueChange = { section = it },
                        label = { Text("Section") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (className == "Class 11" || className == "Class 12") {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = stream,
                        onValueChange = { stream = it },
                        label = { Text("Stream (Science/Commerce/Arts) *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = examName,
                    onValueChange = { examName = it },
                    label = { Text("Exam Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // PDF FILE ATTACHMENT SIMULATION
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SecondaryBlue.copy(alpha = 0.4f))
                        .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                        .clickable { attachedFileName = "${studentName.replace(" ", "_")}_Marksheet.pdf" }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FileUpload, contentDescription = "Upload", tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (attachedFileName.isNotBlank()) "Attached: $attachedFileName" else "Attach Official Result PDF File",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryDarkBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Subject Marks Breakdown", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = mathMarks, onValueChange = { mathMarks = it }, label = { Text("Math") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = sciMarks, onValueChange = { sciMarks = it }, label = { Text("Science") }, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = sstMarks, onValueChange = { sstMarks = it }, label = { Text("SST") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = hindiMarks, onValueChange = { hindiMarks = it }, label = { Text("Hindi") }, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = engMarks, onValueChange = { engMarks = it }, label = { Text("English") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = maithiliMarks, onValueChange = { maithiliMarks = it }, label = { Text("Maithili") }, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Publish Result Immediately", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrimaryDarkBlue)
                    Switch(checked = isPublished, onCheckedChange = { isPublished = it })
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val math = mathMarks.toIntOrNull() ?: 0
                            val sci = sciMarks.toIntOrNull() ?: 0
                            val sst = sstMarks.toIntOrNull() ?: 0
                            val hindi = hindiMarks.toIntOrNull() ?: 0
                            val eng = engMarks.toIntOrNull() ?: 0
                            val maithili = maithiliMarks.toIntOrNull() ?: 0
                            val total = math + sci + sst + hindi + eng + maithili
                            val pct = (total.toDouble() / 600.0) * 100.0

                            val entity = (initialResult ?: ResultEntity(studentName = studentName, rollNumber = rollNumber, className = className, examName = examName)).copy(
                                studentName = studentName,
                                rollNumber = rollNumber,
                                registrationNumber = regNumber,
                                className = className,
                                section = section,
                                stream = if (className == "Class 11" || className == "Class 12") stream else "",
                                examName = examName,
                                academicSession = session,
                                isPublished = isPublished,
                                pdfUrl = if (attachedFileName.isNotBlank()) "https://mithilahs.edu.in/results/$attachedFileName" else "",
                                mathMarks = math,
                                scienceMarks = sci,
                                socialScienceMarks = sst,
                                hindiMarks = hindi,
                                englishMarks = eng,
                                maithiliMarks = maithili,
                                totalMarks = total,
                                maxMarks = 600,
                                percentage = pct,
                                remarks = if (pct >= 80.0) "1st Division with Distinction" else if (pct >= 60.0) "1st Division" else "2nd Division"
                            )
                            onSave(entity)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("Save Result")
                    }
                }
            }
        }
    }
}

/**
 * Shimmer gradient brush for skeleton loading states
 */
@Composable
fun shimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1200f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color(0xFFE2E8F0),
            Color(0xFFF8FAFC),
            Color(0xFFCBD5E1),
            Color(0xFFF8FAFC),
            Color(0xFFE2E8F0)
        )
        val transition = rememberInfiniteTransition(label = "shimmer")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1100, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmerTranslate"
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

/**
 * Skeleton card for result search loading state
 */
@Composable
fun ResultSearchSkeletonCard() {
    val shimmer = shimmerBrush()
    GlassmorphicCard(cornerRadius = 24.dp) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(22.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(shimmer)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(shimmer)
                    )
                }

                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(shimmer)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmer)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmer)
                )

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Box(modifier = Modifier.width(70.dp).height(12.dp).clip(RoundedCornerShape(4.dp)).background(shimmer))
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(modifier = Modifier.width(85.dp).height(18.dp).clip(RoundedCornerShape(4.dp)).background(shimmer))
                    }
                    Column {
                        Box(modifier = Modifier.width(70.dp).height(12.dp).clip(RoundedCornerShape(4.dp)).background(shimmer))
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(modifier = Modifier.width(65.dp).height(18.dp).clip(RoundedCornerShape(4.dp)).background(shimmer))
                    }
                    Column {
                        Box(modifier = Modifier.width(60.dp).height(12.dp).clip(RoundedCornerShape(4.dp)).background(shimmer))
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(modifier = Modifier.width(90.dp).height(18.dp).clip(RoundedCornerShape(4.dp)).background(shimmer))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(shimmer)
            )
        }
    }
}

/**
 * Skeleton loading state for PDF Marksheet preview rendering
 */
@Composable
fun PdfPreviewSkeleton(scale: Float = 1.0f) {
    val shimmer = shimmerBrush()
    Card(
        modifier = Modifier
            .padding(16.dp)
            .scale(scale)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skeleton Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(shimmer)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.width(220.dp).height(18.dp).clip(RoundedCornerShape(4.dp)).background(shimmer))
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.width(180.dp).height(12.dp).clip(RoundedCornerShape(4.dp)).background(shimmer))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.width(150.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).background(shimmer))
            Spacer(modifier = Modifier.height(16.dp))

            // Student Info Skeleton Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                repeat(3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(modifier = Modifier.width(130.dp).height(14.dp).clip(RoundedCornerShape(4.dp)).background(shimmer))
                        Box(modifier = Modifier.width(130.dp).height(14.dp).clip(RoundedCornerShape(4.dp)).background(shimmer))
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Marks Table Skeleton
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp))
            ) {
                // Table Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .background(shimmer)
                )

                // Table Rows
                repeat(6) {
                    Divider(color = Color(0xFFE2E8F0))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.width(110.dp).height(14.dp).clip(RoundedCornerShape(4.dp)).background(shimmer))
                        Box(modifier = Modifier.width(40.dp).height(14.dp).clip(RoundedCornerShape(4.dp)).background(shimmer))
                        Box(modifier = Modifier.width(40.dp).height(14.dp).clip(RoundedCornerShape(4.dp)).background(shimmer))
                        Box(modifier = Modifier.width(50.dp).height(14.dp).clip(RoundedCornerShape(4.dp)).background(shimmer))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Grand Total Skeleton Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(shimmer)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Signatures & Stamp Skeleton
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(shimmer))
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.width(70.dp).height(12.dp).clip(RoundedCornerShape(4.dp)).background(shimmer))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.width(100.dp).height(36.dp).clip(RoundedCornerShape(4.dp)).background(shimmer))
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.width(90.dp).height(12.dp).clip(RoundedCornerShape(4.dp)).background(shimmer))
                }
            }
        }
    }
}
