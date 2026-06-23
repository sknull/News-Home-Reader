package de.visualdigits.newshomereader.data.util

import de.visualdigits.newshomereader.domain.util.fileName
import de.visualdigits.newshomereader.domain.util.fileNameWithoutExtension
import io.ktor.http.Url
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HtmlUtilTest {

    @Test
    fun testExtractFileName() {
        val url = Url("http://www.foobar.com/the/sub/page/theFile.jpg?bla=fasel&boofar=guz&")
        assertEquals("theFile.jpg", url.fileName())
        assertEquals("theFile", url.fileNameWithoutExtension())
    }
}
