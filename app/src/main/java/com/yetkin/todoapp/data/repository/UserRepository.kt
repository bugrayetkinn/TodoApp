package com.yetkin.todoapp.data.repository

import androidx.lifecycle.LiveData
import com.yetkin.todoapp.data.local.user.UserDAO
import com.yetkin.todoapp.data.local.user.UserModel


/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */
class UserRepository(private val userDAO: UserDAO) {

    suspend fun insertUser(user: UserModel) = userDAO.insert(user)
    suspend fun deleteUser(user: UserModel) = userDAO.delete(user)
    suspend fun updateUser(user: UserModel) = userDAO.update(user)
    suspend fun deletePassword(password: String) = userDAO.deletePassword(password)
    fun getAllUser(): LiveData<MutableList<UserModel>> = userDAO.getAllUser()
    fun passwordControl(password: String): Boolean = userDAO.passwordControl(password)
}