package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue

@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    notificationsEnabled: Boolean,
    onToggleNotifications: (Boolean) -> Unit,
    currentLanguage: String,
    onSelectLanguage: (String) -> Unit,
    onLogout: () -> Unit
) {
    var showLangDialog by remember { mutableStateOf(false) }

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
                    text = "Application Settings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkBlue
                )
                Text(
                    text = "Theme Preferences, Notifications & Language Selection",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        item {
            GlassmorphicCard(cornerRadius = 20.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Dark Mode Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DarkMode, contentDescription = "Dark", tint = PrimaryBlue)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "Dark Mode Theme", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                                Text(text = "Toggle night reading appearance", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = onToggleDarkMode,
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryBlue)
                        )
                    }

                    Divider(color = GlassBorder)

                    // Notifications Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notif", tint = PrimaryBlue)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "Push Notifications", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                                Text(text = "Get notice & homework alerts", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = onToggleNotifications,
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryBlue)
                        )
                    }

                    Divider(color = GlassBorder)

                    // Language Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLangDialog = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = "Lang", tint = PrimaryBlue)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "Interface Language", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                                Text(text = "Current: $currentLanguage", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }
                        Text(text = "Change ›", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                }
            }
        }

        item {
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout Account", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }

    if (showLangDialog) {
        AlertDialog(
            onDismissRequest = { showLangDialog = false },
            title = { Text("Select App Language", fontWeight = FontWeight.Bold, color = PrimaryDarkBlue) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("English", "Hindi (हिन्दी)", "Maithili (मैथिली)").forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (currentLanguage == lang) PrimaryBlue.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable {
                                    onSelectLanguage(lang)
                                    showLangDialog = false
                                }
                                .padding(12.dp)
                        ) {
                            Text(text = lang, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLangDialog = false }) { Text("Close") }
            }
        )
    }
}
