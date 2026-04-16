package de.visualdigits.common.domain.service

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import co.touchlab.kermit.platformLogWriter
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

actual fun getPlatformLogWriters(): List<LogWriter> {
    val userHome = System.getProperty("user.home")
    val logDirectory = File(userHome, ".newshomereader")
    if (!logDirectory.exists()) {
        logDirectory.mkdirs()
    }

    return listOf(CustomJvmFileLogWriter(File(logDirectory, "NewsHomeReader.log")), platformLogWriter())
}

class CustomJvmFileLogWriter(private val logFile: File) : LogWriter() {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        val timestamp = LocalDateTime.now().format(formatter)
        val logLine = "$timestamp [${severity.name}] $tag: $message\n"

        logFile.appendBytes(logLine.toByteArray(Charsets.UTF_8))

        throwable?.let {
            logFile.appendBytes("${it.stackTraceToString()}\n".toByteArray(Charsets.UTF_8))
        }
    }
}
