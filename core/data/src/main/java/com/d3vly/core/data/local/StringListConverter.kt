package com.d3vly.core.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StringListConverter {
    private val gson = Gson()
    private val listType = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun fromJson(value: String): List<String> {
        return runCatching {
            gson.fromJson<List<String>>(value, listType).orEmpty()
        }.getOrDefault(emptyList())
    }

    @TypeConverter
    fun toJson(value: List<String>): String {
        return gson.toJson(value, listType)
    }
}
