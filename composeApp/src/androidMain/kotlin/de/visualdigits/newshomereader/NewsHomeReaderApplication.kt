package de.visualdigits.newshomereader

import android.app.Application
import androidx.work.Configuration
import de.visualdigits.newshomereader.di.platformModule
import de.visualdigits.newshomereader.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.GlobalContext.startKoin

class NewsHomeReaderApplication: Application(), Configuration.Provider {

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(KoinWorkerFactory())
            .build()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NewsHomeReaderApplication)
            workManagerFactory()
            modules(sharedModule, platformModule)
        }
    }
}
