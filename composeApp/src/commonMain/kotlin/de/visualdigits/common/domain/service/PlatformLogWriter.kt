package de.visualdigits.common.domain.service

import co.touchlab.kermit.LogWriter

expect fun getPlatformLogWriters(): List<LogWriter>
