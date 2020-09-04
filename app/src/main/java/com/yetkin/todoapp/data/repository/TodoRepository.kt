package com.yetkin.todoapp.data.repository

import com.yetkin.todoapp.data.local.TodoDAO
import com.yetkin.todoapp.data.local.TodoModel


/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */

class TodoRepository(private val todoDAO: TodoDAO) {

    suspend fun insert(todoModel: TodoModel) = todoDAO.insert(todoModel)
    suspend fun delete(todoModel: TodoModel) = todoDAO.delete(todoModel)
    suspend fun update(todoModel: TodoModel) = todoDAO.update(todoModel)
    fun getTodo(date: String) = todoDAO.getTodo(date)
    fun getDone(date: String) = todoDAO.getDone(date)
}