package com.arsenosov.notebook

import android.app.Application
import com.arsenosov.notebook.di.AppComponent

class App: Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
    }
}