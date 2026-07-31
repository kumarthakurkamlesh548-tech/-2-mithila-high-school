package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.GeminiMessage
import com.example.data.repository.GeminiRepository
import com.example.data.repository.GeminiRole
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue
import com.example.util.ContentModerator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiChatbotScreen() {
    var selectedRole by remember { mutableStateOf(GeminiRole.GENERAL_SUPPORT) }

    // Map storing conversation history per agent role
    var conversationHistories by remember {
        mutableStateOf(
            mapOf(
                GeminiRole.GENERAL_SUPPORT to listOf(
                    GeminiMessage(
                        role = "model",
                        text = "Hello! I am your Mithila School Support Agent. How can I help you with admissions, timetables, or school troubleshooting today?"
                    )
                ),
                GeminiRole.ACADEMIC_TUTOR to listOf(
                    GeminiMessage(
                        role = "model",
                        text = "Greetings! I am your Academic AI Tutor (powered by Gemini 3.1 Pro). Ask me any complex math problem, science doubt, or homework question!"
                    )
                ),
                GeminiRole.QUICK_ASSISTANT to listOf(
                    GeminiMessage(
                        role = "model",
                        text = "Hi! I am Instant Quick Help. Ask me quick questions about school timings, office numbers, or campus facilities for instant answers."
                    )
                )
            )
        )
    }

    var inputText by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var snackbarMsg by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val geminiRepo = remember { GeminiRepository() }

    val activeHistory = conversationHistories[selectedRole] ?: emptyList()
    val listState = rememberLazyListState()

    // Scroll to bottom when history changes
    LaunchedEffect(activeHistory.size, isGenerating) {
        if (activeHistory.isNotEmpty()) {
            listState.animateScrollToItem(activeHistory.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top Banner
            Surface(
                color = PrimaryDarkBlue,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "AI Support Assistant",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Multi-turn context-aware assistant",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }

                        // Reset Conversation Button
                        IconButton(
                            onClick = {
                                val initialMsg = when (selectedRole) {
                                    GeminiRole.GENERAL_SUPPORT -> "Hello! I am your Mithila School Support Agent. How can I help you with admissions, timetables, or school troubleshooting today?"
                                    GeminiRole.ACADEMIC_TUTOR -> "Greetings! I am your Academic AI Tutor (powered by Gemini 3.1 Pro). Ask me any complex math problem, science doubt, or homework question!"
                                    GeminiRole.QUICK_ASSISTANT -> "Hi! I am Instant Quick Help. Ask me quick questions about school timings, office numbers, or campus facilities for instant answers."
                                }
                                conversationHistories = conversationHistories.toMutableMap().apply {
                                    put(selectedRole, listOf(GeminiMessage(role = "model", text = initialMsg)))
                                }
                                snackbarMsg = "Conversation reset."
                            }
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Model Role Tabs
                    ScrollableTabRow(
                        selectedTabIndex = GeminiRole.values().indexOf(selectedRole),
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        edgePadding = 0.dp
                    ) {
                        GeminiRole.values().forEach { role ->
                            Tab(
                                selected = selectedRole == role,
                                onClick = { selectedRole = role },
                                text = {
                                    Text(
                                        text = role.displayName,
                                        fontSize = 12.sp,
                                        fontWeight = if (selectedRole == role) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Role Description Badge
            Surface(
                color = Color(0xFFEFF6FF),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Psychology, contentDescription = "Model", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Model: ${selectedRole.modelName} — ${selectedRole.description}",
                        fontSize = 11.sp,
                        color = PrimaryDarkBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Conversation Messages Stream
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC))
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(activeHistory) { msg ->
                        val isUser = msg.role == "user"
                        val timeStr = remember(msg.timestamp) {
                            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(msg.timestamp))
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!isUser) {
                                    Icon(Icons.Default.SmartToy, contentDescription = "AI", tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(selectedRole.displayName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                } else {
                                    Text("You", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isUser) 16.dp else 4.dp,
                                    bottomEnd = if (isUser) 4.dp else 16.dp
                                ),
                                color = if (isUser) PrimaryBlue else Color.White,
                                shadowElevation = 1.dp,
                                modifier = Modifier.widthIn(max = 300.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = msg.text,
                                        fontSize = 13.sp,
                                        color = if (isUser) Color.White else Color(0xFF1E293B),
                                        lineHeight = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = timeStr,
                                        fontSize = 9.sp,
                                        color = if (isUser) Color.White.copy(alpha = 0.7f) else Color.Gray,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }
                        }
                    }

                    if (isGenerating) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = PrimaryBlue
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${selectedRole.displayName} is thinking...",
                                    fontSize = 12.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Ask support agent or tutor...", fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        enabled = !isGenerating,
                        maxLines = 4
                    )

                    FloatingActionButton(
                        onClick = {
                            if (ContentModerator.containsProhibitedLanguage(inputText)) {
                                snackbarMsg = ContentModerator.PROHIBITED_LANGUAGE_MESSAGE
                            } else if (inputText.isNotBlank()) {
                                val userMsg = GeminiMessage(role = "user", text = inputText)
                                val currentList = conversationHistories[selectedRole] ?: emptyList()
                                val updatedHistory = currentList + userMsg

                                conversationHistories = conversationHistories.toMutableMap().apply {
                                    put(selectedRole, updatedHistory)
                                }

                                val promptText = inputText
                                inputText = ""
                                isGenerating = true

                                coroutineScope.launch {
                                    val result = geminiRepo.generateMultiTurnResponse(selectedRole, updatedHistory)
                                    isGenerating = false

                                    result.onSuccess { modelReply ->
                                        val modelMsg = GeminiMessage(role = "model", text = modelReply)
                                        conversationHistories = conversationHistories.toMutableMap().apply {
                                            put(selectedRole, updatedHistory + modelMsg)
                                        }
                                    }.onFailure { err ->
                                        snackbarMsg = err.localizedMessage ?: "Failed to get AI response."
                                    }
                                }
                            }
                        },
                        containerColor = PrimaryBlue,
                        contentColor = Color.White,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
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
