package com.mak.youtubex.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }

}

/*@TypeConverter
    fun fromImages(images: List<String>): String {
        return images.joinToString(separator = ",")
    }

    @TypeConverter
    fun toImages(data: String): List<String> {
        return if (data.isEmpty()) emptyList() else data.split(",")
    }*/