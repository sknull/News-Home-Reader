package de.visualdigits.generated

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AppVersionTest {

    @Test
    fun testCompare() {
        val v1 = AppVersion("1.0.0")
        val v2 = AppVersion("1.0.1")
        val v3 = AppVersion("1.1.0")

        assertTrue(v2 > v1)
        assertTrue(v3 > v1)
        assertTrue(v3 > v2)

        assertFalse(v2 < v1)
        assertFalse(v3 < v1)
        assertFalse(v3 < v2)
    }
}
