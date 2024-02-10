package com.johnreg.to_doapp.fragments.list.adapter

import androidx.recyclerview.widget.DiffUtil
import com.johnreg.to_doapp.data.models.ToDoData

class ToDoDiffUtil(
    private val oldList: List<ToDoData>,
    private val newList: List<ToDoData>
) : DiffUtil.Callback() {

    // Returns the size of the old list
    override fun getOldListSize(): Int = oldList.size

    // Returns the size of the new list
    override fun getNewListSize(): Int = newList.size

    // Called by the DiffUtil to decide whether two objects represent the same item
    // If your items have unique ids, this method should check their id equality
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    // Checks whether two items have the same data, you can change its behavior depending on your UI
    // This method is called by DiffUtil only if areItemsTheSame() returns true
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
                && oldList[oldItemPosition].title == newList[newItemPosition].title
                && oldList[oldItemPosition].description == newList[newItemPosition].description
                && oldList[oldItemPosition].priority == newList[newItemPosition].priority
    }

}