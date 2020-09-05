package com.yetkin.todoapp.module

import com.yetkin.todoapp.ui.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */

val homeViewModelModule = module {
    viewModel { HomeViewModel(get(), get()) }
}