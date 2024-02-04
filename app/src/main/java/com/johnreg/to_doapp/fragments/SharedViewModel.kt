package com.johnreg.to_doapp.fragments

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import com.johnreg.to_doapp.data.models.Priority

class SharedViewModel(application: Application): AndroidViewModel(application) {

    fun verifyDataFromUser(title: String, description: String): Boolean {
        return when {
            TextUtils.isEmpty(title) || TextUtils.isEmpty(description) -> false
            else -> !(title.isEmpty() || description.isEmpty())
        }
    }

    fun parsePriority(priority: String): Priority {
        return when (priority) {
            "Medium Priority" -> Priority.MEDIUM
            "Low Priority" -> Priority.LOW
            else -> Priority.HIGH
        }
    }

}