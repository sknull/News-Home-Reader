package de.visualdigits.newshomereader.data.database

import de.visualdigits.newshomereader.NewsHomeReaderDatabaseQueries
import de.visualdigits.newshomereader.di.platformModule
import de.visualdigits.newshomereader.di.sharedModule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension

class DatabaseTest : KoinTest {

    private val dao: NewsHomeReaderDatabaseQueries by inject()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(sharedModule, platformModule)
    }

    @Test
    fun testUpdate() {
        val article = dao.getFullArticleByItemId(435)
        println(article)
    }
}
