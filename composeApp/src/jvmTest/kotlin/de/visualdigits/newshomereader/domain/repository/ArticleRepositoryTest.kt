package de.visualdigits.newshomereader.domain.repository

import de.visualdigits.newshomereader.di.platformModule
import de.visualdigits.newshomereader.di.sharedModule
import de.visualdigits.newshomereader.domain.model.errorhandling.onError
import de.visualdigits.newshomereader.domain.model.errorhandling.onSuccess
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension

@Disabled("Only for local testing")
class ArticleRepositoryTest : KoinTest {

    private val repository: ArticleRepository by inject()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(sharedModule, platformModule)
    }

    @Test
    fun testReadArticle() {
        runBlocking {
            repository.readFullArticle(4711, "https://www.tagesschau.de/newsticker/liveblog-iran-donnerstag-106.html")
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
