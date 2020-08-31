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
class TodoAndDoneAdapter :
    ListAdapter<TodoModel, TodoAndDoneAdapter.TodoAndDoneHolder>(DiffUtilCalback()) {

    class TodoAndDoneHolder(private val todoAndDoneBinding: TodoAndDoneBinding) :
        RecyclerView.ViewHolder(todoAndDoneBinding.root) {

        fun bind(todoModel: TodoModel) {
            todoAndDoneBinding.apply {
                txtViewHour.text = todoModel.time
                txtTitle.text = todoModel.title
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoAndDoneHolder =
        TodoAndDoneHolder(TodoAndDoneBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: TodoAndDoneHolder, position: Int) =
        holder.bind(getItem(position))

    class DiffUtilCalback : DiffUtil.ItemCallback<TodoModel>() {
        override fun areItemsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean =
            oldItem.message == newItem.message

        override fun areContentsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean =
            oldItem == newItem

    }
}