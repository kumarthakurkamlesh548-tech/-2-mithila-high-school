package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import androidx.room.TypeConverters

@Database(
    entities = [
        UserEntity::class,
        NoticeEntity::class,
        ResultEntity::class,
        SyllabusEntity::class,
        StudyMaterialEntity::class,
        HomeworkEntity::class,
        HomeworkSubmissionEntity::class,
        AttendanceEntity::class,
        TimetableEntity::class,
        DoubtEntity::class,
        DoubtReplyEntity::class,
        GalleryEntity::class,
        EventEntity::class,
        DownloadEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SchoolDatabase : RoomDatabase() {

    abstract fun schoolDao(): SchoolDao

    companion object {
        @Volatile
        private var INSTANCE: SchoolDatabase? = null

        fun getDatabase(context: Context): SchoolDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SchoolDatabase::class.java,
                    "mithila_school_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedData(database.schoolDao())
                    }
                }
            }
        }

        suspend fun seedData(dao: SchoolDao) {
            // Seed Users
            dao.insertUser(
                UserEntity(
                    id = "std_001",
                    name = "Aman Kumar",
                    email = "student@mithilahs.edu.in",
                    role = UserRole.STUDENT,
                    rollNumber = "10042",
                    admissionNumber = "MHS-2023-089",
                    className = "Class 10",
                    section = "A",
                    parentName = "Ramesh Kumar Thakur",
                    phone = "+91 9835412890",
                    address = "Village Balaur, PO Manigachhi, District Darbhanga, Bihar 847422"
                )
            )
            dao.insertUser(
                UserEntity(
                    id = "tch_001",
                    name = "Prof. Dr. R. K. Jha",
                    email = "teacher@mithilahs.edu.in",
                    role = UserRole.TEACHER,
                    className = "Class 10 & 12",
                    section = "All",
                    phone = "+91 9431089230",
                    address = "Darbhanga Town, Bihar"
                )
            )
            dao.insertUser(
                UserEntity(
                    id = "adm_001",
                    name = "Principal Office (Admin)",
                    email = "admin@mithilahs.edu.in",
                    role = UserRole.ADMIN,
                    phone = "06272-234567",
                    address = "+2 Govt Mithila High School Balaur, Manigachhi, Darbhanga"
                )
            )
            dao.insertUser(
                UserEntity(
                    id = "super_001",
                    name = "Super Administrator",
                    email = "superadmin@mithilahs.edu.in",
                    role = UserRole.SUPER_ADMIN,
                    phone = "06272-999999",
                    address = "Central Admin HQ, Balaur, Mithila"
                )
            )

            // Seed Notices
            dao.insertNotice(
                NoticeEntity(
                    title = "BSEB Class 10 & 12 Board Exam Registration Form Submission",
                    content = "All students of Class 10 and Class 12 are instructed to verify their registration details for the Bihar School Examination Board (BSEB) upcoming examinations. Submit passport photos at counter #2 by Friday.",
                    date = "30 July 2026",
                    category = "Exam",
                    postedBy = "Principal Office"
                )
            )
            dao.insertNotice(
                NoticeEntity(
                    title = "Independence Day Grand Cultural Celebration & Flag Hoisting",
                    content = "The annual Independence Day celebration will take place at the main school playground on 15th August at 8:00 AM. Attendance is mandatory for all students and teaching staff.",
                    date = "28 July 2026",
                    category = "Event",
                    postedBy = "Sports & Cultural Committee"
                )
            )
            dao.insertNotice(
                NoticeEntity(
                    title = "Distribution of Free Textbooks & Uniform Stipend (Class 9 & 10)",
                    content = "Government Bihar Education Department free textbook distribution counter will open this Monday in Room 12. Bring Student ID Card.",
                    date = "25 July 2026",
                    category = "Academic",
                    postedBy = "Academic Office"
                )
            )

            // Seed Results
            dao.insertResult(
                ResultEntity(
                    studentId = "std_001",
                    studentName = "Aman Kumar",
                    rollNumber = "10042",
                    registrationNumber = "26102384912",
                    className = "Class 10",
                    section = "A",
                    stream = "",
                    examName = "Annual Examination",
                    academicSession = "2025-2026",
                    uploadDate = "30 Jul 2026",
                    isPublished = true,
                    scienceMarks = 88,
                    mathMarks = 92,
                    socialScienceMarks = 84,
                    hindiMarks = 90,
                    englishMarks = 82,
                    maithiliMarks = 94,
                    totalMarks = 530,
                    maxMarks = 600,
                    percentage = 88.33,
                    grade = "A+",
                    remarks = "1st Division with Distinction"
                )
            )
            dao.insertResult(
                ResultEntity(
                    studentId = "std_011",
                    studentName = "Priya Kumari",
                    rollNumber = "11005",
                    registrationNumber = "26118492015",
                    className = "Class 11",
                    section = "A",
                    stream = "Science",
                    examName = "Annual Examination",
                    academicSession = "2025-2026",
                    uploadDate = "30 Jul 2026",
                    isPublished = true,
                    scienceMarks = 92, // Physics
                    mathMarks = 95,    // Chemistry
                    socialScienceMarks = 90, // Biology / Math
                    hindiMarks = 88,
                    englishMarks = 86,
                    maithiliMarks = 89,
                    totalMarks = 540,
                    maxMarks = 600,
                    percentage = 90.00,
                    grade = "A+",
                    remarks = "1st Division with Distinction"
                )
            )
            dao.insertResult(
                ResultEntity(
                    studentId = "std_012",
                    studentName = "Rahul Sharma",
                    rollNumber = "12018",
                    registrationNumber = "26120492811",
                    className = "Class 12",
                    section = "B",
                    stream = "Commerce",
                    examName = "Board Examination",
                    academicSession = "2025-2026",
                    uploadDate = "30 Jul 2026",
                    isPublished = true,
                    scienceMarks = 85, // Accountancy
                    mathMarks = 88,    // Business Studies
                    socialScienceMarks = 82, // Economics
                    hindiMarks = 80,
                    englishMarks = 85,
                    maithiliMarks = 84,
                    totalMarks = 504,
                    maxMarks = 600,
                    percentage = 84.00,
                    grade = "A",
                    remarks = "1st Division"
                )
            )

            // Seed Syllabus
            val classes = listOf("Class 9", "Class 10", "Class 11", "Class 12")
            classes.forEach { cls ->
                dao.insertSyllabus(
                    SyllabusEntity(
                        className = cls,
                        subject = "Mathematics",
                        topics = "Real Numbers, Polynomials, Pair of Linear Equations, Quadratic Equations, Arithmetic Progressions, Triangles, Coordinate Geometry, Trigonometry",
                        downloadUrl = "https://biharboardonline.bihar.gov.in/maths_syllabus.pdf"
                    )
                )
                dao.insertSyllabus(
                    SyllabusEntity(
                        className = cls,
                        subject = "Science (Physics, Chemistry, Biology)",
                        topics = "Chemical Reactions & Equations, Acids Bases & Salts, Life Processes, Control & Coordination, Light Reflection & Refraction, Human Eye, Electricity",
                        downloadUrl = "https://biharboardonline.bihar.gov.in/science_syllabus.pdf"
                    )
                )
                dao.insertSyllabus(
                    SyllabusEntity(
                        className = cls,
                        subject = "Social Science (History, Civics, Geography, Economics)",
                        topics = "Nationalism in India, Resources and Development, Power Sharing, Federalism, Money and Credit, Globalization",
                        downloadUrl = "https://biharboardonline.bihar.gov.in/sst_syllabus.pdf"
                    )
                )
            }

            // Seed Study Material
            dao.insertStudyMaterial(
                StudyMaterialEntity(
                    title = "Class 10 Science Chapter 1 Notes (Hindi/English Medium)",
                    className = "Class 10",
                    subject = "Science",
                    type = "Notes",
                    description = "Handwritten comprehensive revision notes with diagrammatic illustrations for BSEB Board exams.",
                    fileOrVideoUrl = "https://biharboard.ac.in/notes_sci_ch1.pdf",
                    dateUploaded = "28 July 2026"
                )
            )
            dao.insertStudyMaterial(
                StudyMaterialEntity(
                    title = "Class 10 Mathematics - Trigonometric Formulas & Question Bank",
                    className = "Class 10",
                    subject = "Mathematics",
                    type = "Question Bank",
                    description = "Top 50 expected VVI short & long numerical questions for Class 10 BSEB Mathematics.",
                    fileOrVideoUrl = "https://biharboard.ac.in/maths_vvi.pdf",
                    dateUploaded = "26 July 2026"
                )
            )
            dao.insertStudyMaterial(
                StudyMaterialEntity(
                    title = "BSEB Class 10 Previous 10 Years Solved Question Papers",
                    className = "Class 10",
                    subject = "All Subjects",
                    type = "PYQ",
                    description = "Official solved papers from 2016 to 2025 with marking schemes.",
                    fileOrVideoUrl = "https://biharboard.ac.in/pyq_10years.pdf",
                    dateUploaded = "20 July 2026"
                )
            )

            // Seed Homework
            dao.insertHomework(
                HomeworkEntity(
                    title = "Solve Exercise 8.4 Trigonometric Identities (Q1 to Q10)",
                    className = "Class 10",
                    subject = "Mathematics",
                    description = "Complete all proofs in NCERT textbook notebook. Prepare for surprise oral test tomorrow.",
                    dueDate = "02 August 2026",
                    assignedBy = "Dr. R. K. Jha",
                    datePosted = "29 July 2026"
                )
            )
            dao.insertHomework(
                HomeworkEntity(
                    title = "Diagram & Notes on Human Digestive System",
                    className = "Class 10",
                    subject = "Science",
                    description = "Draw neat labeled diagram of human digestive tract and explain enzyme functions in stomach and small intestine.",
                    dueDate = "03 August 2026",
                    assignedBy = "Mrs. S. Roy",
                    datePosted = "28 July 2026"
                )
            )

            // Seed Attendance
            val sampleDates = listOf("2026-07-29", "2026-07-28", "2026-07-27", "2026-07-26", "2026-07-25", "2026-07-24", "2026-07-23")
            sampleDates.forEachIndexed { idx, date ->
                dao.insertAttendanceList(
                    listOf(
                        AttendanceEntity(
                            studentId = "std_001",
                            studentName = "Aman Kumar",
                            rollNumber = "10042",
                            className = "Class 10",
                            date = date,
                            isPresent = idx != 3 // present except 1 day
                        )
                    )
                )
            }

            // Seed Timetable
            val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
            val periods = listOf(
                Pair(1, "09:30 AM - 10:15 AM to Mathematics"),
                Pair(2, "10:15 AM - 11:00 AM to Science (Physics)"),
                Pair(3, "11:00 AM - 11:45 AM to Social Science"),
                Pair(4, "11:45 AM - 12:30 PM to Hindi / Maithili"),
                Pair(5, "01:00 PM - 01:45 PM to English"),
                Pair(6, "01:45 PM - 02:30 PM to Computer / Practical Lab")
            )
            days.forEach { day ->
                periods.forEach { (pNum, text) ->
                    val parts = text.split(" to ")
                    dao.insertTimetableItem(
                        TimetableEntity(
                            className = "Class 10",
                            dayOfWeek = day,
                            periodNumber = pNum,
                            timeSlot = parts[0],
                            subject = parts[1],
                            teacherName = when(pNum) {
                                1 -> "Dr. R. K. Jha"
                                2 -> "Mrs. S. Roy"
                                3 -> "Mr. B. K. Singh"
                                4 -> "Mr. N. K. Mishra"
                                5 -> "Ms. P. Sharma"
                                else -> "Mr. A. Kumar"
                            }
                        )
                    )
                }
            }

            // Seed Doubts
            dao.insertDoubt(
                DoubtEntity(
                    studentId = "std_001",
                    studentName = "Aman Kumar",
                    className = "Class 10",
                    subject = "Mathematics",
                    question = "Sir, how to prove that square root of 5 is an irrational number using contradiction method for board exam?",
                    date = "29 July 2026",
                    status = "Answered"
                )
            )
            dao.insertDoubtReply(
                DoubtReplyEntity(
                    doubtId = 1,
                    replierName = "Dr. R. K. Jha",
                    replierRole = "Teacher",
                    replyText = "Assume sqrt(5) = a/b where a and b are co-prime integers (b != 0). Squaring both sides yields 5b^2 = a^2. Hence 5 divides a^2, which implies 5 divides a. Substitute a = 5c and solve to find 5 also divides b, contradicting that a and b are co-prime!",
                    date = "29 July 2026"
                )
            )

            // Seed Gallery
            dao.insertGalleryItem(
                GalleryEntity(
                    title = "+2 Govt Mithila High School Campus Overview",
                    category = "Campus",
                    imageResName = "img_school_banner",
                    date = "2026"
                )
            )
            dao.insertGalleryItem(
                GalleryEntity(
                    title = "Principal's Office & Academic Administrative Block",
                    category = "Campus",
                    imageResName = "img_principal",
                    date = "2026"
                )
            )
            dao.insertGalleryItem(
                GalleryEntity(
                    title = "School Crest & Official Crest Seal",
                    category = "Celebration",
                    imageResName = "img_school_logo",
                    date = "2026"
                )
            )

            // Seed Events
            dao.insertEvent(
                EventEntity(
                    title = "Annual Science & Innovation Exhibition 2026",
                    date = "10 August 2026",
                    time = "10:00 AM - 04:00 PM",
                    venue = "School Science Block & Auditorium",
                    description = "Students from Classes 9-12 will display working models on renewable energy, robotics, and agricultural innovation."
                )
            )
            dao.insertEvent(
                EventEntity(
                    title = "Inter-School Sports Meet & Athletics Championship",
                    date = "25 August 2026",
                    time = "08:30 AM",
                    venue = "Balaur High School Main Ground",
                    description = "Track events, kabaddi, volleyball, and football tournaments among Darbhanga district schools."
                )
            )

            // Seed Downloads
            dao.insertDownload(
                DownloadEntity(
                    title = "Class 9 to 12 Admission Application Form 2026-27",
                    category = "Form",
                    fileSize = "1.2 MB"
                )
            )
            dao.insertDownload(
                DownloadEntity(
                    title = "BSEB Board Examination Sample Admit Card & Guidelines",
                    category = "Admit Card",
                    fileSize = "850 KB"
                )
            )
            dao.insertDownload(
                DownloadEntity(
                    title = "Bihar Post-Matric Scholarship Scheme Application Guidelines",
                    category = "Circular",
                    fileSize = "2.1 MB"
                )
            )
        }
    }
}
