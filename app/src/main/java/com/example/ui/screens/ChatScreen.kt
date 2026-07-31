package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.ChatRoom
import com.example.data.model.UserEntity
import com.example.data.model.UserPresence
import com.example.data.model.UserRole
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue
import com.example.ui.theme.SecondaryBlue
import com.example.util.ContentModerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    currentUser: UserEntity?,
    chatRooms: List<ChatRoom>,
    currentRoomMessages: List<ChatMessage>,
    presences: List<UserPresence>,
    allUsers: List<UserEntity>,
    activeRoomId: String,
    onSelectRoom: (roomId: String) -> Unit,
    onSendMessage: (roomId: String, text: String, replyToId: String, replyToText: String, replyToSender: String) -> Unit,
    onDeleteMessage: (roomId: String, messageId: String) -> Unit,
    onUpdateTypingStatus: (roomId: String, isTyping: Boolean) -> Unit,
    onMarkRead: (roomId: String, messageId: String) -> Unit,
    onCreatePrivateRoom: (targetUser: UserEntity) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Groups, 1 = Direct Messages
    var inputText by remember { mutableStateOf("") }
    var replyingToMessage by remember { mutableStateOf<ChatMessage?>(null) }
    var snackbarMsg by remember { mutableStateOf("") }
    var showUserPicker by remember { mutableStateOf(false) }

    val activeRoom = chatRooms.find { it.id == activeRoomId } ?: chatRooms.firstOrNull()
    val activeRoomIdActual = activeRoom?.id ?: "group_general"

    // Find typing users in current active room
    val typingUsers = presences.filter {
        it.isTyping && it.typingInRoomId == activeRoomIdActual && it.userId != (currentUser?.id ?: "")
    }

    val listState = rememberLazyListState()

    // Scroll to latest message when new messages arrive
    LaunchedEffect(currentRoomMessages.size) {
        if (currentRoomMessages.isNotEmpty()) {
            listState.animateScrollToItem(currentRoomMessages.size - 1)
        }
    }

    // Mark unread messages as read
    LaunchedEffect(currentRoomMessages, currentUser) {
        val uid = currentUser?.id ?: return@LaunchedEffect
        currentRoomMessages.forEach { msg ->
            if (!msg.readBy.contains(uid)) {
                onMarkRead(activeRoomIdActual, msg.id)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top Header
            Surface(
                color = PrimaryDarkBlue,
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Forum, contentDescription = "Chat", tint = Color.White, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "School Chat & Messaging",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Real-time communication & discussions",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }

                        // Start New Chat Button
                        IconButton(
                            onClick = { showUserPicker = true },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = PrimaryBlue)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "New Chat", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Group / DM Tab Selector
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Group Channels", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Direct Messages", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }

            // Chat Room Selector Bar (Horizontal)
            val filteredRooms = remember(chatRooms, selectedTab) {
                if (selectedTab == 0) chatRooms.filter { it.isGroup }
                else chatRooms.filter { !it.isGroup }
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredRooms) { room ->
                    val isSelected = room.id == activeRoomIdActual
                    Surface(
                        onClick = { onSelectRoom(room.id) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) PrimaryBlue else Color.White,
                        shadowElevation = if (isSelected) 4.dp else 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (room.isGroup) Icons.Default.Groups else Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else PrimaryDarkBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = room.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF334155),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Active Chat Room Header
            if (activeRoom != null) {
                Surface(
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = PrimaryBlue.copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        if (activeRoom.isGroup) Icons.Default.Groups else Icons.Default.Person,
                                        contentDescription = null,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = activeRoom.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryDarkBlue
                                )

                                // Presence or Typing Subtitle
                                if (typingUsers.isNotEmpty()) {
                                    val names = typingUsers.joinToString(", ") { it.userName }
                                    Text(
                                        text = "$names is typing...",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue
                                    )
                                } else if (!activeRoom.isGroup) {
                                    val otherUserId = activeRoom.participantIds.find { it != currentUser?.id }
                                    val otherPresence = presences.find { it.userId == otherUserId }
                                    val isOnline = otherPresence?.isOnline ?: false
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(if (isOnline) Color(0xFF10B981) else Color.Gray)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isOnline) "Active Now" else "Offline",
                                            fontSize = 11.sp,
                                            color = if (isOnline) Color(0xFF10B981) else Color.Gray
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Official Group Channel",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                        // Role badge indicator
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (activeRoom.isGroup) "GROUP" else "PRIVATE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Chat Message Stream
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC))
            ) {
                if (currentRoomMessages.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Empty", tint = Color.LightGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No messages in this chat yet.", fontSize = 14.sp, color = Color.Gray)
                            Text("Be the first to start the conversation!", fontSize = 12.sp, color = Color.LightGray)
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(currentRoomMessages) { message ->
                            val isMe = currentUser?.id != null && message.senderId == currentUser.id
                            val isAdminOrSuper = currentUser?.role == UserRole.ADMIN || currentUser?.role == UserRole.SUPER_ADMIN

                            ChatMessageBubble(
                                message = message,
                                isMe = isMe,
                                canDelete = isMe || isAdminOrSuper,
                                onReply = { replyingToMessage = message },
                                onDelete = { onDeleteMessage(activeRoomIdActual, message.id) }
                            )
                        }
                    }
                }
            }

            // Input Bar Container
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {

                    // Quoted message preview if replying
                    if (replyingToMessage != null) {
                        val replyMsg = replyingToMessage!!
                        Surface(
                            color = PrimaryBlue.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Replying to ${replyMsg.senderName}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue
                                    )
                                    Text(
                                        text = replyMsg.messageText,
                                        fontSize = 12.sp,
                                        color = Color.DarkGray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                IconButton(
                                    onClick = { replyingToMessage = null },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.Gray)
                                }
                            }
                        }
                    }

                    // Text Input Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { text ->
                                inputText = text
                                onUpdateTypingStatus(activeRoomIdActual, text.isNotBlank())
                            },
                            placeholder = { Text("Type a message...", fontSize = 13.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            shape = RoundedCornerShape(20.dp),
                            maxLines = 4
                        )

                        FloatingActionButton(
                            onClick = {
                                if (ContentModerator.containsProhibitedLanguage(inputText)) {
                                    snackbarMsg = ContentModerator.PROHIBITED_LANGUAGE_MESSAGE
                                } else if (inputText.isNotBlank()) {
                                    onSendMessage(
                                        activeRoomIdActual,
                                        inputText,
                                        replyingToMessage?.id ?: "",
                                        replyingToMessage?.messageText ?: "",
                                        replyingToMessage?.senderName ?: ""
                                    )
                                    inputText = ""
                                    replyingToMessage = null
                                    onUpdateTypingStatus(activeRoomIdActual, false)
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
        }

        // New Direct Message User Picker Dialog
        if (showUserPicker) {
            AlertDialog(
                onDismissRequest = { showUserPicker = false },
                title = { Text("Start Direct Message", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue) },
                text = {
                    Column {
                        Text("Select a user to message:", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val otherUsers = allUsers.filter { it.id != (currentUser?.id ?: "") }
                            items(otherUsers) { user ->
                                Surface(
                                    onClick = {
                                        onCreatePrivateRoom(user)
                                        showUserPicker = false
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = PrimaryBlue.copy(alpha = 0.2f),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = user.name.take(1).uppercase(),
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = PrimaryBlue
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = user.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                                            Text(text = "Role: ${user.role.name} • Class: ${user.className}", fontSize = 10.sp, color = Color.Gray)
                                        }
                                        Icon(Icons.Default.ChevronRight, contentDescription = "Select", tint = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showUserPicker = false }) {
                        Text("Close")
                    }
                }
            )
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

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    isMe: Boolean,
    canDelete: Boolean,
    onReply: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    val bubbleBg = if (isMe) PrimaryBlue else Color.White
    val textColor = if (isMe) Color.White else Color(0xFF1E293B)
    val alignment = if (isMe) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        // Sender Name & Role
        if (!isMe) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = message.senderName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkBlue
                )
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                    color = PrimaryBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = message.senderRole,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
        }

        Box {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isMe) 16.dp else 4.dp,
                    bottomEnd = if (isMe) 4.dp else 16.dp
                ),
                color = if (message.isDeleted) Color(0xFFF1F5F9) else bubbleBg,
                shadowElevation = 1.dp,
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clickable { showMenu = true }
            ) {
                Column(modifier = Modifier.padding(10.dp)) {

                    // Quoted message if replying
                    if (message.replyToText.isNotBlank() && !message.isDeleted) {
                        Surface(
                            color = if (isMe) Color.White.copy(alpha = 0.2f) else PrimaryBlue.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Text(
                                    text = message.replyToSender,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMe) Color.White else PrimaryBlue
                                )
                                Text(
                                    text = message.replyToText,
                                    fontSize = 11.sp,
                                    color = if (isMe) Color.White.copy(alpha = 0.9f) else Color.DarkGray,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Message Content
                    if (message.isDeleted) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Block, contentDescription = "Deleted", tint = Color.Gray, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "This message was deleted",
                                fontSize = 12.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = Color.Gray
                            )
                        }
                    } else {
                        Text(
                            text = message.messageText,
                            fontSize = 13.sp,
                            color = textColor,
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Timestamp and Read Receipts
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = message.formattedTime,
                            fontSize = 9.sp,
                            color = if (isMe) Color.White.copy(alpha = 0.8f) else Color.Gray
                        )

                        if (isMe && !message.isDeleted) {
                            Spacer(modifier = Modifier.width(4.dp))
                            val isRead = message.readBy.isNotEmpty()
                            Icon(
                                if (isRead) Icons.Default.DoneAll else Icons.Default.Done,
                                contentDescription = if (isRead) "Read" else "Sent",
                                tint = if (isRead) Color(0xFF93C5FD) else Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }

            // Options Dropdown Menu (Reply / Delete)
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Reply", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Reply, contentDescription = "Reply") },
                    onClick = {
                        showMenu = false
                        onReply()
                    }
                )

                if (canDelete && !message.isDeleted) {
                    DropdownMenuItem(
                        text = { Text("Delete Message", fontSize = 12.sp, color = Color.Red) },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}
