package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SchoolDatabase
import com.example.data.model.*
import com.example.data.repository.SchoolRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ScreenRoute(val route: String, val title: String) {
    object Splash : ScreenRoute("splash", "Splash")
    object Auth : ScreenRoute("auth", "Sign In")
    object Home : ScreenRoute("home", "+2 Govt Mithila High School")
    object AboutSchool : ScreenRoute("about_school", "About School")
    object Results : ScreenRoute("results", "Exam Results")
    object Syllabus : ScreenRoute("syllabus", "Class Syllabus")
    object StudyMaterial : ScreenRoute("study_material", "Study Material & PYQs")
    object Homework : ScreenRoute("homework", "Daily Homework")
    object Attendance : ScreenRoute("attendance", "Attendance Tracker")
    object Timetable : ScreenRoute("timetable", "Class Routine & Exams")
    object DoubtSection : ScreenRoute("doubt_section", "Ask Doubts & QnA")
    object NoticeBoard : ScreenRoute("notice_board", "Official Notice Board")
    object Gallery : ScreenRoute("gallery", "Photo Gallery")
    object Events : ScreenRoute("events", "School Events & Calendar")
    object Downloads : ScreenRoute("downloads", "Forms & Downloads")
    object Profile : ScreenRoute("profile", "User Profile")
    object Settings : ScreenRoute("settings", "App Settings")
    object AdminDashboard : ScreenRoute("admin_dashboard", "Admin Control Panel")
    object SuperAdminDashboard : ScreenRoute("super_admin_dashboard", "Super Admin HQ")
    object TeacherDashboard : ScreenRoute("teacher_dashboard", "Teacher Portal")
    object StudentDashboard : ScreenRoute("student_dashboard", "Student Overview")

    companion object {
        fun fromString(routeStr: String): ScreenRoute {
            return when (routeStr) {
                "home" -> Home
                "about_school" -> AboutSchool
                "results" -> Results
                "syllabus" -> Syllabus
                "study_material" -> StudyMaterial
                "homework" -> Homework
                "attendance" -> Attendance
                "timetable" -> Timetable
                "doubt_section" -> DoubtSection
                "notice_board" -> NoticeBoard
                "gallery" -> Gallery
                "events" -> Events
                "downloads" -> Downloads
                "profile" -> Profile
                "settings" -> Settings
                "admin_dashboard" -> AdminDashboard
                "super_admin_dashboard" -> SuperAdminDashboard
                "teacher_dashboard" -> TeacherDashboard
                "student_dashboard" -> StudentDashboard
                else -> Home
            }
        }
    }
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SchoolRepository
    private val firebaseRepository = com.example.data.repository.FirebaseRepository()

    init {
        val dao = SchoolDatabase.getDatabase(application).schoolDao()
        repository = SchoolRepository(dao)

        val fbUser = com.example.data.firebase.FirebaseConfig.auth?.currentUser
        if (fbUser != null && !fbUser.email.isNullOrBlank()) {
            viewModelScope.launch {
                val res = firebaseRepository.authenticateUser(fbUser.email!!, "")
                if (res.isSuccess) {
                    val user = res.getOrNull()!!
                    _currentUser.value = user
                    when (user.role) {
                        UserRole.SUPER_ADMIN -> _currentRoute.value = ScreenRoute.SuperAdminDashboard
                        UserRole.ADMIN -> _currentRoute.value = ScreenRoute.AdminDashboard
                        UserRole.TEACHER -> _currentRoute.value = ScreenRoute.TeacherDashboard
                        UserRole.STUDENT -> _currentRoute.value = ScreenRoute.StudentDashboard
                    }
                }
            }
        }
    }

    // Current User State
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Users list for Super Admin & Admin Management
    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )

    // Navigation State
    private val _currentRoute = MutableStateFlow<ScreenRoute>(ScreenRoute.Splash)
    val currentRoute: StateFlow<ScreenRoute> = _currentRoute.asStateFlow()

    // App Preferences
    val isDarkMode = MutableStateFlow(false)
    val currentLanguage = MutableStateFlow("English")
    val notificationsEnabled = MutableStateFlow(true)

    // Reactive DB Streams
    val notices = repository.allNotices.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val results = repository.allResults.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val syllabusList = repository.allSyllabus.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val studyMaterials = repository.allStudyMaterials.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val homeworkList = repository.allHomework.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val timetables = repository.allTimetables.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val doubts = repository.allDoubts.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val galleryItems = repository.galleryItems.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val events = repository.allEvents.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val downloads = repository.allDownloads.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Selected Class Filter State
    val selectedClass = MutableStateFlow("Class 10")

    fun navigateTo(route: ScreenRoute) {
        val user = _currentUser.value
        if (user != null) {
            if (user.role == UserRole.STUDENT) {
                if (route is ScreenRoute.SuperAdminDashboard || route is ScreenRoute.AdminDashboard || route is ScreenRoute.TeacherDashboard) {
                    _currentRoute.value = ScreenRoute.StudentDashboard
                    return
                }
            } else if (user.role == UserRole.TEACHER) {
                if (route is ScreenRoute.SuperAdminDashboard || route is ScreenRoute.AdminDashboard) {
                    _currentRoute.value = ScreenRoute.TeacherDashboard
                    return
                }
            } else if (user.role == UserRole.ADMIN) {
                if (route is ScreenRoute.SuperAdminDashboard) {
                    _currentRoute.value = ScreenRoute.AdminDashboard
                    return
                }
            }
        }
        _currentRoute.value = route
    }

    fun authenticateUser(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = firebaseRepository.authenticateUser(email, password)
            if (res.isSuccess) {
                val user = res.getOrNull()!!
                if (!user.isEnabled) {
                    onResult(false, "Account disabled by Administrator.")
                    return@launch
                }
                repository.saveUser(user)
                _currentUser.value = user

                // Redirect strictly based on user's role from Firestore
                when (user.role) {
                    UserRole.SUPER_ADMIN -> _currentRoute.value = ScreenRoute.SuperAdminDashboard
                    UserRole.ADMIN -> _currentRoute.value = ScreenRoute.AdminDashboard
                    UserRole.TEACHER -> _currentRoute.value = ScreenRoute.TeacherDashboard
                    UserRole.STUDENT -> _currentRoute.value = ScreenRoute.StudentDashboard
                }
                onResult(true, "Login successful")
            } else {
                // Local DB fallback lookup
                val localUser = repository.getUserByEmail(email.trim().lowercase())
                if (localUser != null) {
                    if (!localUser.isEnabled) {
                        onResult(false, "Account disabled by Administrator.")
                        return@launch
                    }
                    _currentUser.value = localUser
                    when (localUser.role) {
                        UserRole.SUPER_ADMIN -> _currentRoute.value = ScreenRoute.SuperAdminDashboard
                        UserRole.ADMIN -> _currentRoute.value = ScreenRoute.AdminDashboard
                        UserRole.TEACHER -> _currentRoute.value = ScreenRoute.TeacherDashboard
                        UserRole.STUDENT -> _currentRoute.value = ScreenRoute.StudentDashboard
                    }
                    onResult(true, "Signed in successfully")
                } else {
                    onResult(false, res.exceptionOrNull()?.message ?: "Invalid email or password")
                }
            }
        }
    }

    fun signUpUser(name: String, email: String, password: String, role: UserRole, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = firebaseRepository.registerUser(name, email, password, role)
            if (res.isSuccess) {
                val newUser = res.getOrNull()!!
                repository.saveUser(newUser)
                _currentUser.value = newUser
                when (newUser.role) {
                    UserRole.SUPER_ADMIN -> _currentRoute.value = ScreenRoute.SuperAdminDashboard
                    UserRole.ADMIN -> _currentRoute.value = ScreenRoute.AdminDashboard
                    UserRole.TEACHER -> _currentRoute.value = ScreenRoute.TeacherDashboard
                    UserRole.STUDENT -> _currentRoute.value = ScreenRoute.StudentDashboard
                }
                onResult(true, "Account created successfully!")
            } else {
                onResult(false, res.exceptionOrNull()?.message ?: "Sign up failed")
            }
        }
    }

    fun sendPasswordReset(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = firebaseRepository.sendPasswordReset(email)
            if (res.isSuccess) {
                onResult(true, "Password reset instructions sent to $email")
            } else {
                onResult(false, "Failed to send password reset email")
            }
        }
    }

    // Super Admin Actions
    fun createAdmin(name: String, email: String, password: String, permissions: AdminPermissions, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val cleanEmail = email.trim().lowercase()
            val newAdmin = UserEntity(
                id = "adm_${System.currentTimeMillis()}",
                name = name,
                email = cleanEmail,
                role = UserRole.ADMIN,
                permissions = permissions,
                isEnabled = true,
                address = "+2 Govt Mithila High School, Balaur"
            )
            repository.saveUser(newAdmin)
            firebaseRepository.saveUserToFirestore(newAdmin)
            onResult(true, "Admin account for $name created successfully!")
        }
    }

    fun updateAdminPermissions(userId: String, permissions: AdminPermissions) {
        viewModelScope.launch {
            val existing = repository.allUsers.firstOrNull()?.find { it.id == userId }
            if (existing != null) {
                val updated = existing.copy(permissions = permissions)
                repository.saveUser(updated)
                firebaseRepository.updateAdminPermissions(userId, permissions)
            }
        }
    }

    fun toggleUserStatus(userId: String, isEnabled: Boolean) {
        viewModelScope.launch {
            val existing = repository.allUsers.firstOrNull()?.find { it.id == userId }
            if (existing != null) {
                val updated = existing.copy(isEnabled = isEnabled)
                repository.saveUser(updated)
                firebaseRepository.updateUserStatus(userId, isEnabled)
            }
        }
    }

    fun updateUserRole(userId: String, newRole: UserRole) {
        viewModelScope.launch {
            val existing = repository.allUsers.firstOrNull()?.find { it.id == userId }
            if (existing != null) {
                val updated = existing.copy(role = newRole)
                repository.saveUser(updated)
                firebaseRepository.updateUserRole(userId, newRole)
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            repository.deleteUser(userId)
            firebaseRepository.deleteUser(userId)
        }
    }

    fun updateUserProfile(phone: String, address: String, parentName: String, photoUrl: String, onResult: (Boolean, String) -> Unit) {
        val user = _currentUser.value
        if (user == null) {
            onResult(false, "User not logged in")
            return
        }
        viewModelScope.launch {
            val updated = user.copy(
                phone = phone,
                address = address,
                parentName = parentName,
                photoUrl = photoUrl
            )
            repository.saveUser(updated)
            firebaseRepository.saveUserToFirestore(updated)
            _currentUser.value = updated
            onResult(true, "Profile updated in Firestore")
        }
    }

    fun logout() {
        com.example.data.firebase.FirebaseConfig.auth?.signOut()
        _currentUser.value = null
        _currentRoute.value = ScreenRoute.Auth
    }

    fun toggleDarkMode(enabled: Boolean) { isDarkMode.value = enabled }
    fun toggleNotifications(enabled: Boolean) { notificationsEnabled.value = enabled }
    fun setLanguage(lang: String) { currentLanguage.value = lang }

    fun addDoubt(subject: String, question: String) = askDoubt(subject, question)

    // Actions
    fun addNotice(title: String, content: String, category: String) {
        viewModelScope.launch {
            repository.addNotice(
                NoticeEntity(
                    title = title,
                    content = content,
                    date = "30 July 2026",
                    category = category,
                    postedBy = _currentUser.value?.name ?: "School Office"
                )
            )
        }
    }

    fun addHomework(title: String, className: String, subject: String, desc: String, dueDate: String) {
        viewModelScope.launch {
            repository.addHomework(
                HomeworkEntity(
                    title = title,
                    className = className,
                    subject = subject,
                    description = desc,
                    dueDate = dueDate,
                    assignedBy = _currentUser.value?.name ?: "Subject Teacher",
                    datePosted = "30 July 2026"
                )
            )
        }
    }

    fun addStudyMaterial(title: String, className: String, subject: String, type: String, desc: String, url: String) {
        viewModelScope.launch {
            repository.addStudyMaterial(
                StudyMaterialEntity(
                    title = title,
                    className = className,
                    subject = subject,
                    type = type,
                    description = desc,
                    fileOrVideoUrl = url,
                    dateUploaded = "30 July 2026"
                )
            )
        }
    }

    fun askDoubt(subject: String, question: String) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.askDoubt(
                DoubtEntity(
                    studentId = user.id,
                    studentName = user.name,
                    className = user.className,
                    subject = subject,
                    question = question,
                    date = "30 July 2026"
                )
            )
        }
    }

    fun replyDoubt(doubtId: Int, replyText: String) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.replyDoubt(
                DoubtReplyEntity(
                    doubtId = doubtId,
                    replierName = user.name,
                    replierRole = user.role.name,
                    replyText = replyText,
                    date = "30 July 2026"
                )
            )
            repository.updateDoubtStatus(doubtId, "Answered")
        }
    }

    // Result Management Engine
    fun searchExactResult(
        className: String,
        stream: String,
        examName: String,
        rollNumber: String,
        onResult: (ResultEntity?, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Try Firebase query first
                val firebaseResult = firebaseRepository.searchExactResultInFirestore(className, stream, examName, rollNumber)
                if (firebaseResult != null) {
                    onResult(firebaseResult, null)
                    return@launch
                }

                // Fallback to local Room Database query
                val localResult = repository.searchExactResult(className, stream, examName, rollNumber)
                if (localResult != null) {
                    onResult(localResult, null)
                } else {
                    onResult(null, "No Result Found.")
                }
            } catch (e: Exception) {
                onResult(null, "No Result Found.")
            }
        }
    }

    fun saveResult(result: ResultEntity, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                // Save to Firestore
                val firebaseId = firebaseRepository.saveResultToFirestore(result)
                val updatedResult = result.copy(firebaseId = firebaseId)
                // Save to Room DB
                repository.addResult(updatedResult)
                onResult(true, "Result uploaded/saved successfully!")
            } catch (e: Exception) {
                // Save to Room DB as fallback
                repository.addResult(result)
                onResult(true, "Result saved locally!")
            }
        }
    }

    fun togglePublishResult(result: ResultEntity, isPublished: Boolean) {
        viewModelScope.launch {
            val updated = result.copy(isPublished = isPublished)
            repository.addResult(updated)
            if (result.firebaseId.isNotBlank()) {
                firebaseRepository.updateResultPublishedStatus(result.firebaseId, isPublished)
            }
        }
    }

    fun deleteResult(result: ResultEntity) {
        viewModelScope.launch {
            if (result.id > 0) {
                repository.deleteResult(result.id)
            }
            if (result.firebaseId.isNotBlank()) {
                firebaseRepository.deleteResultFromFirestore(result.firebaseId)
            }
        }
    }

    fun markAttendance(className: String, presentRolls: Set<String>) {
        viewModelScope.launch {
            val studentList = listOf(
                Pair("10042", "Aman Kumar"),
                Pair("10043", "Priya Kumari"),
                Pair("10044", "Rahul Sharma"),
                Pair("10045", "Sita Devi"),
                Pair("10046", "Vikram Singh")
            )
            val records = studentList.map { (roll, name) ->
                AttendanceEntity(
                    studentId = "std_$roll",
                    studentName = name,
                    rollNumber = roll,
                    className = className,
                    date = "2026-07-30",
                    isPresent = presentRolls.contains(roll)
                )
            }
            repository.saveAttendanceRecords(records)
        }
    }
}
