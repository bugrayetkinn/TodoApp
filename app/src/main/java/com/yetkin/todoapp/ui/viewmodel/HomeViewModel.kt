package com.yetkin.todoapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yetkin.todoapp.data.local.todo.TodoModel
import com.yetkin.todoapp.data.local.user.UserModel
import com.yetkin.todoapp.data.repository.TodoRepository
import com.yetkin.todoapp.data.repository.UserRepository
import kotlinx.coroutines.launch

/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */
class HomeViewModel(
    private val todoRepository: TodoRepository,
    private val userRepository: UserRepository
) : ViewModel() {

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
    fun getPrivateTodo(): LiveData<List<TodoModel>> = todoRepository.getPrivateTodo()
    fun getPrivateDone(): LiveData<List<TodoModel>> = todoRepository.getPrivateDone()


    fun insertUser(user: UserModel) = viewModelScope.launch { userRepository.insertUser(user) }
    fun deleteUser(user: UserModel) = viewModelScope.launch { userRepository.deleteUser(user) }
    fun deletePassword(password: String) =
        viewModelScope.launch { userRepository.deletePassword(password) }

    fun updateUser(user: UserModel) = viewModelScope.launch { userRepository.updateUser(user) }
    fun passwordControl(password: String): Boolean = userRepository.passwordControl(password)
    fun getAllUser(): LiveData<MutableList<UserModel>> = userRepository.getAllUser()
}