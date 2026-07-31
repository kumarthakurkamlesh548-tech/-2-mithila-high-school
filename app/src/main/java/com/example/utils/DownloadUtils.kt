package com.example.utils

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

object DownloadUtils {

    /**
     * Saves a text/PDF file or document directly into the Android public Downloads directory.
     */
    fun saveToDownloads(context: Context, filename: String, content: String, mimeType: String = "application/pdf") {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri: Uri? = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(content.toByteArray(Charsets.UTF_8))
                    }
                    Toast.makeText(context, "Saved to Downloads: $filename", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Failed to save file to Downloads", Toast.LENGTH_SHORT).show()
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }
                val file = File(downloadsDir, filename)
                FileOutputStream(file).use { fos ->
                    fos.write(content.toByteArray(Charsets.UTF_8))
                }
                Toast.makeText(context, "Saved to Downloads: ${file.absolutePath}", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error saving file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Downloads file from a remote URL via Android DownloadManager to Downloads directory.
     */
    fun downloadFromUrl(context: Context, url: String, title: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(title)
                .setDescription("Downloading file from +2 Govt Mithila High School")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
            downloadManager?.enqueue(request)
            Toast.makeText(context, "Downloading $title...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Fallback to local save if URL is invalid or local sample
            saveToDownloads(context, if (title.endsWith(".pdf")) title else "$title.pdf", "Official Document: $title\n+2 Govt Mithila High School Balaur")
        }
    }
}
