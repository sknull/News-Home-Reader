package de.visualdigits.newshomereader.di

import app.cash.sqldelight.ColumnAdapter
import de.visualdigits.newshomereader.FullArticleEntity
import de.visualdigits.newshomereader.NewsFeedConfigurationBlob
import de.visualdigits.newshomereader.NewsFeedEntity
import de.visualdigits.newshomereader.NewsHomeReaderDatabaseQueries
import de.visualdigits.newshomereader.SettingsDatabase
import de.visualdigits.newshomereader.data.database.DriverFactory
import de.visualdigits.newshomereader.data.database.mapper.applicationJsonAdapter
import de.visualdigits.newshomereader.data.database.mapper.mediaItemAdapter
import de.visualdigits.newshomereader.data.database.mapper.stringListAdapter
import de.visualdigits.newshomereader.data.http.HttpClientFactory
import de.visualdigits.newshomereader.data.model.newsfeeds.NewsFeedConfigurationEntity
import de.visualdigits.newshomereader.data.repository.DefaultArticleRepository
import de.visualdigits.newshomereader.data.repository.DefaultFeedRepository
import de.visualdigits.newshomereader.data.repository.DefaultNewsFeedConfigurationRepository
import de.visualdigits.newshomereader.data.repository.DefaultSettingsRepository
import de.visualdigits.newshomereader.data.repository.NewsFeedWorker
import de.visualdigits.newshomereader.domain.repository.ArticleRepository
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import de.visualdigits.newshomereader.domain.repository.NewsFeedConfigurationRepository
import de.visualdigits.newshomereader.domain.repository.SettingsRepository
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val newsFeedsAdapter = object : ColumnAdapter<NewsFeedConfigurationEntity, String> {
    override fun decode(databaseValue: String): NewsFeedConfigurationEntity =
        Json.decodeFromString(databaseValue)

    override fun encode(value: NewsFeedConfigurationEntity): String =
        Json.encodeToString(value)
}


val sharedModule = module {

    singleOf(::NewsHomeReaderViewModel)

    single {
        val driver = get<DriverFactory>().createDriver()
        SettingsDatabase(driver,
            NewsFeedConfigurationBlobAdapter = NewsFeedConfigurationBlob.Adapter(newsFeedsAdapter),
            NewsFeedEntityAdapter = NewsFeedEntity.Adapter(
                keywordsAdapter = stringListAdapter
            ),
            FullArticleEntityAdapter = FullArticleEntity.Adapter(
                applicationJsonAdapter = applicationJsonAdapter,
                imageItemsAdapter = mediaItemAdapter,
                videoItemsAdapter = mediaItemAdapter,
                audioItemsAdapter = mediaItemAdapter,
            )
        )
    }
    single<NewsHomeReaderDatabaseQueries> {
        get<SettingsDatabase>().newsHomeReaderDatabaseQueries
    }

    single { HttpClientFactory.create(get()) }
    singleOf(::DefaultFeedRepository).bind<FeedRepository>()
    singleOf(::DefaultNewsFeedConfigurationRepository).bind<NewsFeedConfigurationRepository>()
    singleOf(::DefaultArticleRepository).bind<ArticleRepository>()
    singleOf(::DefaultSettingsRepository).bind<SettingsRepository>()

    single { NewsFeedWorker(
        feedRepository = get(),
        newsFeedConfigurationRepository = get(),
        settingsRepository = get(),
    ) }
}
