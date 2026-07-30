package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.AdminPermissions
import com.example.data.model.UserRole
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val permissionsAdapter = moshi.adapter(AdminPermissions::class.java)

    @TypeConverter
    fun fromAdminPermissions(permissions: AdminPermissions?): String {
        return permissionsAdapter.toJson(permissions ?: AdminPermissions())
    }

    @TypeConverter
    fun toAdminPermissions(json: String?): AdminPermissions {
        return if (json.isNullOrEmpty()) {
            AdminPermissions()
        } else {
            try {
                permissionsAdapter.fromJson(json) ?: AdminPermissions()
            } catch (e: Exception) {
                AdminPermissions()
            }
        }
    }

    @TypeConverter
    fun fromUserRole(role: UserRole?): String {
        return role?.name ?: UserRole.STUDENT.name
    }

    @TypeConverter
    fun toUserRole(name: String?): UserRole {
        return try {
            if (name == null) UserRole.STUDENT else UserRole.valueOf(name)
        } catch (e: Exception) {
            UserRole.STUDENT
        }
    }
}
