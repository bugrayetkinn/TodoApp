package com.yetkin.todoapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.*


/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */
@Dao
interface TodoDAO {

    @Insert
    suspend fun insert(todoModel: TodoModel)

    @Delete
    suspend fun delete(todoModel: TodoModel)

    @Update
    suspend fun update(todoModel: TodoModel)

    @Query("SELECT * FROM todotable WHERE todo_date=:date AND todo_checkDone=0 ORDER BY todo_priority DESC")
    fun getTodo(date: String): LiveData<List<TodoModel>>

    @Query("SELECT * FROM todotable WHERE todo_date=:date AND todo_checkDone=1 ORDER BY todo_priority DESC")
    fun getDone(date: String): LiveData<List<TodoModel>>

}