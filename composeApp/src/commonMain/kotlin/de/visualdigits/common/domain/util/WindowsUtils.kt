package de.visualdigits.common.domain.util

import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.Table
import de.visualdigits.newshomereader.domain.model.errorhandling.LogMessage
import de.visualdigits.newshomereader.domain.model.errorhandling.LogMessage.Companion.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

object WindowsUtils {

    suspend fun getRunningTasks(
        logger: (LogMessage) -> Unit = { },
    ): Table = withContext(Dispatchers.IO) {
        val log = mutableListOf<LogMessage>()
        runCommand(
            command = listOf("tasklist"),
                workingDir = File(/* pathname = */ "C:\\Windows\\SysWOW64"),
            timeOut = Pair(0, TimeUnit.SECONDS),
            logger = { lm ->
                log.add(lm)
                logger(lm)
            },
        )

        val lines = log.firstOrNull()
            ?.message
            ?.split("\r\n")
            ?.filter { l -> l.isNotBlank() }
            ?:listOf()
        val template = lines[1]
            .split(" ")
            .map { t -> t.length }
        val data = lines.drop(2)
            .map { line ->
                splitByTemplate(line, template)
            }
        val table = Table(
            keys = splitByTemplate(log[0].message, template),
            data = data
        )

        table
    }

    /**
     * Runs the command represented by this string with the given working directory.
     */
    suspend fun runCommand(
        command: List<String>,
        workingDir: File,
        timeOut: Pair<Long, TimeUnit> = Pair(60, TimeUnit.MINUTES),
        logger: (LogMessage) -> Unit = { },
    ) = withContext(Dispatchers.IO) {
        try {
            val proc = ProcessBuilder(*command.toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()
            if (timeOut.first > 0) {
                proc.waitFor(timeOut.first, timeOut.second)
            }
            val message = proc.inputStream.bufferedReader().readText()
            if (message.isNotBlank()) {
                logger(log(Severity.Info, message))
            }
            val error = proc.errorStream.bufferedReader().readText()
            if (error.isNotBlank()) {
                logger(log(Severity.Error, error))
            }
        } catch(e: IOException) {
            throw IllegalStateException("Could not execute command", e)
        }
    }

    private fun splitByTemplate(
        line: String,
        template: List<Int>
    ): List<String> {
        var buffer = line
        return template.map { len ->
            val token = buffer.take(len).trim()
            buffer = buffer.drop(len + 1)
            token
        }
    }
}
