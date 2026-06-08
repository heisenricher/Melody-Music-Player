package com.melody.core.util

import java.io.File

object FileUtils {
    fun getFileName(filePath: String): String {
        return File(filePath).name
    }

    fun getParentFolderPath(filePath: String): String {
        return File(filePath).parent ?: ""
    }

    fun getParentFolderName(filePath: String): String {
        return File(filePath).parentFile?.name ?: ""
    }

    fun readText(filePath: String): String? {
        return try {
            val file = File(filePath)
            if (file.exists() && file.isFile) {
                file.readText()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
