package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_rooms")
data class ChatRoom(
    @PrimaryKey val id: String,
    val title: String,
    val isGroup: Boolean = true,
    val participantIds: List<String> = emptyList(),
    val participantNames: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val iconName: String = "group"
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey val id: String,
    val roomId: String,
    val senderId: String,
    val senderName: String,
    val senderRole: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val formattedTime: String = "",
    val replyToId: String = "",
    val replyToText: String = "",
    val replyToSender: String = "",
    val readBy: List<String> = emptyList(),
    val isDeleted: Boolean = false
)

data class UserPresence(
    val userId: String = "",
    val userName: String = "",
    val userRole: String = "",
    val isOnline: Boolean = false,
    val lastSeen: Long = System.currentTimeMillis(),
    val isTyping: Boolean = false,
    val typingInRoomId: String = ""
)
