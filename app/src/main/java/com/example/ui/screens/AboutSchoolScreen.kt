package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.InfoRow
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue

@Composable
fun AboutSchoolScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_school_banner),
                    contentDescription = "Mithila High School",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Welcome to +2 Government Mithila High School",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Balaur, Manigachhi, Darbhanga, Bihar",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Welcome Message Box
        item {
            GlassmorphicCard(cornerRadius = 24.dp) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.img_school_logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Institutional Mission",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Welcome to +2 Government Mithila High School, Balaur, a government co-educational institution dedicated to providing quality education and shaping the future of students through knowledge, discipline, and strong moral values. Our mission is to create a positive and inclusive learning environment that supports the overall academic and personal development of every student.",
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFF334155)
                    )
                }
            }
        }

        // Key Institutional Info Grid
        item {
            Text(
                text = "School Information & Credentials",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkBlue,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            GlassmorphicCard(cornerRadius = 20.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    InfoRow(icon = Icons.Default.Verified, title = "Affiliation", value = "Bihar School Examination Board (BSEB), Patna")
                    Divider(color = GlassBorder)
                    InfoRow(icon = Icons.Default.CalendarToday, title = "Established Year", value = "1955")
                    Divider(color = GlassBorder)
                    InfoRow(icon = Icons.Default.History, title = "Former Name", value = "Mithila High English School")
                    Divider(color = GlassBorder)
                    InfoRow(icon = Icons.Default.School, title = "Current Name", value = "+2 Government Mithila High School Balaur")
                }
            }
        }

        // Official Codes
        item {
            Text(
                text = "Government & Board Codes",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkBlue,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White)
                        .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(text = "UDISE Code", fontSize = 11.sp, color = Color(0xFF64748B))
                        Text(text = "10130800503", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White)
                        .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(text = "Secondary Code", fontSize = 11.sp, color = Color(0xFF64748B))
                        Text(text = "61026", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White)
                        .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(text = "Sr Sec Code", fontSize = 11.sp, color = Color(0xFF64748B))
                        Text(text = "51102", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                    }
                }
            }
        }

        // Classes Offered
        item {
            GlassmorphicCard(cornerRadius = 20.dp) {
                Column {
                    Text(
                        text = "Classes Taught",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkBlue
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Class 9", "Class 10", "Class 11", "Class 12").forEach { cls ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(PrimaryBlue.copy(alpha = 0.15f))
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = cls,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryDarkBlue
                                )
                            }
                        }
                    }
                }
            }
        }

        // Address & Contact Info
        item {
            GlassmorphicCard(cornerRadius = 20.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Campus Location & Contact",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkBlue
                    )
                    InfoRow(icon = Icons.Default.Place, title = "School Address", value = "+2 Government Mithila High School Balaur, PS Manigachhi, District Darbhanga, Bihar - 847422")
                    Divider(color = GlassBorder)
                    InfoRow(icon = Icons.Default.Email, title = "Official Email", value = "mithilahs610@gmail.com")
                }
            }
        }
    }
}
