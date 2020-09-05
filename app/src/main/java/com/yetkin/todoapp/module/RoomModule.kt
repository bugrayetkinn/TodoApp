package com.yetkin.todoapp.module

import androidx.room.Room
import com.yetkin.todoapp.data.local.RoomDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */

val roomModule = module {

    single {
        Room.databaseBuilder(androidContext(), RoomDatabase::class.java, "tododatabase")
            .allowMainThreadQueries().build()
    }
    single { get<RoomDatabase>().todoDAO() }
    single { get<RoomDatabase>().userDAO() }
}