package de.visualdigits.newshomereader

import android.app.Application
import androidx.work.Configuration
import de.visualdigits.newshomereader.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.factory.KoinWorkerFactory

class NewsHomeReaderApplication: Application(), Configuration.Provider {

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(KoinWorkerFactory())
            .build()

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@NewsHomeReaderApplication)

        }
    }
}
