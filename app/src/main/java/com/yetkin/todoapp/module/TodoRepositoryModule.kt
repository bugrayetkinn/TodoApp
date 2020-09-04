package com.yetkin.todoapp.module

import com.yetkin.todoapp.data.repository.TodoRepository
import org.koin.dsl.module


/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */

val todoRepositoryModule = module {
    single { TodoRepository(get()) }
}