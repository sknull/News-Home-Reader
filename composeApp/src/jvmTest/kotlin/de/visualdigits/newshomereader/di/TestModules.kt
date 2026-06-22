package de.visualdigits.newshomereader.di

import app.cash.sqldelight.ColumnAdapter
import de.visualdigits.common.domain.util.CryptoBox
import de.visualdigits.common.domain.util.EncryptedString
import de.visualdigits.newshomereader.FullArticleEntity
import de.visualdigits.newshomereader.NewsFeedEntity
import de.visualdigits.newshomereader.NewsFeedGroupEntity
import de.visualdigits.newshomereader.NewsHomeReaderDatabaseQueries
import de.visualdigits.newshomereader.SettingsDatabase
import de.visualdigits.newshomereader.SettingsEntity
import de.visualdigits.newshomereader.data.database.DriverFactory
import de.visualdigits.newshomereader.data.database.applicationJsonAdapter
import de.visualdigits.newshomereader.data.database.mediaItemAdapter
import de.visualdigits.newshomereader.data.database.newsFeedsAdapter
import de.visualdigits.newshomereader.data.database.stringListAdapter
import de.visualdigits.newshomereader.data.http.HttpClientFactory
import de.visualdigits.newshomereader.data.repository.DefaultSettingsRepository
import de.visualdigits.newshomereader.domain.repository.ArticleRepository
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import de.visualdigits.newshomereader.domain.repository.SettingsRepository
import de.visualdigits.newshomereader.repository.MockArticleRepository
import de.visualdigits.newshomereader.repository.MockFeedRepository
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import java.io.File

val testHomeDirectory = File("E:\\temp\\.newshomereader")

val testModule = module {

    single(named("homeDirectory")) { testHomeDirectory.canonicalPath }


    single {
        val driver = get<DriverFactory>().createDriver(get<String>(named("homeDirectory")))

        val cryptoBox = get<CryptoBox>()

        val passwordAdapter = object : ColumnAdapter<EncryptedString, String> {
            override fun decode(databaseValue: String): EncryptedString = cryptoBox.decrypt(databaseValue)
            override fun encode(value: EncryptedString): String = cryptoBox.encrypt(value)
        }

        SettingsDatabase(driver,
            FullArticleEntityAdapter = FullArticleEntity.Adapter(
                applicationJsonAdapter = applicationJsonAdapter,
                imageItemsAdapter = mediaItemAdapter,
                videoItemsAdapter = mediaItemAdapter,
                audioItemsAdapter = mediaItemAdapter,
            ),
            NewsFeedEntityAdapter = NewsFeedEntity.Adapter(
                keywordsAdapter = stringListAdapter
            ),
            NewsFeedGroupEntityAdapter = NewsFeedGroupEntity.Adapter(
                newsFeedsAdapter = newsFeedsAdapter
            ),
            SettingsEntityAdapter = SettingsEntity.Adapter(
                webDavPasswordAdapter = passwordAdapter
            )
        )
    }
    single<NewsHomeReaderDatabaseQueries> {
        get<SettingsDatabase>().newsHomeReaderDatabaseQueries
    }

    single { HttpClientFactory.create(
        engine = get(),
        settingsRepositoryProvider = { get<SettingsRepository>() }
    )}

    singleOf(::DefaultSettingsRepository).bind<SettingsRepository>()
    singleOf(::MockFeedRepository).bind<FeedRepository>()
    singleOf(::MockArticleRepository).bind<ArticleRepository>()
}
