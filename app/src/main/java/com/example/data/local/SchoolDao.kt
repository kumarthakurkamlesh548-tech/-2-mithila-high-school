package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {

    // Users
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: UserRole): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // Notices
    @Query("SELECT * FROM notices ORDER BY id DESC")
    fun getAllNotices(): Flow<List<NoticeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: NoticeEntity)

    @Query("DELETE FROM notices WHERE id = :id")
    suspend fun deleteNotice(id: Int)

    // Results
    @Query("SELECT * FROM results WHERE studentId = :studentId OR rollNumber = :rollNumber")
    fun getResultsForStudent(studentId: String, rollNumber: String): Flow<List<ResultEntity>>

    @Query("SELECT * FROM results WHERE className = :className")
    fun getResultsByClass(className: String): Flow<List<ResultEntity>>

    @Query("SELECT * FROM results ORDER BY id DESC")
    fun getAllResults(): Flow<List<ResultEntity>>

    @Query("SELECT * FROM results WHERE className = :className AND (:stream = '' OR stream = :stream) AND examName = :examName AND rollNumber = :rollNumber AND isPublished = 1 LIMIT 1")
    suspend fun searchExactResult(className: String, stream: String, examName: String, rollNumber: String): ResultEntity?

    @Query("UPDATE results SET isPublished = :isPublished WHERE id = :id")
    suspend fun updateResultPublishedStatus(id: Int, isPublished: Boolean)

    @Query("DELETE FROM results WHERE id = :id")
    suspend fun deleteResult(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: ResultEntity)

    // Syllabus
    @Query("SELECT * FROM syllabus WHERE className = :className")
    fun getSyllabusByClass(className: String): Flow<List<SyllabusEntity>>

    @Query("SELECT * FROM syllabus ORDER BY className ASC")
    fun getAllSyllabus(): Flow<List<SyllabusEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyllabus(syllabus: SyllabusEntity)

    // Study Material
    @Query("SELECT * FROM study_materials WHERE className = :className ORDER BY id DESC")
    fun getStudyMaterialByClass(className: String): Flow<List<StudyMaterialEntity>>

    @Query("SELECT * FROM study_materials ORDER BY id DESC")
    fun getAllStudyMaterials(): Flow<List<StudyMaterialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyMaterial(material: StudyMaterialEntity)

    @Query("DELETE FROM study_materials WHERE id = :id")
    suspend fun deleteStudyMaterial(id: Int)

    // Homework
    @Query("SELECT * FROM homework WHERE className = :className ORDER BY id DESC")
    fun getHomeworkByClass(className: String): Flow<List<HomeworkEntity>>

    @Query("SELECT * FROM homework ORDER BY id DESC")
    fun getAllHomework(): Flow<List<HomeworkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomework(homework: HomeworkEntity)

    // Homework Submissions
    @Query("SELECT * FROM homework_submissions WHERE homeworkId = :homeworkId")
    fun getSubmissionsForHomework(homeworkId: Int): Flow<List<HomeworkSubmissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeworkSubmission(submission: HomeworkSubmissionEntity)

    // Attendance
    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceForStudent(studentId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE className = :className AND date = :date")
    fun getAttendanceByClassAndDate(className: String, date: String): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(records: List<AttendanceEntity>)

    // Timetable
    @Query("SELECT * FROM timetable WHERE className = :className ORDER BY periodNumber ASC")
    fun getTimetableByClass(className: String): Flow<List<TimetableEntity>>

    @Query("SELECT * FROM timetable ORDER BY className, periodNumber")
    fun getAllTimetables(): Flow<List<TimetableEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetableItem(item: TimetableEntity)

    // Doubts
    @Query("SELECT * FROM doubts ORDER BY id DESC")
    fun getAllDoubts(): Flow<List<DoubtEntity>>

    @Query("SELECT * FROM doubts WHERE studentId = :studentId ORDER BY id DESC")
    fun getDoubtsForStudent(studentId: String): Flow<List<DoubtEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoubt(doubt: DoubtEntity)

    @Query("UPDATE doubts SET status = :status WHERE id = :id")
    suspend fun updateDoubtStatus(id: Int, status: String)

    @Query("SELECT * FROM doubt_replies WHERE doubtId = :doubtId ORDER BY id ASC")
    fun getRepliesForDoubt(doubtId: Int): Flow<List<DoubtReplyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoubtReply(reply: DoubtReplyEntity)

    // Gallery
    @Query("SELECT * FROM gallery ORDER BY id DESC")
    fun getAllGalleryItems(): Flow<List<GalleryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGalleryItem(item: GalleryEntity)

    // Events
    @Query("SELECT * FROM events ORDER BY id DESC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    // Downloads
    @Query("SELECT * FROM downloads ORDER BY id DESC")
    fun getAllDownloads(): Flow<List<DownloadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadEntity)

    // Chat Rooms
    @Query("SELECT * FROM chat_rooms ORDER BY lastMessageTimestamp DESC")
    fun getAllChatRooms(): Flow<List<ChatRoom>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatRoom(room: ChatRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatRooms(rooms: List<ChatRoom>)

    // Chat Messages
    @Query("SELECT * FROM chat_messages WHERE roomId = :roomId ORDER BY timestamp ASC")
    fun getMessagesForRoom(roomId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessages(messages: List<ChatMessage>)

    @Query("UPDATE chat_messages SET isDeleted = 1 WHERE id = :messageId")
    suspend fun deleteChatMessage(messageId: String)

    // Activity Logs
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC")
    fun getAllActivityLogs(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(log: ActivityLogEntity)

    // Announcements
    @Query("SELECT * FROM announcements ORDER BY id DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItemEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: Int)

    // Favorites
    @Query("SELECT * FROM favorites ORDER BY id DESC")
    fun getAllFavorites(): Flow<List<FavoriteItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteItemEntity)

    @Query("DELETE FROM favorites WHERE itemType = :itemType AND itemId = :itemId")
    suspend fun removeFavorite(itemType: String, itemId: String)
}

