package com.yetkin

import android.app.Application
import org.koin.core.context.startKoin


/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules()
        }
    }
}