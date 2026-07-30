package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserEntity
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue
import com.example.ui.viewmodel.ScreenRoute

data class DrawerMenuItem(
    val route: ScreenRoute,
    val title: String,
    val icon: ImageVector
)

@Composable
fun NavigationDrawerContent(
    currentRoute: ScreenRoute,
    currentUser: UserEntity?,
    onNavigate: (ScreenRoute) -> Unit,
    onCloseDrawer: () -> Unit
) {
    val menuItems = mutableListOf(
        DrawerMenuItem(ScreenRoute.Home, "Home", Icons.Default.Home)
    )

    if (currentUser?.role == com.example.data.model.UserRole.SUPER_ADMIN) {
        menuItems.add(DrawerMenuItem(ScreenRoute.SuperAdminDashboard, "Super Admin HQ", Icons.Default.Security))
    }
    if (currentUser?.role == com.example.data.model.UserRole.ADMIN || currentUser?.role == com.example.data.model.UserRole.SUPER_ADMIN) {
        menuItems.add(DrawerMenuItem(ScreenRoute.AdminDashboard, "Admin Dashboard", Icons.Default.AdminPanelSettings))
    }

    menuItems.addAll(
        listOf(
            DrawerMenuItem(ScreenRoute.AboutSchool, "About School", Icons.Default.School),
            DrawerMenuItem(ScreenRoute.Results, "Results", Icons.Default.Assessment),
            DrawerMenuItem(ScreenRoute.Syllabus, "Syllabus", Icons.Default.MenuBook),
            DrawerMenuItem(ScreenRoute.StudyMaterial, "Study Material", Icons.Default.Description),
            DrawerMenuItem(ScreenRoute.Homework, "Homework", Icons.Default.Assignment),
            DrawerMenuItem(ScreenRoute.Attendance, "Attendance", Icons.Default.FactCheck),
            DrawerMenuItem(ScreenRoute.Timetable, "Timetable", Icons.Default.Schedule),
            DrawerMenuItem(ScreenRoute.DoubtSection, "Doubt Section", Icons.Default.QuestionAnswer),
            DrawerMenuItem(ScreenRoute.NoticeBoard, "Notice Board", Icons.Default.Campaign),
            DrawerMenuItem(ScreenRoute.Gallery, "Gallery", Icons.Default.Collections),
            DrawerMenuItem(ScreenRoute.Events, "Events", Icons.Default.Event),
            DrawerMenuItem(ScreenRoute.Downloads, "Downloads", Icons.Default.Download),
            DrawerMenuItem(ScreenRoute.Profile, "Profile", Icons.Default.Person),
            DrawerMenuItem(ScreenRoute.Settings, "Settings", Icons.Default.Settings)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Drawer Header with School Logo & User Info
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(PrimaryDarkBlue)
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.img_school_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "+2 Govt Mithila High School",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Balaur, Darbhanga",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                if (currentUser != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = currentUser.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = "Role: ${currentUser.role.name}", fontSize = 10.sp, color = PrimaryBlue)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "NAVIGATION MENU",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(menuItems) { item ->
                val isSelected = currentRoute.route == item.route.route

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) PrimaryBlue.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable {
                            onNavigate(item.route)
                            onCloseDrawer()
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) PrimaryBlue else Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = item.title,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isSelected) PrimaryBlue else PrimaryDarkBlue
                    )
                }
            }
        }
    }
}
