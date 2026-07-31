package com.example.data.repository

import com.example.data.local.SchoolDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class SchoolRepository(private val dao: SchoolDao) {

    // Auth & Users
    suspend fun getUserByEmail(email: String) = dao.getUserByEmail(email)
    fun getUserById(id: String) = dao.getUserById(id)
    fun getUsersByRole(role: UserRole) = dao.getUsersByRole(role)
    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()
    suspend fun saveUser(user: UserEntity) = dao.insertUser(user)
    suspend fun deleteUser(id: String) = dao.deleteUser(id)

    // Notices
    val allNotices: Flow<List<NoticeEntity>> = dao.getAllNotices()
    suspend fun addNotice(notice: NoticeEntity) = dao.insertNotice(notice)
    suspend fun deleteNotice(id: Int) = dao.deleteNotice(id)

    // Results
    fun getResultsForStudent(studentId: String, rollNumber: String) = dao.getResultsForStudent(studentId, rollNumber)
    fun getResultsByClass(className: String) = dao.getResultsByClass(className)
    val allResults = dao.getAllResults()
    suspend fun addResult(result: ResultEntity) = dao.insertResult(result)
    suspend fun searchExactResult(className: String, stream: String, examName: String, rollNumber: String) =
        dao.searchExactResult(className, stream, examName, rollNumber)
    suspend fun updateResultPublishedStatus(id: Int, isPublished: Boolean) =
        dao.updateResultPublishedStatus(id, isPublished)
    suspend fun deleteResult(id: Int) = dao.deleteResult(id)

    // Syllabus
    fun getSyllabusByClass(className: String) = dao.getSyllabusByClass(className)
    val allSyllabus = dao.getAllSyllabus()
    suspend fun addSyllabus(syllabus: SyllabusEntity) = dao.insertSyllabus(syllabus)

    // Study Materials
    fun getStudyMaterialByClass(className: String) = dao.getStudyMaterialByClass(className)
    val allStudyMaterials = dao.getAllStudyMaterials()
    suspend fun addStudyMaterial(material: StudyMaterialEntity) = dao.insertStudyMaterial(material)
    suspend fun deleteStudyMaterial(id: Int) = dao.deleteStudyMaterial(id)

    // Homework
    fun getHomeworkByClass(className: String) = dao.getHomeworkByClass(className)
    val allHomework = dao.getAllHomework()
    suspend fun addHomework(homework: HomeworkEntity) = dao.insertHomework(homework)

    // Homework Submissions
    fun getSubmissionsForHomework(homeworkId: Int) = dao.getSubmissionsForHomework(homeworkId)
    suspend fun submitHomework(submission: HomeworkSubmissionEntity) = dao.insertHomeworkSubmission(submission)

    // Attendance
    fun getAttendanceForStudent(studentId: String) = dao.getAttendanceForStudent(studentId)
    fun getAttendanceByClassAndDate(className: String, date: String) = dao.getAttendanceByClassAndDate(className, date)
    suspend fun saveAttendanceRecords(records: List<AttendanceEntity>) = dao.insertAttendanceList(records)

    // Timetable
    fun getTimetableByClass(className: String) = dao.getTimetableByClass(className)
    val allTimetables = dao.getAllTimetables()
    suspend fun addTimetableItem(item: TimetableEntity) = dao.insertTimetableItem(item)

    // Doubts
    val allDoubts = dao.getAllDoubts()
    fun getDoubtsForStudent(studentId: String) = dao.getDoubtsForStudent(studentId)
    suspend fun askDoubt(doubt: DoubtEntity) = dao.insertDoubt(doubt)
    suspend fun updateDoubtStatus(doubtId: Int, status: String) = dao.updateDoubtStatus(doubtId, status)
    fun getRepliesForDoubt(doubtId: Int) = dao.getRepliesForDoubt(doubtId)
    suspend fun replyDoubt(reply: DoubtReplyEntity) = dao.insertDoubtReply(reply)

    // Gallery
    val galleryItems = dao.getAllGalleryItems()
    suspend fun addGalleryItem(item: GalleryEntity) = dao.insertGalleryItem(item)

    // Events
    val allEvents = dao.getAllEvents()
    suspend fun addEvent(event: EventEntity) = dao.insertEvent(event)

    // Downloads
    val allDownloads = dao.getAllDownloads()
    suspend fun addDownload(download: DownloadEntity) = dao.insertDownload(download)

    // Chat
    val allChatRooms: Flow<List<ChatRoom>> = dao.getAllChatRooms()
    suspend fun saveChatRoom(room: ChatRoom) = dao.insertChatRoom(room)
    suspend fun saveChatRooms(rooms: List<ChatRoom>) = dao.insertChatRooms(rooms)
    fun getMessagesForRoom(roomId: String): Flow<List<ChatMessage>> = dao.getMessagesForRoom(roomId)
    suspend fun saveChatMessage(message: ChatMessage) = dao.insertChatMessage(message)
    suspend fun saveChatMessages(messages: List<ChatMessage>) = dao.insertChatMessages(messages)
    suspend fun deleteChatMessage(messageId: String) = dao.deleteChatMessage(messageId)
}
