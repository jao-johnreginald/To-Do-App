package com.johnreg.to_doapp.data.room

import androidx.room.TypeConverter
import com.johnreg.to_doapp.data.models.Priority

class Converter {

    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(priority: String): Priority = Priority.valueOf(priority)

}