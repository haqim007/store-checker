package dev.haqim.pitjarusapp.util

import android.app.Application
import dev.haqim.pitjarusapp.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

fun createFile(application: Application, mimeType: String = "jpg"): File {
    val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
        File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
    }

    val outputDirectory = if (
        mediaDir != null && mediaDir.exists()
    ) mediaDir else application.filesDir

    val timeStamp: String = SimpleDateFormat(
        "dd-MMM-yyyy",
        Locale.US
    ).format(System.currentTimeMillis())
    
    return File(outputDirectory, "$timeStamp.$mimeType")
}