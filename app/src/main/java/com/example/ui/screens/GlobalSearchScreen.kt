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
import com.example.data.model.*
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue

data class SearchResultItem(
    val title: String,
    val subtitle: String,
    val category: String, // Student, Teacher, Homework, Result, Study Material, Timetable, Event, Notice, Syllabus
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchScreen(
    users: List<UserEntity>,
    homeworkList: List<HomeworkEntity>,
    results: List<ResultEntity>,
    studyMaterials: List<StudyMaterialEntity>,
    timetables: List<TimetableEntity>,
    events: List<EventEntity>,
    notices: List<NoticeEntity>,
    syllabusList: List<SyllabusEntity>,
    onNavigate: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "People", "Homework", "Results", "Study Materials", "Notices", "Timetable", "Events", "Syllabus")

    val allSearchableItems = remember(users, homeworkList, results, studyMaterials, timetables, events, notices, syllabusList) {
        val list = mutableListOf<SearchResultItem>()

        // Users
        users.forEach { u ->
            list.add(
                SearchResultItem(
                    title = u.name,
                    subtitle = "${u.role.name} • ${if (u.className.isNotBlank()) u.className else u.email}",
                    category = if (u.role == UserRole.STUDENT) "Student" else if (u.role == UserRole.TEACHER) "Teacher" else "Admin",
                    route = "chat"
                )
            )
        }

        // Homework
        homeworkList.forEach { hw ->
            list.add(
                SearchResultItem(
                    title = hw.title,
                    subtitle = "${hw.className} • ${hw.subject} • Due: ${hw.dueDate}",
                    category = "Homework",
                    route = "homework"
                )
            )
        }

        // Results
        results.forEach { r ->
            list.add(
                SearchResultItem(
                    title = "${r.studentName} (${r.rollNumber})",
                    subtitle = "${r.className} • ${r.examName} • ${r.percentage}% (${r.grade})",
                    category = "Results",
                    route = "results"
                )
            )
        }

        // Study Material
        studyMaterials.forEach { sm ->
            list.add(
                SearchResultItem(
                    title = sm.title,
                    subtitle = "${sm.className} • ${sm.subject} • ${sm.type}",
                    category = "Study Materials",
                    route = "study_material"
                )
            )
        }

        // Timetables
        timetables.forEach { t ->
            list.add(
                SearchResultItem(
                    title = "${t.subject} - ${t.className}",
                    subtitle = "${t.dayOfWeek} • Period ${t.periodNumber} (${t.timeSlot}) • ${t.teacherName}",
                    category = "Timetable",
                    route = "timetable"
                )
            )
        }

        // Events
        events.forEach { ev ->
            list.add(
                SearchResultItem(
                    title = ev.title,
                    subtitle = "${ev.date} at ${ev.time} • ${ev.venue}",
                    category = "Events",
                    route = "events"
                )
            )
        }

        // Notices
        notices.forEach { n ->
            list.add(
                SearchResultItem(
                    title = n.title,
                    subtitle = "${n.category} • Posted: ${n.date}",
                    category = "Notices",
                    route = "notice_board"
                )
            )
        }

        // Syllabus
        syllabusList.forEach { s ->
            list.add(
                SearchResultItem(
                    title = "${s.className} - ${s.subject} Syllabus",
                    subtitle = s.topics,
                    category = "Syllabus",
                    route = "syllabus"
                )
            )
        }

        list
    }

    val filteredResults = remember(searchQuery, selectedCategory, allSearchableItems) {
        if (searchQuery.isBlank()) emptyList()
        else {
            allSearchableItems.filter { item ->
                val matchesQuery = item.title.contains(searchQuery, ignoreCase = true) ||
                        item.subtitle.contains(searchQuery, ignoreCase = true) ||
                        item.category.contains(searchQuery, ignoreCase = true)

                val matchesCat = when (selectedCategory) {
                    "All" -> true
                    "People" -> item.category in listOf("Student", "Teacher", "Admin")
                    else -> item.category.contains(selectedCategory, ignoreCase = true)
                }

                matchesQuery && matchesCat
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Search Header
        Surface(
            color = PrimaryDarkBlue,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search students, teachers, results, homework...", fontSize = 13.sp, color = Color.White.copy(0.7f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.White)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                ScrollableTabRow(
                    selectedTabIndex = categories.indexOf(selectedCategory),
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    edgePadding = 0.dp
                ) {
                    categories.forEach { cat ->
                        Tab(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            text = {
                                Text(
                                    text = cat,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }

        // Results View
        if (searchQuery.isBlank()) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    title = "Global Search Hub",
                    subtitle = "Type any student name, roll number, teacher, exam result, homework, or notice to quickly find it.",
                    icon = Icons.Default.Search
                )
            }
        } else if (filteredResults.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    title = "No Matches Found",
                    subtitle = "No results matching \"$searchQuery\" under $selectedCategory.",
                    icon = Icons.Default.SearchOff
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredResults) { item ->
                    val icon = when (item.category) {
                        "Student", "Teacher", "Admin" -> Icons.Default.Person
                        "Homework" -> Icons.Default.Assignment
                        "Results" -> Icons.Default.BarChart
                        "Study Materials" -> Icons.Default.MenuBook
                        "Timetable" -> Icons.Default.Schedule
                        "Events" -> Icons.Default.Event
                        "Notices" -> Icons.Default.Campaign
                        else -> Icons.Default.Class
                    }

                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onNavigate(item.route) }
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
                                        contentDescription = item.category,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = PrimaryDarkBlue.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = item.category,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryDarkBlue,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = item.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryDarkBlue
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = item.subtitle,
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    maxLines = 2
                                )
                            }

                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = "Go",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
