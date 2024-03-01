package com.johnreg.to_doapp.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "todo_table")
@Parcelize
data class ToDoData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val priority: Priority = Priority.HIGH,
    val description: String? = null
) : Parcelable