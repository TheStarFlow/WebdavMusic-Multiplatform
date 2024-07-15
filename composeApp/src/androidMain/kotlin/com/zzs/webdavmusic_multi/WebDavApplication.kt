package com.zzs.webdavmusic_multi

import android.app.Application
import org.koin.android.ext.koin.androidContext
import zzs.webdav.music.di.initKoin

class WebDavApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@WebDavApplication)
        }
    }
}