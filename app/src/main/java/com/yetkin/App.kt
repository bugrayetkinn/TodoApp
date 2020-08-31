package com.yetkin

import android.app.Application
import com.yetkin.todoapp.module.roomModule
import com.yetkin.todoapp.module.todoRepositoryModule
import com.yetkin.todoapp.module.todoViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                roomModule,
                todoRepositoryModule,
                todoViewModelModule
            )
        }
    }
}