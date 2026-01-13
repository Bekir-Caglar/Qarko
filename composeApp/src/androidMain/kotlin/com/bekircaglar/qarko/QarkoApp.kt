package com.bekircaglar.qarko

import android.app.Application

class QarkoApp : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
