package com.mak.youtubex.data.remote.mapper

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.buffer
import okio.source
import java.io.ByteArrayOutputStream

class ProgressRequestBody(
    private val requestBody: RequestBody,
    private val onBytesWritten: (Long) -> Unit
) : RequestBody() {

    override fun contentType(): MediaType? = requestBody.contentType()

    override fun contentLength(): Long = requestBody.contentLength()

    override fun writeTo(sink: BufferedSink) {

        val countingSink = object : ForwardingSink(sink) {

            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                onBytesWritten(byteCount) // send raw bytes
            }
        }

        val bufferedSink = countingSink.buffer()
        requestBody.writeTo(bufferedSink)
        bufferedSink.flush()
    }
}

fun Uri.toCompressedMultipart(
    resolver: ContentResolver,
    partName: String,
    quality: Int = 80,
    onBytesWritten: (Long) -> Unit
): MultipartBody.Part {

    val bitmap = resolver.openInputStream(this).use {
        BitmapFactory.decodeStream(it)
    }

    val output = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)

    val bytes = output.toByteArray()

    val baseRequestBody =
        bytes.toRequestBody("image/jpeg".toMediaType())

    val progressBody =
        ProgressRequestBody(baseRequestBody, onBytesWritten)

    return MultipartBody.Part.createFormData(
        partName,
        "$partName.jpg",
        progressBody
    )
}

fun Uri.toCompressedMultipart(
    resolver: ContentResolver,
    partName: String,
    quality: Int = 80 // Default industry standard quality
): MultipartBody.Part {
    // .use ensures the stream is closed automatically
    val bitmap = resolver.openInputStream(this).use { stream ->
        BitmapFactory.decodeStream(stream)
    }

    val output = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)

    val requestBody = output
        .toByteArray()
        .toRequestBody("image/jpeg".toMediaType())

    return MultipartBody.Part.createFormData(
        partName,
        "$partName.jpg",
        requestBody
    )
}

/***
 * Stream video without loading into memory
 */
fun Uri.toVideoMultipart(
    resolver: ContentResolver,
    partName: String
): MultipartBody.Part {

    val mimeType = resolver.getType(this) ?: "video/mp4"

    val requestBody = object : RequestBody() {
        override fun contentType() = mimeType.toMediaType()

        override fun writeTo(sink: okio.BufferedSink) {
            resolver.openInputStream(this@toVideoMultipart)?.use { input ->
                sink.writeAll(input.source())
            }
        }
    }

    return MultipartBody.Part.createFormData(
        partName,
        "$partName.mp4",
        requestBody
    )
}


/*fun Uri.toCompressedMultipart(
    resolver: ContentResolver,
    partName: String,
    quality: Int
): MultipartBody.Part {

    val bitmap = BitmapFactory.decodeStream(
        resolver.openInputStream(this)
    )

    val output = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)

    val requestBody = output
        .toByteArray()
        .toRequestBody("image/jpeg".toMediaType())

    return MultipartBody.Part.createFormData(
        partName,
        "$partName.jpg",
        requestBody
    )
}*/
