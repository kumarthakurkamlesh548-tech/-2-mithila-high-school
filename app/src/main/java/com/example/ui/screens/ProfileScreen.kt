package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserEntity
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.InfoRow
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue
import com.example.ui.theme.SecondaryBlue

@Composable
fun ProfileScreen(
    currentUser: UserEntity?
) {
    val user = currentUser ?: return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlassmorphicCard(cornerRadius = 24.dp) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.img_school_logo),
                            contentDescription = "Profile Photo",
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .border(3.dp, PrimaryBlue, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = user.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                    Text(text = user.email, fontSize = 12.sp, color = Color(0xFF64748B))

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = PrimaryBlue,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "ROLE: ${user.role.name}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Academic Credentials & Details",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkBlue,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            GlassmorphicCard(cornerRadius = 20.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    InfoRow(icon = Icons.Default.Badge, title = "Roll Number", value = user.rollNumber.ifEmpty { "N/A" })
                    Divider(color = GlassBorder)
                    InfoRow(icon = Icons.Default.ConfirmationNumber, title = "Admission Number", value = user.admissionNumber.ifEmpty { "MHS-2023-089" })
                    Divider(color = GlassBorder)
                    InfoRow(icon = Icons.Default.Class, title = "Class & Section", value = "${user.className} (${user.section})")
                    Divider(color = GlassBorder)
                    InfoRow(icon = Icons.Default.FamilyRestroom, title = "Parent / Guardian Name", value = user.parentName.ifEmpty { "Ramesh Kumar Thakur" })
                    Divider(color = GlassBorder)
                    InfoRow(icon = Icons.Default.Phone, title = "Mobile Contact", value = user.phone.ifEmpty { "+91 9835412890" })
                    Divider(color = GlassBorder)
                    InfoRow(icon = Icons.Default.Place, title = "Permanent Address", value = user.address)
                }
            }
        }
    }
}
