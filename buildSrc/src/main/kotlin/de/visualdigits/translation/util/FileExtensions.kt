package de.visualdigits.translation.util

import com.github.doyaaaaaken.kotlincsv.dsl.context.WriteQuoteMode
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

fun File.writeCsv(keys: List<String>, data: List<List<String>>) {
    csvWriter {
        delimiter = ';'
        lineTerminator = "\n"
        quote {
            mode = WriteQuoteMode.ALL
            char = '\"'
        }
        outputLastLineTerminator = true
    }.writeAll(listOf(keys) + data, this)
}

fun File.readCsv(): Pair<List<String>, List<List<String>>> {
    val table = csvReader {
        delimiter = ';'
        quoteChar = '\"'
    }.readAll(this)

    return Pair(table.take(1).first(), table.drop(1).sortedBy { row -> row[0] })
}
