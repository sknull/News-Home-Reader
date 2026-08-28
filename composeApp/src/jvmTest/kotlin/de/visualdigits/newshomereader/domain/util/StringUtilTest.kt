package de.visualdigits.newshomereader.domain.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StringUtilTest {

    @Test
    fun testDuration() {
        assertEquals("00:03:45", "PT3M45S".parseDuration())
        assertEquals("00:03:45", "3M45S".parseDuration())
        assertEquals("05:46:17", "345M77S".parseDuration())

        assertEquals("00:03:00", "03M".parseDuration())
        assertEquals("04:05:00", "245M".parseDuration())

        assertEquals("00:00:45", "45S".parseDuration())
        assertEquals("00:01:17", "77S".parseDuration())
        assertEquals("01:01:50", "3710S".parseDuration())

        assertEquals("00:00:00", "foo".parseDuration())
    }
}
