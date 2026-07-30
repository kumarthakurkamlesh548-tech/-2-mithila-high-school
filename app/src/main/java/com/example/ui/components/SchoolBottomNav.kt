package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryBlue
import com.example.ui.viewmodel.ScreenRoute

data class BottomNavItem(
    val route: ScreenRoute,
    val icon: ImageVector,
    val label: String
)

@Composable
fun SchoolBottomNav(
    currentRoute: ScreenRoute,
    userRole: UserRole?,
    onNavigate: (ScreenRoute) -> Unit
) {
    val navItems = when (userRole) {
        UserRole.SUPER_ADMIN -> listOf(
            BottomNavItem(ScreenRoute.SuperAdminDashboard, Icons.Default.Security, "Super HQ"),
            BottomNavItem(ScreenRoute.AdminDashboard, Icons.Default.AdminPanelSettings, "Admin"),
            BottomNavItem(ScreenRoute.NoticeBoard, Icons.Default.Campaign, "Notices"),
            BottomNavItem(ScreenRoute.Results, Icons.Default.Assessment, "Results"),
            BottomNavItem(ScreenRoute.Settings, Icons.Default.Settings, "Settings")
        )
        UserRole.ADMIN -> listOf(
            BottomNavItem(ScreenRoute.AdminDashboard, Icons.Default.AdminPanelSettings, "Dashboard"),
            BottomNavItem(ScreenRoute.NoticeBoard, Icons.Default.Campaign, "Notices"),
            BottomNavItem(ScreenRoute.Results, Icons.Default.Assessment, "Results"),
            BottomNavItem(ScreenRoute.AboutSchool, Icons.Default.School, "School"),
            BottomNavItem(ScreenRoute.Settings, Icons.Default.Settings, "Settings")
        )
        UserRole.TEACHER -> listOf(
            BottomNavItem(ScreenRoute.TeacherDashboard, Icons.Default.Dashboard, "Dashboard"),
            BottomNavItem(ScreenRoute.Homework, Icons.Default.Assignment, "Homework"),
            BottomNavItem(ScreenRoute.Attendance, Icons.Default.FactCheck, "Attendance"),
            BottomNavItem(ScreenRoute.DoubtSection, Icons.Default.QuestionAnswer, "Doubts"),
            BottomNavItem(ScreenRoute.Settings, Icons.Default.Settings, "Settings")
        )
        else -> listOf(
            BottomNavItem(ScreenRoute.Home, Icons.Default.Home, "Home"),
            BottomNavItem(ScreenRoute.StudentDashboard, Icons.Default.Dashboard, "Overview"),
            BottomNavItem(ScreenRoute.StudyMaterial, Icons.Default.MenuBook, "Study"),
            BottomNavItem(ScreenRoute.NoticeBoard, Icons.Default.Campaign, "Notices"),
            BottomNavItem(ScreenRoute.Profile, Icons.Default.Person, "Profile")
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.92f))
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
            .padding(vertical = 6.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val isSelected = currentRoute.route == item.route.route

                val backgroundColor = if (isSelected) SecondaryBlue else Color.Transparent
                val contentColor = if (isSelected) PrimaryBlue else Color(0xFF64748B)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(backgroundColor)
                        .clickable { onNavigate(item.route) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = contentColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = contentColor
                    )
                }
            }
        }
    }
}
