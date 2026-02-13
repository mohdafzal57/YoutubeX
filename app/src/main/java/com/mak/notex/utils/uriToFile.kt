package com.mak.notex.utils

import android.content.Context
import android.net.Uri
import java.io.File

fun uriToFile(context: Context, imageName: String,  uri: Uri): File {
    val input = context.contentResolver.openInputStream(uri)!!
    val file = File(context.cacheDir, imageName)
    file.outputStream().use { input.copyTo(it) }
    return file
}