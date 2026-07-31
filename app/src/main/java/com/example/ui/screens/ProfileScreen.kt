package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.UserEntity
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.InfoRow
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue

@Composable
fun ProfileScreen(
    currentUser: UserEntity?,
    onUpdateProfile: (phone: String, address: String, parentName: String, photoUrl: String, callback: (Boolean, String) -> Unit) -> Unit = { _, _, _, _, _ -> }
) {
    val user = currentUser ?: return
    var showEditDialog by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                GlassmorphicCard(cornerRadius = 24.dp) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (user.photoUrl.isNotBlank()) {
                                AsyncImage(
                                    model = user.photoUrl,
                                    contentDescription = "Profile Photo",
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(CircleShape)
                                        .border(3.dp, PrimaryBlue, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.img_school_logo),
                                    contentDescription = "Profile Photo",
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(CircleShape)
                                        .border(3.dp, PrimaryBlue, CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = user.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                        Text(text = user.email, fontSize = 12.sp, color = Color(0xFF64748B))

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = PrimaryBlue,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "ROLE: ${user.role.name}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { showEditDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profile Details", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Academic Credentials & Details",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkBlue,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                GlassmorphicCard(cornerRadius = 20.dp) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        InfoRow(icon = Icons.Default.Badge, title = "Roll Number", value = user.rollNumber.ifEmpty { "N/A" })
                        Divider(color = GlassBorder)
                        InfoRow(icon = Icons.Default.ConfirmationNumber, title = "Admission Number", value = user.admissionNumber.ifEmpty { "N/A" })
                        Divider(color = GlassBorder)
                        InfoRow(icon = Icons.Default.Class, title = "Class & Section", value = "${user.className} (${user.section})")
                        Divider(color = GlassBorder)
                        InfoRow(icon = Icons.Default.FamilyRestroom, title = "Parent / Guardian Name", value = user.parentName.ifEmpty { "Not Provided" })
                        Divider(color = GlassBorder)
                        InfoRow(icon = Icons.Default.Phone, title = "Mobile Contact", value = user.phone.ifEmpty { "Not Provided" })
                        Divider(color = GlassBorder)
                        InfoRow(icon = Icons.Default.Place, title = "Permanent Address", value = user.address.ifEmpty { "Not Provided" })
                    }
                }
            }
        }

        // EDIT PROFILE MODAL DIALOG
        if (showEditDialog) {
            EditProfileDialog(
                user = user,
                onDismiss = { showEditDialog = false },
                onSave = { phone, address, parentName, photoUrl ->
                    onUpdateProfile(phone, address, parentName, photoUrl) { success, msg ->
                        if (success) {
                            showEditDialog = false
                            snackbarMessage = "Profile updated in Firestore!"
                        } else {
                            snackbarMessage = msg
                        }
                    }
                }
            )
        }

        if (snackbarMessage.isNotEmpty()) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = { TextButton(onClick = { snackbarMessage = "" }) { Text("OK", color = Color.White) } }
            ) {
                Text(snackbarMessage)
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    user: UserEntity,
    onDismiss: () -> Unit,
    onSave: (phone: String, address: String, parentName: String, photoUrl: String) -> Unit
) {
    var phone by remember { mutableStateOf(user.phone) }
    var address by remember { mutableStateOf(user.address) }
    var parentName by remember { mutableStateOf(user.parentName) }
    var photoUrl by remember { mutableStateOf(user.photoUrl) }
    var isSaving by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Edit Profile Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                Text("Changes will be saved to your Firestore account", fontSize = 11.sp, color = Color(0xFF64748B))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile Contact Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryBlue) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Permanent Address") },
                    leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = PrimaryBlue) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = parentName,
                    onValueChange = { parentName = it },
                    label = { Text("Parent / Guardian Name") },
                    leadingIcon = { Icon(Icons.Default.FamilyRestroom, contentDescription = null, tint = PrimaryBlue) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = photoUrl,
                    onValueChange = { photoUrl = it },
                    label = { Text("Profile Photo URL") },
                    leadingIcon = { Icon(Icons.Default.Image, contentDescription = null, tint = PrimaryBlue) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color(0xFF64748B))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            isSaving = true
                            onSave(phone, address, parentName, photoUrl)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text("Save to Firestore", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
