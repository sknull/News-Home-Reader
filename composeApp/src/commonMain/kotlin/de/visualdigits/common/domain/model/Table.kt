package de.visualdigits.common.domain.model

class Table(
    val keys: List<String> = listOf(),
    val data: List<List<String>> = listOf()
) {

    private val dataMap: List<Map<String, String>> = data.map { row -> keys.zip(row).toMap() }

    override fun toString(): String {
        return keys.joinToString(";") + "\n" +
                data.joinToString("\n") { row -> row.joinToString(";") }
    }

    fun get(key: String, value: String): Map<String, String>? {
        check(keys.contains(key)) { "Given key '$key' is not in this table" }
        return dataMap.find { row -> row[key] == value }
    }

    fun any(key: String, value: String): Boolean {
        check(keys.contains(key)) { "Given key '$key' is not in this table" }
        return dataMap.any { row -> row[key] == value }
    }
}
