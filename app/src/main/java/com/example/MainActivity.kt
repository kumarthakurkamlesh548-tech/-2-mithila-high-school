package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.data.model.UserRole
import com.example.ui.components.AppHeader
import com.example.ui.components.NavigationDrawerContent
import com.example.ui.components.SchoolBottomNav
import com.example.ui.screens.*
import com.example.ui.theme.MithilaSchoolTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ScreenRoute
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val currentRoute by viewModel.currentRoute.collectAsState()
            val currentUser by viewModel.currentUser.collectAsState()
            val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
            val currentLanguage by viewModel.currentLanguage.collectAsState()

            val allUsers by viewModel.allUsers.collectAsState()
            val notices by viewModel.notices.collectAsState()
            val results by viewModel.results.collectAsState()
            val syllabusList by viewModel.syllabusList.collectAsState()
            val studyMaterials by viewModel.studyMaterials.collectAsState()
            val homeworkList by viewModel.homeworkList.collectAsState()
            val timetables by viewModel.timetables.collectAsState()
            val doubts by viewModel.doubts.collectAsState()
            val chatRooms by viewModel.chatRooms.collectAsState()
            val currentRoomMessages by viewModel.currentRoomMessages.collectAsState()
            val userPresences by viewModel.userPresences.collectAsState()
            val activeRoomId by viewModel.activeRoomId.collectAsState()
            val galleryItems by viewModel.galleryItems.collectAsState()
            val events by viewModel.events.collectAsState()
            val downloads by viewModel.downloads.collectAsState()

            MithilaSchoolTheme(darkTheme = isDarkMode) {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            NavigationDrawerContent(
                                currentRoute = currentRoute,
                                currentUser = currentUser,
                                onNavigate = { route -> viewModel.navigateTo(route) },
                                onCloseDrawer = { scope.launch { drawerState.close() } }
                            )
                        }
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF7FBFF),
                        topBar = {
                            if (currentRoute !in listOf(ScreenRoute.Splash, ScreenRoute.Auth)) {
                                AppHeader(
                                    title = currentRoute.title,
                                    currentUser = currentUser,
                                    onMenuClick = { scope.launch { drawerState.open() } },
                                    onProfileClick = { viewModel.navigateTo(ScreenRoute.Profile) }
                                )
                            }
                        },
                        bottomBar = {
                            if (currentRoute !in listOf(ScreenRoute.Splash, ScreenRoute.Auth)) {
                                SchoolBottomNav(
                                    currentRoute = currentRoute,
                                    userRole = currentUser?.role,
                                    onNavigate = { route -> viewModel.navigateTo(route) }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            AnimatedContent(
                                targetState = currentRoute,
                                transitionSpec = {
                                    fadeIn() togetherWith fadeOut()
                                },
                                label = "ScreenTransition"
                            ) { targetRoute ->
                                when (targetRoute) {
                                    ScreenRoute.Splash -> SplashScreen(
                                        onContinue = { viewModel.navigateTo(ScreenRoute.Auth) }
                                    )

                                    ScreenRoute.Auth -> AuthScreen(
                                        onAuthenticate = { email, password, onResult ->
                                            viewModel.authenticateUser(email, password, onResult)
                                        },
                                        onSignUp = { name, email, password, role, onResult ->
                                            viewModel.signUpUser(name, email, password, role, onResult)
                                        },
                                        onForgotPassword = { email, onResult ->
                                            viewModel.sendPasswordReset(email, onResult)
                                        }
                                    )

                                    ScreenRoute.Home -> HomeScreen(
                                        currentUser = currentUser,
                                        notices = notices,
                                        onCardClick = { routeStr -> viewModel.navigateTo(ScreenRoute.fromString(routeStr)) }
                                    )

                                    ScreenRoute.AboutSchool -> AboutSchoolScreen()

                                    ScreenRoute.Results -> ResultsScreen(
                                        results = results,
                                        currentUser = currentUser,
                                        onSearchExact = { cls, stream, exam, roll, onResult ->
                                            viewModel.searchExactResult(cls, stream, exam, roll, onResult)
                                        },
                                        onSaveResult = { result, onResult ->
                                            viewModel.saveResult(result, onResult)
                                        },
                                        onTogglePublish = { result, isPublished ->
                                            viewModel.togglePublishResult(result, isPublished)
                                        },
                                        onDeleteResult = { result ->
                                            viewModel.deleteResult(result)
                                        }
                                    )

                                    ScreenRoute.Syllabus -> SyllabusScreen(
                                        syllabusList = syllabusList
                                    )

                                    ScreenRoute.StudyMaterial -> StudyMaterialScreen(
                                        studyMaterials = studyMaterials
                                    )

                                    ScreenRoute.Homework -> HomeworkScreen(
                                        homeworkList = homeworkList
                                    )

                                    ScreenRoute.Attendance -> AttendanceScreen(
                                        userRole = currentUser?.role,
                                        onSaveAttendance = { className, presentRolls ->
                                            viewModel.markAttendance(className, presentRolls)
                                        }
                                    )

                                    ScreenRoute.Timetable -> TimetableScreen(
                                        timetables = timetables
                                    )

                                    ScreenRoute.Chat -> ChatScreen(
                                        currentUser = currentUser,
                                        chatRooms = chatRooms,
                                        currentRoomMessages = currentRoomMessages,
                                        presences = userPresences,
                                        allUsers = allUsers,
                                        activeRoomId = activeRoomId,
                                        onSelectRoom = { viewModel.selectChatRoom(it) },
                                        onSendMessage = { roomId, text, replyToId, replyToText, replyToSender ->
                                            viewModel.sendChatMessage(roomId, text, replyToId, replyToText, replyToSender)
                                        },
                                        onDeleteMessage = { roomId, messageId ->
                                            viewModel.deleteChatMessage(roomId, messageId)
                                        },
                                        onUpdateTypingStatus = { roomId, isTyping ->
                                            viewModel.updateTypingStatus(roomId, isTyping)
                                        },
                                        onMarkRead = { roomId, messageId ->
                                            viewModel.markMessageRead(roomId, messageId)
                                        },
                                        onCreatePrivateRoom = { targetUser ->
                                            viewModel.createOrSelectPrivateRoom(targetUser)
                                        }
                                    )

                                    ScreenRoute.GeminiChatbot -> GeminiChatbotScreen()

                                    ScreenRoute.DoubtSection -> DoubtSection(
                                        doubts = doubts,
                                        userRole = currentUser?.role,
                                        currentUserId = currentUser?.id ?: "",
                                        currentUserName = currentUser?.name ?: "",
                                        onAskDoubt = { subject, question ->
                                            viewModel.addDoubt(subject, question)
                                        },
                                        onReplyDoubt = { doubtId, reply ->
                                            viewModel.replyDoubt(doubtId, reply)
                                        },
                                        onUpdateStatus = { doubtId, status ->
                                            viewModel.updateDoubtStatus(doubtId, status)
                                        }
                                    )

                                    ScreenRoute.NoticeBoard -> NoticeBoardScreen(
                                        notices = notices,
                                        userRole = currentUser?.role,
                                        onAddNotice = { title, content, cat ->
                                            viewModel.addNotice(title, content, cat)
                                        }
                                    )

                                    ScreenRoute.Gallery -> GalleryScreen(
                                        galleryItems = galleryItems
                                    )

                                    ScreenRoute.Events -> EventsScreen(
                                        events = events
                                    )

                                    ScreenRoute.Downloads -> DownloadsScreen(
                                        downloads = downloads
                                    )

                                    ScreenRoute.Profile -> ProfileScreen(
                                        currentUser = currentUser,
                                        onUpdateProfile = { phone, address, parentName, photoUrl, callback ->
                                            viewModel.updateUserProfile(phone, address, parentName, photoUrl, callback)
                                        }
                                    )

                                    ScreenRoute.Settings -> SettingsScreen(
                                        isDarkMode = isDarkMode,
                                        onToggleDarkMode = { viewModel.toggleDarkMode(it) },
                                        notificationsEnabled = notificationsEnabled,
                                        onToggleNotifications = { viewModel.toggleNotifications(it) },
                                        currentLanguage = currentLanguage,
                                        onSelectLanguage = { viewModel.setLanguage(it) },
                                        onLogout = { viewModel.logout() }
                                    )

                                    ScreenRoute.AdminDashboard -> AdminDashboardScreen(
                                        currentUser = currentUser,
                                        onNavigate = { routeStr -> viewModel.navigateTo(ScreenRoute.fromString(routeStr)) }
                                    )

                                    ScreenRoute.SuperAdminDashboard -> SuperAdminDashboardScreen(
                                        users = allUsers,
                                        onNavigate = { routeStr -> viewModel.navigateTo(ScreenRoute.fromString(routeStr)) },
                                        onCreateAdmin = { name, email, pass, perms, onResult ->
                                            viewModel.createAdmin(name, email, pass, perms, onResult)
                                        },
                                        onUpdatePermissions = { userId, perms ->
                                            viewModel.updateAdminPermissions(userId, perms)
                                        },
                                        onToggleStatus = { userId, isEnabled ->
                                            viewModel.toggleUserStatus(userId, isEnabled)
                                        },
                                        onUpdateRole = { userId, newRole ->
                                            viewModel.updateUserRole(userId, newRole)
                                        },
                                        onDeleteUser = { userId ->
                                            viewModel.deleteUser(userId)
                                        },
                                        onSendPasswordReset = { email, onResult ->
                                            viewModel.sendPasswordReset(email, onResult)
                                        }
                                    )

                                    ScreenRoute.TeacherDashboard -> TeacherDashboardScreen(
                                        onNavigate = { routeStr -> viewModel.navigateTo(ScreenRoute.fromString(routeStr)) },
                                        onUploadHomeworkClick = {
                                            viewModel.addHomework("Mathematics Integration HW", "Class 10", "Mathematics", "Solve Exercises 7.1 and 7.2", "05 Aug 2026")
                                        },
                                        onUploadMaterialClick = {
                                            viewModel.addStudyMaterial("Physics Mechanics Notes", "Class 10", "Physics", "PDF", "Formula sheet for Force & Motion", "https://mithilahs.edu.in/notes.pdf")
                                        },
                                        onUploadResultClick = {
                                            viewModel.saveResult(
                                                com.example.data.model.ResultEntity(
                                                    studentName = "Priya Kumari",
                                                    rollNumber = "10043",
                                                    className = "Class 10",
                                                    examName = "Annual Examination",
                                                    scienceMarks = 96,
                                                    mathMarks = 94,
                                                    socialScienceMarks = 92,
                                                    hindiMarks = 98,
                                                    englishMarks = 90,
                                                    maithiliMarks = 95,
                                                    totalMarks = 565,
                                                    remarks = "1st Division with Distinction"
                                                )
                                            ) { _, _ -> }
                                        }
                                    )

                                    ScreenRoute.StudentDashboard -> StudentDashboardScreen(
                                        currentUser = currentUser,
                                        onNavigate = { routeStr -> viewModel.navigateTo(ScreenRoute.fromString(routeStr)) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
