package com.yetkin.todoapp.module

import androidx.room.Room
import com.yetkin.todoapp.data.local.TodoDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */

val roomModule = module {

    single {
        Room.databaseBuilder(androidContext(), TodoDatabase::class.java, "tododatabase")
            .allowMainThreadQueries().build()
    }
    single { get<TodoDatabase>().todoDAO() }
}