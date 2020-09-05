package com.yetkin.todoapp.module

import com.yetkin.todoapp.data.repository.UserRepository
import org.koin.dsl.module


/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */

val userRepositoryModule = module {

    single { UserRepository(get()) }
}