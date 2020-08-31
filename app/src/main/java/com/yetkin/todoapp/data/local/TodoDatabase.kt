package com.yetkin.todoapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase


/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */
@Database(entities = [TodoModel::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDAO(): TodoDAO
}