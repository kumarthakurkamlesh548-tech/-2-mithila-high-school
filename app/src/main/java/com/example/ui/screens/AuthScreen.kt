package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserRole
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue
import com.example.ui.theme.SecondaryBlue

@Composable
fun AuthScreen(
    onAuthenticate: (email: String, password: String, onResult: (Boolean, String) -> Unit) -> Unit,
    onSignUp: (name: String, email: String, password: String, role: UserRole, onResult: (Boolean, String) -> Unit) -> Unit,
    onForgotPassword: (email: String, onResult: (Boolean, String) -> Unit) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Sign In, 1: Sign Up, 2: Forgot Password
    var signUpRole by remember { mutableStateOf(UserRole.STUDENT) } // Only Student & Teacher allowed on public sign up

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FBFF))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
        ) {
            // School Branding Header
            Image(
                painter = painterResource(id = R.drawable.img_school_logo),
                contentDescription = "School Logo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "+2 Govt Mithila High School",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkBlue,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Balaur, Manigachhi, Darbhanga, Bihar",
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Glassmorphic Auth Card
            GlassmorphicCard(
                cornerRadius = 24.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Auth Tab Selector (Sign In, Sign Up, Forgot Password)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SecondaryBlue)
                            .padding(4.dp)
                    ) {
                        listOf("Sign In", "Sign Up", "Forgot Password").forEachIndexed { idx, tabLabel ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selectedTab == idx) PrimaryBlue else Color.Transparent)
                                    .clickable {
                                        selectedTab = idx
                                        errorMessage = null
                                        successMessage = null
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tabLabel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == idx) Color.White else PrimaryDarkBlue,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (selectedTab == 1) { // Sign Up Fields
                        Surface(
                            color = PrimaryBlue.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.School,
                                    contentDescription = "Student Registration",
                                    tint = PrimaryDarkBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Student Registration Portal",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryDarkBlue
                                    )
                                    Text(
                                        text = "Teachers & Admins are registered directly by Super Admin",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Student Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    if (selectedTab != 2) { // Password field for Sign In & Sign Up
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )
                    }

                    // Error & Success Feedback
                    AnimatedVisibility(visible = errorMessage != null) {
                        errorMessage?.let {
                            Text(
                                text = "⚠️ $it",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                    }

                    AnimatedVisibility(visible = successMessage != null) {
                        successMessage?.let {
                            Text(
                                text = "✅ $it",
                                fontSize = 12.sp,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Button(
                        onClick = {
                            errorMessage = null
                            successMessage = null

                            if (email.isBlank()) {
                                errorMessage = "Please enter your email address"
                                return@Button
                            }

                            isLoading = true

                            when (selectedTab) {
                                0 -> { // Sign In
                                    if (password.isBlank()) {
                                        errorMessage = "Please enter your password"
                                        isLoading = false
                                        return@Button
                                    }
                                    onAuthenticate(email, password) { success, msg ->
                                        isLoading = false
                                        if (!success) {
                                            errorMessage = msg
                                        }
                                    }
                                }
                                1 -> { // Sign Up
                                    if (name.isBlank() || password.isBlank()) {
                                        errorMessage = "Please fill in all details"
                                        isLoading = false
                                        return@Button
                                    }
                                    onSignUp(name, email, password, UserRole.STUDENT) { success, msg ->
                                        isLoading = false
                                        if (success) successMessage = msg else errorMessage = msg
                                    }
                                }
                                2 -> { // Forgot Password
                                    onForgotPassword(email) { success, msg ->
                                        isLoading = false
                                        if (success) successMessage = msg else errorMessage = msg
                                    }
                                }
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = when (selectedTab) {
                                    0 -> "Sign In"
                                    1 -> "Create Account"
                                    else -> "Send Password Reset Link"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Protected by Firebase Auth & Cloud Firestore Security Rules",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )
        }
    }
}
