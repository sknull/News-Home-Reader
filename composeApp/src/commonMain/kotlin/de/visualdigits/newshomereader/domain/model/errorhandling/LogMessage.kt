package de.visualdigits.newshomereader.domain.model.errorhandling

import androidx.compose.runtime.Immutable
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity

@Immutable
data class LogMessage(
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
        inline fun log(
            severity: Severity,
            message: String,
            throwable: Throwable? = null
        ): LogMessage = LogMessage(severity, message, throwable)

        inline fun log(logMessage: LogMessage) {
            when (logMessage.severity) {
                Severity.Info -> Logger.i(logMessage.message, logMessage.throwable)
                Severity.Warn -> Logger.w(logMessage.message, logMessage.throwable)
                Severity.Error -> Logger.e(logMessage.message, logMessage.throwable)
                Severity.Verbose -> Logger.v(logMessage.message, logMessage.throwable)
                Severity.Debug -> Logger.d(logMessage.message, logMessage.throwable)
                Severity.Assert -> Logger.a(logMessage.message, logMessage.throwable)
            }
        }
    }
}
