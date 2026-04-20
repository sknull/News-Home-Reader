package de.visualdigits.newshomereader

import android.app.Application
import androidx.work.Configuration
import co.touchlab.kermit.Logger
import de.visualdigits.newshomereader.di.platformModule
import de.visualdigits.newshomereader.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.GlobalContext.startKoin

class NewsHomeReaderApplication: Application(), Configuration.Provider {

    override val workManagerConfiguration: Configuration
        get() {
            Logger.i("#### Request WorkManager configuration")
            return Configuration.Builder()
                .setWorkerFactory(KoinWorkerFactory())
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .build()
        }

    override fun onCreate() {
        Logger.i("#### Starting koin...")
        startKoin {
            androidContext(this@NewsHomeReaderApplication)
            workManagerFactory()
            modules(sharedModule, platformModule)
        }

        // IMPORTANT do super create AFTER koin initializing to avoid problems with work managers
        Logger.i("#### Initializing application...")
        super.onCreate()

        Logger.i("#### Application initialized")
    }
}
