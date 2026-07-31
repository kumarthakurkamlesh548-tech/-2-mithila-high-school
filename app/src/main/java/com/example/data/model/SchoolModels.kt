package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    STUDENT,
    TEACHER,
    ADMIN,
    SUPER_ADMIN
}

data class AdminPermissions(
    val manageResults: Boolean = true,
    val manageNotices: Boolean = true,
    val manageAttendance: Boolean = true,
    val manageStudyMaterial: Boolean = true,
    val manageHomework: Boolean = true,
    val manageTimetable: Boolean = true,
    val manageStudents: Boolean = true,
    val manageTeachers: Boolean = true,
    val manageGallery: Boolean = true,
    val manageEvents: Boolean = true,
    val manageDownloads: Boolean = true,
    val manageSyllabus: Boolean = true
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val permissions: AdminPermissions = AdminPermissions(),
    val isEnabled: Boolean = true,
    val rollNumber: String = "",
    val admissionNumber: String = "",
    val className: String = "Class 10",
    val section: String = "A",
    val parentName: String = "",
    val phone: String = "",
    val address: String = "Balaur, Manigachhi, Darbhanga, Bihar",
    val photoUrl: String = ""
)

@Entity(tableName = "notices")
data class NoticeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val date: String,
    val category: String, // Urgent, Academic, Exam, General
    val postedBy: String = "Principal Office",
    val driveUrl: String = ""
)

@Entity(tableName = "results")
data class ResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firebaseId: String = "",
    val studentId: String = "",
    val studentName: String,
    val rollNumber: String,
    val registrationNumber: String = "",
    val className: String, // Class 9, Class 10, Class 11, Class 12
    val section: String = "A",
    val stream: String = "", // Science, Commerce, Arts (only for Class 11 & 12)
    val examName: String, // Annual Examination, Half Yearly, Unit Test, Monthly Test, Pre Board, Board Examination
    val academicSession: String = "2025-2026",
    val uploadDate: String = "30 Jul 2026",
    val isPublished: Boolean = true,
    val pdfUrl: String = "", // Official result PDF URL or document path
    val scienceMarks: Int = 85,
    val mathMarks: Int = 90,
    val socialScienceMarks: Int = 84,
    val hindiMarks: Int = 88,
    val englishMarks: Int = 82,
    val maithiliMarks: Int = 92,
    val totalMarks: Int = 521,
    val maxMarks: Int = 600,
    val percentage: Double = 86.83,
    val grade: String = "A+",
    val remarks: String = "1st Division with Distinction"
)

@Entity(tableName = "syllabus")
data class SyllabusEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val className: String,
    val subject: String,
    val topics: String,
    val downloadUrl: String
)

@Entity(tableName = "study_materials")
data class StudyMaterialEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val className: String,
    val subject: String,
    val type: String, // Notes, PDF, Video, Assignment, Question Bank, PYQ
    val description: String,
    val fileOrVideoUrl: String,
    val dateUploaded: String
)

@Entity(tableName = "homework")
data class HomeworkEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val className: String,
    val subject: String,
    val description: String,
    val dueDate: String,
    val assignedBy: String,
    val datePosted: String,
    val driveUrl: String = ""
)

@Entity(tableName = "homework_submissions")
data class HomeworkSubmissionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val homeworkId: Int,
    val studentId: String,
    val studentName: String,
    val submissionText: String,
    val submissionDate: String,
    val status: String = "Submitted", // Submitted, Graded
    val grade: String = "Pending"
)

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: String,
    val studentName: String,
    val rollNumber: String,
    val className: String,
    val date: String,
    val isPresent: Boolean
)

@Entity(tableName = "timetable")
data class TimetableEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val className: String,
    val dayOfWeek: String, // Monday, Tuesday...
    val periodNumber: Int,
    val timeSlot: String,
    val subject: String,
    val teacherName: String
)

@Entity(tableName = "doubts")
data class DoubtEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firebaseId: String = "",
    val studentId: String,
    val studentName: String,
    val className: String,
    val subject: String,
    val question: String,
    val date: String,
    val status: String = "Pending", // Pending, Answered
    val replyText: String = "",
    val repliedBy: String = "",
    val replyDate: String = ""
)

@Entity(tableName = "doubt_replies")
data class DoubtReplyEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val doubtId: Int,
    val replierName: String,
    val replierRole: String,
    val replyText: String,
    val date: String
)

@Entity(tableName = "gallery")
data class GalleryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // Event, Campus, Sports, Celebration
    val imageResName: String, // Drawable resource or URL
    val date: String
)

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val date: String,
    val time: String,
    val venue: String,
    val description: String
)

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // Form, Admit Card, Circular, Board Notification
    val fileSize: String,
    val fileType: String = "PDF",
    val driveUrl: String = ""
)

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val actorName: String,
    val actorRole: String,
    val actionType: String, // Login, Logout, Attendance, Homework, Result, Notice, User Management
    val details: String,
    val timestamp: Long = System.currentTimeMillis(),
    val formattedTime: String = "Just now"
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val message: String,
    val isImportant: Boolean = true,
    val createdBy: String = "Super Admin",
    val date: String = "30 Jul 2026"
)

@Entity(tableName = "notifications")
data class NotificationItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val type: String, // Homework, Notice, Results, Events, Chat, Doubt, Announcement
    val timestamp: Long = System.currentTimeMillis(),
    val formattedTime: String = "Just now",
    val isRead: Boolean = false
)

@Entity(tableName = "favorites")
data class FavoriteItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemType: String, // Homework, Notice, StudyMaterial, Download
    val itemId: String,
    val title: String,
    val subtitle: String,
    val url: String = "",
    val dateAdded: String = "Today"
)

