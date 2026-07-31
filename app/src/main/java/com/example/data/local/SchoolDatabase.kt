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
        DownloadEntity::class,
        ChatRoom::class,
        ChatMessage::class,
        ActivityLogEntity::class,
        AnnouncementEntity::class,
        NotificationItemEntity::class,
        FavoriteItemEntity::class
    ],
    version = 6,
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
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
