package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.NoticeEntity
import com.example.data.model.UserEntity
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue
import com.example.ui.theme.SecondaryBlue

data class QuickAccessCardItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String,
    val color: Color
)

@Composable
fun HomeScreen(
    currentUser: UserEntity?,
    notices: List<NoticeEntity>,
    onCardClick: (String) -> Unit
) {
    val quickAccessList = listOf(
        QuickAccessCardItem("Latest Notice", "Official Updates", Icons.Default.Campaign, "notice_board", Color(0xFF3B82F6)),
        QuickAccessCardItem("Exam Results", "Marksheet & PDF", Icons.Default.Assessment, "results", Color(0xFF10B981)),
        QuickAccessCardItem("Attendance", "Daily & Monthly", Icons.Default.FactCheck, "attendance", Color(0xFFF59E0B)),
        QuickAccessCardItem("Study Material", "Notes & PYQs", Icons.Default.MenuBook, "study_material", Color(0xFF8B5CF6)),
        QuickAccessCardItem("Class Routine", "Timetable & Exams", Icons.Default.Schedule, "timetable", Color(0xFFEC4899)),
        QuickAccessCardItem("Daily Homework", "Assignments", Icons.Default.Assignment, "homework", Color(0xFF06B6D4)),
        QuickAccessCardItem("Syllabus", "Classes 9 to 12", Icons.Default.Menu, "syllabus", Color(0xFF6366F1)),
        QuickAccessCardItem("Doubt Section", "Ask & QnA", Icons.Default.QuestionAnswer, "doubt_section", Color(0xFF14B8A6)),
        QuickAccessCardItem("School Chat", "Live Channels", Icons.Default.Forum, "chat", Color(0xFF0284C7)),
        QuickAccessCardItem("AI Support Agent", "Gemini Assistant", Icons.Default.AutoAwesome, "gemini_chatbot", Color(0xFF7C3AED))
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Section Card with School Banner & Principal Photo
        item {
            GlassmorphicCard(
                cornerRadius = 24.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // School Banner Image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(18.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_school_banner),
                            contentDescription = "School Campus",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                                    )
                                )
                        )
                        Text(
                            text = "+2 Govt Mithila High School Balaur",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Principal Profile & Welcome
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_principal),
                            contentDescription = "Principal",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(2.dp, PrimaryBlue, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Welcome to Our Campus",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDarkBlue
                            )
                            Text(
                                text = "Affiliated to BSEB Patna • Estd. 1955",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        IconButton(
                            onClick = { onCardClick("about_school") },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(SecondaryBlue)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "School Info",
                                tint = PrimaryBlue
                            )
                        }
                    }
                }
            }
        }

        // Quick Access Cards Grid (2 columns)
        item {
            Text(
                text = "Quick Access Services",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkBlue,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                quickAccessList.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White)
                                    .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                                    .clickable { onCardClick(item.route) }
                                    .padding(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(item.color.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.title,
                                            tint = item.color,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = item.title,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryDarkBlue,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = item.subtitle,
                                            fontSize = 10.sp,
                                            color = Color(0xFF64748B),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Latest Official Notice Banner
        item {
            GlassmorphicCard(
                cornerRadius = 20.dp,
                onClick = { onCardClick("notice_board") }
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = "Notice",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Latest Notice Board",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDarkBlue
                            )
                        }
                        Text(
                            text = "View All →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (notices.isNotEmpty()) {
                        val latest = notices.first()
                        Text(
                            text = latest.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = latest.content,
                            fontSize = 11.sp,
                            color = Color(0xFF475569),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Text(
                            text = "No notices posted today.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }

        // Photo Gallery Preview
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Campus & Gallery",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkBlue
                    )
                    Text(
                        text = "Explore →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        modifier = Modifier.clickable { onCardClick("gallery") }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        listOf(
                            Triple("School Campus", R.drawable.img_school_banner, "Balaur Main Ground"),
                            Triple("Principal Office", R.drawable.img_principal, "Administrative Wing"),
                            Triple("Official Emblem", R.drawable.img_school_logo, "Mithila High Crest")
                        )
                    ) { (title, resId, sub) ->
                        Box(
                            modifier = Modifier
                                .width(180.dp)
                                .height(110.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onCardClick("gallery") }
                        ) {
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                        )
                                    )
                            )
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
