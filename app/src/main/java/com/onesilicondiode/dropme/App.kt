package com.onesilicondiode.dropme

import android.app.Application
import android.os.Bundle
import com.theapache64.removebg.RemoveBg

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        /**
         * GET YOUR API KEY FROM HERE -> https://www.remove.bg/profile#api-key
         */
        RemoveBg.init("muwoXsbd5N35jmxitxvPymQU")
    }
}