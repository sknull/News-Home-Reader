package de.visualdigits.newshomereader.di

import de.visualdigits.newshomereader.FullArticleEntity
import de.visualdigits.newshomereader.NewsFeedEntity
import de.visualdigits.newshomereader.NewsFeedGroupEntity
import de.visualdigits.newshomereader.NewsHomeReaderDatabaseQueries
import de.visualdigits.newshomereader.SettingsDatabase
import de.visualdigits.newshomereader.data.database.DriverFactory
import de.visualdigits.newshomereader.data.database.mapper.applicationJsonAdapter
import de.visualdigits.newshomereader.data.database.mapper.mediaItemAdapter
import de.visualdigits.newshomereader.data.database.mapper.newsFeedsAdapter
import de.visualdigits.newshomereader.data.database.mapper.stringListAdapter
import de.visualdigits.newshomereader.data.database.mapper.subGroupsAdapter
import de.visualdigits.newshomereader.data.http.HttpClientFactory
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
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {

    singleOf(::NewsHomeReaderViewModel)

    single {
        val driver = get<DriverFactory>().createDriver()
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
                newsFeedsAdapter = newsFeedsAdapter,
                subGroupsAdapter = subGroupsAdapter
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
