package com.yetkin.todoapp.module

import com.yetkin.todoapp.viewmodel.TodoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */

val todoViewModelModule = module {
    viewModel { TodoViewModel(get()) }
}