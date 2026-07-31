package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FavoriteItemEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDarkBlue

@Composable
fun FavoritesScreen(
    favorites: List<FavoriteItemEntity>,
    onRemoveFavorite: (String, String) -> Unit,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        Surface(
            color = PrimaryDarkBlue,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Bookmark, contentDescription = "Bookmarks", tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Bookmarks & Favorites", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("${favorites.size} saved items for offline reference", fontSize = 11.sp, color = Color.White.copy(0.8f))
                }
            }
        }

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    title = "No Bookmarked Items",
                    subtitle = "Tap the bookmark icon on any Homework, Notice, or Study Material to save it here.",
                    icon = Icons.Default.BookmarkBorder
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(favorites, key = { "${it.itemType}_${it.itemId}" }) { fav ->
                    val icon = when (fav.itemType) {
                        "Homework" -> Icons.Default.Assignment
                        "Notice" -> Icons.Default.Campaign
                        "StudyMaterial" -> Icons.Default.MenuBook
                        else -> Icons.Default.Description
                    }

                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = PrimaryBlue.copy(alpha = 0.12f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(icon, contentDescription = fav.itemType, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = PrimaryDarkBlue.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = fav.itemType,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryDarkBlue,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(fav.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                                Text(fav.subtitle, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
                            }

                            if (fav.url.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fav.url))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            // Fallback
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.OpenInNew, contentDescription = "Open Drive Link", tint = PrimaryBlue)
                                }
                            }

                            IconButton(
                                onClick = { onRemoveFavorite(fav.itemType, fav.itemId) }
                            ) {
                                Icon(Icons.Default.BookmarkRemove, contentDescription = "Remove", tint = Color(0xFFDC2626))
                            }
                        }
                    }
                }
            }
        }
    }
}
