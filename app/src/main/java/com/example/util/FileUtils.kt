package com.example.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    /**
     * Copies a content URI (e.g. from PhotoPicker or file chooser) to app internal storage
     * so that it never loses permission and persists permanently across app restarts.
     * Returns a valid "file://" URI string for reliable image loaders like Coil.
     */
    fun copyUriToInternalStorage(context: Context, uri: Uri, prefix: String): String {
        return try {
            val mimeType = context.contentResolver.getType(uri)
            val extension = when {
                mimeType?.contains("png", ignoreCase = true) == true -> "png"
                mimeType?.contains("webp", ignoreCase = true) == true -> "webp"
                else -> "jpg"
            }
            val fileName = "${prefix}_${System.currentTimeMillis()}.$extension"
            val destFile = File(context.filesDir, fileName)
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            "file://${destFile.absolutePath}"
        } catch (e: Exception) {
            e.printStackTrace()
            uri.toString()
        }
    }
}
