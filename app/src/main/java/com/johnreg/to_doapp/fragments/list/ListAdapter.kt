package com.johnreg.to_doapp.fragments.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.johnreg.to_doapp.R
import com.johnreg.to_doapp.data.models.Priority
import com.johnreg.to_doapp.data.models.ToDoData
import com.johnreg.to_doapp.databinding.RowLayoutBinding

class ListAdapter : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    var dataList: List<ToDoData> = emptyList()

    override fun getItemCount(): Int = dataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position], holder.itemView.context)
    }

    class ViewHolder(private val binding: RowLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(toDoData: ToDoData, context: Context) {
            binding.tvTitle.text = toDoData.title
            binding.tvDescription.text = toDoData.description

            when (toDoData.priority) {
                Priority.HIGH -> binding.priorityIndicator.setCardBackgroundColor(ContextCompat.getColor(context, R.color.red))
                Priority.MEDIUM -> binding.priorityIndicator.setCardBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
                Priority.LOW -> binding.priorityIndicator.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green))
            }
        }

    }

    fun setData(list: List<ToDoData>) {
        this.dataList = list
        notifyDataSetChanged()
    }

}