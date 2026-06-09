package de.visualdigits.newshomereader.di

import app.cash.sqldelight.ColumnAdapter
import de.visualdigits.common.domain.model.CryptoBox
import de.visualdigits.common.domain.model.EncryptedString
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
import de.visualdigits.newshomereader.data.database.upsertNewsFeedGroupByName
import de.visualdigits.newshomereader.data.http.HttpClientFactory
import de.visualdigits.newshomereader.data.repository.DefaultArticleRepository
import de.visualdigits.newshomereader.data.repository.DefaultCatalogRepository
import de.visualdigits.newshomereader.data.repository.DefaultFeedRepository
import de.visualdigits.newshomereader.data.repository.DefaultNewsFeedConfigurationRepository
import de.visualdigits.newshomereader.data.repository.DefaultSettingsRepository
import de.visualdigits.newshomereader.data.repository.NewsFeedWorker
import de.visualdigits.newshomereader.data.webdav.DefaultWebDavSyncService
import de.visualdigits.newshomereader.domain.repository.ArticleRepository
import de.visualdigits.newshomereader.domain.repository.CatalogRepository
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import de.visualdigits.newshomereader.domain.repository.NewsFeedConfigurationRepository
import de.visualdigits.newshomereader.domain.repository.SettingsRepository
import de.visualdigits.newshomereader.domain.webdav.WebDavSyncService
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

expect val homeDirectory: String

val sharedModule = module {

    single(named("homeDirectory")) { homeDirectory }

    singleOf(::NewsHomeReaderViewModel)

    single {
        val driver = get<DriverFactory>().createDriver(get<String>(named("homeDirectory")))

        val cryptoBox = get<CryptoBox>()

        val passwordAdapter = object : ColumnAdapter<EncryptedString, String> {
            override fun decode(databaseValue: String): EncryptedString = cryptoBox.decrypt(databaseValue)
            override fun encode(value: EncryptedString): String = cryptoBox.encrypt(value)
        }

        val database = SettingsDatabase(driver,
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
        database.newsHomeReaderDatabaseQueries.upsertNewsFeedGroupByName(NewsFeedGroupEntity(
            id = 0L,
            parentId = null,
            parentGroupName = null,
            name = "News Feeds",
            type = "root",
            newsFeeds = listOf()
        ))
        database.newsHomeReaderDatabaseQueries.upsertNewsFeedGroupByName(NewsFeedGroupEntity(
            id = 0L,
            parentId = null,
            parentGroupName = null,
            name = "Keywords",
            type = "root",
            newsFeeds = listOf()
        ))
        database
    }

    single<NewsHomeReaderDatabaseQueries> {
        get<SettingsDatabase>().newsHomeReaderDatabaseQueries
    }

    single { HttpClientFactory.create(
        engine = get(),
        settingsRepositoryProvider = { get<SettingsRepository>() }
    )}
    singleOf(::DefaultFeedRepository).bind<FeedRepository>()
    singleOf(::DefaultNewsFeedConfigurationRepository).bind<NewsFeedConfigurationRepository>()
    singleOf(::DefaultArticleRepository).bind<ArticleRepository>()
    singleOf(::DefaultSettingsRepository).bind<SettingsRepository>()
    singleOf(::DefaultCatalogRepository).bind<CatalogRepository>()
    singleOf(::DefaultWebDavSyncService).bind<WebDavSyncService>()

    single { NewsFeedWorker(
        feedRepository = get(),
        newsFeedConfigurationRepository = get(),
        settingsRepository = get(),
        connectivityManager = get()
    ) }
}
