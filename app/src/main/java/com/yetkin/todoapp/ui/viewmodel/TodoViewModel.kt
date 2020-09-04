package com.yetkin.todoapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yetkin.todoapp.data.local.TodoModel
import com.yetkin.todoapp.data.repository.TodoRepository
import kotlinx.coroutines.launch

/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */
class TodoViewModel(private val todoRepository: TodoRepository) : ViewModel() {

    fun insert(todoModel: TodoModel) = viewModelScope.launch {
        todoRepository.insert(todoModel)
    }

    fun delete(todoModel: TodoModel) = viewModelScope.launch {
        todoRepository.delete(todoModel)
    }

    fun update(todoModel: TodoModel) = viewModelScope.launch {
        todoRepository.update(todoModel)
    }

    fun getTodo(date: String): LiveData<List<TodoModel>> = todoRepository.getTodo(date)
    fun getDone(date: String): LiveData<List<TodoModel>> = todoRepository.getDone(date)
}