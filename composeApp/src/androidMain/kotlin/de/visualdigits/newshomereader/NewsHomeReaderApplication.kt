package de.visualdigits.newshomereader

import android.app.Application
import de.visualdigits.newshomereader.di.initKoin
import org.koin.android.ext.koin.androidContext

class NewsHomeReaderApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@NewsHomeReaderApplication)
        }
    }
}
