package com.johnreg.to_doapp.utils

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.johnreg.to_doapp.data.models.Priority

// This function will update our LiveData object only once and after that it will remove its observer
fun <T> LiveData<T>.observeOnceOnly(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            observer.onChanged(value)
            removeObserver(this)
        }
    })
}

// Copied from StackOverflow, hide the keyboard from a Fragment
fun Fragment.hideKeyboard() {
    val imm = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(requireView().windowToken, 0)
}

fun Fragment.showSnackbar(text: String) {
    Snackbar.make(requireView(), text, Snackbar.LENGTH_SHORT).show()
}

fun Fragment.showSnackbarAndDismiss(text: String) {
    val snackbar = Snackbar.make(requireView(), text, Snackbar.LENGTH_LONG)
    snackbar.setAction("Dismiss") { snackbar.dismiss() }
    snackbar.show()
}

fun getPriorityFrom(string: String): Priority = when (string) {
    "Medium Priority" -> Priority.MEDIUM
    "Low Priority" -> Priority.LOW
    else -> Priority.HIGH
}

fun getSelectionFrom(priority: Priority): Int = when (priority) {
    Priority.HIGH -> 0
    Priority.MEDIUM -> 1
    Priority.LOW -> 2
}