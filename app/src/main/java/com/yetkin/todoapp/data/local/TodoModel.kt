package com.yetkin.todoapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */

@Entity(tableName = "todotable")
data class TodoModel(
    @PrimaryKey(autoGenerate = true)
    val todoId: Int = 0,
    @ColumnInfo(name = "todo_title")
    val title: String,
    @ColumnInfo(name = "todo_message")
    val message: String,
    @ColumnInfo(name = "todo_date")
    val date: String,
    @ColumnInfo(name = "todo_time")
    val time: String,
    @ColumnInfo(name = "todo_priority")
    val priority: Int,
    @ColumnInfo(name = "todo_checkDone")
    val checkDone: Int = 0
) : Serializable