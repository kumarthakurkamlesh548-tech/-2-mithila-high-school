package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventEntity
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue

@Composable
fun EventsScreen(
    events: List<EventEntity>
) {
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
                    text = "Upcoming School Events",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkBlue
                )
                Text(
                    text = "Sports Meets, Science Fairs & Cultural Programs",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        if (events.isEmpty()) {
            item {
                GlassmorphicCard {
                    Text(text = "No upcoming school events scheduled.", fontSize = 13.sp, color = Color(0xFF64748B))
                }
            }
        } else {
            items(events) { ev ->
                GlassmorphicCard(cornerRadius = 20.dp) {
                    Column {
                        Text(
                            text = ev.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkBlue
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Event, contentDescription = "Date", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = ev.date, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)

                            Spacer(modifier = Modifier.width(16.dp))

                            Icon(Icons.Default.Schedule, contentDescription = "Time", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = ev.time, fontSize = 12.sp, color = Color(0xFF475569))
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = "Venue", tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Venue: ${ev.venue}", fontSize = 12.sp, color = Color(0xFF64748B))
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = ev.description,
                            fontSize = 12.sp,
                            color = Color(0xFF334155),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
