package com.johnreg.to_doapp.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import com.johnreg.to_doapp.R

class SpinnerAdapter(
    context: Context, items: List<String>
) : ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val textOfSpinner = super.getView(position, convertView, parent) as TextView
        when (position) {
            0 -> textOfSpinner.setTextColor(getColor(context, R.color.red))
            1 -> textOfSpinner.setTextColor(getColor(context, R.color.yellow))
            2 -> textOfSpinner.setTextColor(getColor(context, R.color.green))
        }
        return textOfSpinner
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val textOfSpinner = super.getDropDownView(position, convertView, parent) as TextView
        when (position) {
            0 -> textOfSpinner.setTextColor(getColor(context, R.color.red))
            1 -> textOfSpinner.setTextColor(getColor(context, R.color.yellow))
            2 -> textOfSpinner.setTextColor(getColor(context, R.color.green))
        }
        return textOfSpinner
    }

}