package com.yetkin.todoapp.data.local.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */
@Entity(tableName = "user")
data class UserModel(

    @PrimaryKey(autoGenerate = true)
    val _userId: Int = 0,
    @ColumnInfo(name = "userPassword")
    val userPassword: String
)