package de.visualdigits.common.domain.service

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class JvmFileLogWriter(private val logFile: File) : LogWriter() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {

        scope.launch {
            try {
                val dateStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-DD-MM HH:mm:ss.SSS"))
                val logEntry = "$dateStamp [${severity.name}] $tag: $message\n"
                FileOutputStream(logFile, true).use { outs ->
                    outs.write(logEntry.toByteArray())
                    throwable?.let {
                        outs.write(it.stackTraceToString().toByteArray())
                    }
                }
            } catch (e: Exception) {
                println("Could not log using kermit: '${e.message}'")
            }
        }
    }
}
