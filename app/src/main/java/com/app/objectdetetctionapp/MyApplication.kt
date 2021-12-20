package com.app.objectdetetctionapp

class MyApplication : android.app.Application() {

    companion object {
        @JvmField
        var appInstance: MyApplication? = null

        @JvmStatic
        fun getAppInstance(): MyApplication {
            return appInstance as MyApplication
        }
    }
    override fun onCreate() {
        super.onCreate()
        appInstance = this


    }
}