package com.onesilicondiode.dropme

import android.app.Application
import com.theapache64.removebg.RemoveBg

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        /**
         * GET YOUR API KEY FROM HERE -> https://www.remove.bg/profile#api-key
         */
        RemoveBg.init("nwMRFvY7nKuNc1m9PnwUKcU3")
    }
}