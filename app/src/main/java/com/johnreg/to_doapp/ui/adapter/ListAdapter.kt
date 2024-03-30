package com.johnreg.to_doapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.johnreg.to_doapp.R
import com.johnreg.to_doapp.data.models.Priority
import com.johnreg.to_doapp.data.models.ToDoData
import com.johnreg.to_doapp.databinding.RowLayoutBinding
import com.johnreg.to_doapp.ui.fragments.ListFragmentDirections

class ListAdapter : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private var dataList: List<ToDoData> = emptyList()

    override fun getItemCount(): Int = dataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(dataList[position])

    class ViewHolder(private val binding: RowLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currentItem: ToDoData) {
            binding.tvTitle.text = currentItem.title
            binding.tvDescription.text = currentItem.description

            val color = when (currentItem.priority) {
                Priority.HIGH -> ContextCompat.getColor(itemView.context, R.color.red)
                Priority.MEDIUM -> ContextCompat.getColor(itemView.context, R.color.yellow)
                Priority.LOW -> ContextCompat.getColor(itemView.context, R.color.green)
            }
            binding.priorityIndicator.setCardBackgroundColor(color)

            if (currentItem.description.isNullOrEmpty()) binding.tvDescription.visibility = View.GONE
            else binding.tvDescription.visibility = View.VISIBLE

            binding.rowBackground.setOnClickListener {
                // This class is automatically generated by the Safe Args plugin
                // Args suffix - receive the argument | Directions suffix - pass the argument
                val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
                itemView.findNavController().navigate(action)
            }
        }
    }

    fun setData(newDataList: List<ToDoData>) {
        val toDoDiffUtil = ToDoDiffUtil(dataList, newDataList)
        val toDoDiffResult = DiffUtil.calculateDiff(toDoDiffUtil)
        this.dataList = newDataList
        toDoDiffResult.dispatchUpdatesTo(this)
    }

    fun getCurrentItem(position: Int): ToDoData = dataList[position]

}