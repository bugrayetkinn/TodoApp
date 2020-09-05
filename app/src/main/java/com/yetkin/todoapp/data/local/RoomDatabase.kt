package com.yetkin.todoapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yetkin.todoapp.data.local.todo.TodoDAO
import com.yetkin.todoapp.data.local.todo.TodoModel
import com.yetkin.todoapp.data.local.user.UserDAO
import com.yetkin.todoapp.data.local.user.UserModel

/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */
@Database(entities = [TodoModel::class, UserModel::class], version = 1)
abstract class RoomDatabase : RoomDatabase() {
    abstract fun todoDAO(): TodoDAO
    abstract fun userDAO(): UserDAO
}