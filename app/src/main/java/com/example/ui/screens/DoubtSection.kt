package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Send
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

@Composable
fun DoubtSection(
    doubts: List<DoubtEntity>,
    userRole: UserRole?,
    onAskDoubt: (subject: String, question: String) -> Unit,
    onReplyDoubt: (doubtId: Int, replyText: String) -> Unit
) {
    var subjectInput by remember { mutableStateOf("Mathematics") }
    var questionInput by remember { mutableStateOf("") }
    var replyInputs by remember { mutableStateOf(mutableMapOf<Int, String>()) }
    var snackbarMsg by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        text = "Doubt Resolution Forum",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkBlue
                    )
                    Text(
                        text = "Ask Subject Questions & Get Help from Expert Teachers",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Ask Doubt Card
            item {
                GlassmorphicCard(cornerRadius = 20.dp) {
                    Column {
                        Text(text = "Ask a New Question / Doubt", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = subjectInput,
                            onValueChange = { subjectInput = it },
                            label = { Text("Subject (e.g. Science, Maths)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = questionInput,
                            onValueChange = { questionInput = it },
                            label = { Text("Write your doubt in detail...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (questionInput.isNotBlank()) {
                                    onAskDoubt(subjectInput, questionInput)
                                    questionInput = ""
                                    snackbarMsg = "Doubt submitted successfully to teachers!"
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Post Doubt", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Doubts Feed
            item {
                Text(
                    text = "Recent Student Doubts & Answers",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkBlue
                )
            }

            items(doubts) { doubt ->
                val currentReplyText = replyInputs[doubt.id] ?: ""

                GlassmorphicCard(cornerRadius = 20.dp) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Help, contentDescription = "Q", tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = doubt.studentName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                            }
                            Surface(
                                color = if (doubt.status == "Answered") Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFF59E0B).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = doubt.status,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (doubt.status == "Answered") Color(0xFF10B981) else Color(0xFFF59E0B),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Subject: ${doubt.subject} (${doubt.className})", fontSize = 11.sp, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = doubt.question, fontSize = 13.sp, color = Color(0xFF1E293B), lineHeight = 18.sp)

                        Spacer(modifier = Modifier.height(10.dp))

                        // Teacher Reply Box
                        if (userRole == UserRole.TEACHER || userRole == UserRole.ADMIN) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SecondaryBlue.copy(alpha = 0.4f))
                                    .padding(10.dp)
                            ) {
                                Text(text = "Teacher Reply Portal", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = currentReplyText,
                                    onValueChange = { text ->
                                        replyInputs = replyInputs.toMutableMap().apply { put(doubt.id, text) }
                                    },
                                    label = { Text("Type teacher solution/reply...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = {
                                        if (currentReplyText.isNotBlank()) {
                                            onReplyDoubt(doubt.id, currentReplyText)
                                            replyInputs = replyInputs.toMutableMap().apply { remove(doubt.id) }
                                            snackbarMsg = "Reply sent to student!"
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Submit Reply", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
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
