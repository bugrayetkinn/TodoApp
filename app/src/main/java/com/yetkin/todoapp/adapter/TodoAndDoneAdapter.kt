package com.yetkin.todoapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yetkin.todoapp.data.local.TodoModel
import com.yetkin.todoapp.databinding.TodoAndDoneBinding

/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */
class TodoAndDoneAdapter(private val setOnCheckBoxClickListener: (TodoModel) -> Unit) :
    ListAdapter<TodoModel, TodoAndDoneAdapter.TodoAndDoneHolder>(DiffUtilCallback()) {

    class TodoAndDoneHolder(private val todoAndDoneBinding: TodoAndDoneBinding) :
        RecyclerView.ViewHolder(todoAndDoneBinding.root) {

        fun bind(todoModel: TodoModel, setOnCheckBoxClickListener: (TodoModel) -> Unit) {
            todoAndDoneBinding.apply {
                txtViewHour.text = todoModel.time
                txtTitle.text = todoModel.title

                when (todoModel.checkDone) {
                    0 -> {
                        checkBox.isChecked = false
                    }
                    1 -> {
                        checkBox.isChecked = true
                    }
                }
                checkBox.setOnClickListener {
                    setOnCheckBoxClickListener(todoModel)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoAndDoneHolder =
        TodoAndDoneHolder(TodoAndDoneBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: TodoAndDoneHolder, position: Int) =
        holder.bind(getItem(position), setOnCheckBoxClickListener)

    class DiffUtilCallback : DiffUtil.ItemCallback<TodoModel>() {
        override fun areItemsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean =
            oldItem.message == newItem.message

        override fun areContentsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean =
            oldItem == newItem
    }

    fun removeItem(position: Int): TodoModel {
        val item: TodoModel = currentList[position]
        notifyItemRemoved(position)
        notifyDataSetChanged()
        return item
    }
}