package de.visualdigits.newshomereader.domain.repository

import de.visualdigits.newshomereader.di.sharedModule
import de.visualdigits.newshomereader.di.testModule
import de.visualdigits.newshomereader.domain.model.errorhandling.onError
import de.visualdigits.newshomereader.domain.model.errorhandling.onSuccess
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension

//@Disabled("Only for local testing")
class ArticleRepositoryTest : KoinTest {

    private val repository: ArticleRepository by inject()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(sharedModule, testModule)
    }

    @Test
    fun testReadArticle() {
        runBlocking {
            repository.readFullArticle(4711, "https://t3n.de/news/fleisch-aus-bier-so-soll-ein-pilz-brauereiabfaelle-in-grillgut-verwandeln-1738102/?utm_source=rss&utm_medium=newsFeed&utm_campaign=newsFeed")
                .onSuccess { article ->
                    println(article)
                }.onError { error, throwable ->
                    if (throwable != null) {
                        throw throwable
                    }
                }
        }
    }
}
