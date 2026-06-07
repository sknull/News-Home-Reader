package de.visualdigits.newshomereader.domain.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StringUtilTest {

    @Test
    fun testDuration() {
        assertEquals("03:45", "PT3M45S".parseDuration())
        assertEquals("03:45", "3M45S".parseDuration())
        assertEquals("00:45", "45S".parseDuration())
        assertEquals("03:00", "03M".parseDuration())
    }
}
