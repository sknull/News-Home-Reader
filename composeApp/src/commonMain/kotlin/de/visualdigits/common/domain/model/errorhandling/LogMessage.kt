package de.visualdigits.common.domain.model.errorhandling

import androidx.compose.runtime.Immutable
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import java.time.OffsetDateTime

@Immutable
data class LogMessage(
    val timestamp: OffsetDateTime,
    val severity: Severity,
    val message: String,
    val throwable: Throwable?
) {

    override fun toString(): String {
        val stackTrace = throwable?.let { t ->
            "\n${
                t.stackTraceToString().split("\n").joinToString("\n") { l -> "${severity}: $l" }
            }"
        }?:""

        return "${severity}: ${message}$stackTrace"
    }

    @Suppress("NOTHING_TO_INLINE")
    companion object {

        val log = Logger.withTag("this")

        inline fun log(
            severity: Severity,
            message: String,
            throwable: Throwable? = null
        ): LogMessage = LogMessage(OffsetDateTime.now(), severity, message, throwable)

        inline fun log(
            logMessage: LogMessage
        ) {
            when (logMessage.severity) {
                Severity.Info -> log.i(logMessage.message, logMessage.throwable)
                Severity.Warn -> log.w(logMessage.message, logMessage.throwable)
                Severity.Error -> log.e(logMessage.message, logMessage.throwable)
                Severity.Verbose -> log.v(logMessage.message, logMessage.throwable)
                Severity.Debug -> log.d(logMessage.message, logMessage.throwable)
                Severity.Assert -> log.a(logMessage.message, logMessage.throwable)
            }
        }
    }
}
