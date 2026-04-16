package de.visualdigits.newshomereader.di

import de.visualdigits.newshomereader.domain.repository.ArticleRepository
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import de.visualdigits.newshomereader.repository.MockArticleRepository
import de.visualdigits.newshomereader.repository.MockFeedRepository
import org.koin.dsl.module

val testModule = module {
    single<FeedRepository> { MockFeedRepository() }
    single<ArticleRepository> { MockArticleRepository() }
}
