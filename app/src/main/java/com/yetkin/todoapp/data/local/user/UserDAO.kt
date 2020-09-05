package com.yetkin.todoapp.data.local.user

import androidx.lifecycle.LiveData
import androidx.room.*


/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */
@Dao
interface UserDAO {

    @Insert
    suspend fun insert(userModel: UserModel)

    @Delete
    suspend fun delete(userModel: UserModel)

    @Query("DELETE FROM user WHERE userPassword=:password")
    suspend fun deletePassword(password: String)

    @Update
    suspend fun update(userModel: UserModel)

    @Query("SELECT*FROM user WHERE userPassword=:password")
    fun passwordControl(password: String): Boolean

    @Query("SELECT*FROM user")
    fun getAllUser(): LiveData<MutableList<UserModel>>

}