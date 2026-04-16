package de.visualdigits.translation.util

import co.touchlab.kermit.Logger
import de.visualdigits.translation.model.ResourceString
import de.visualdigits.translation.model.XmlResources
import nl.adaptivity.xmlutil.serialization.XML
import java.io.File
import java.nio.file.Paths


object TranslationUtil {

    private const val CSV_FILE = "stringresources.csv"

    /**
     * Extracts translations in <PROJECT_ROOT>/composeApp/src/composeResources
     * to a csv file which is stored under <PROJECT_ROOT>/translations.
     *
     * Alongside the full csv additional txt files are stored for each language found.
     * Those contain all keys found across translations in the same order.
     * They are meant to be dropped into the translation website of your choice and pasted back.
     * Make sure that the lines are exactly same as the original before pasting them back.
     *
     * The sister method joinUpdateTranslation() will join those single files back to the csv and then
     * trigger the update of translations.
     *
     * The [rootDir] must point to the directory containing the composeApp folder.
     */
    fun extractTranslation(
        rootDir: File
    ) {
        val stringResourcesDir = Paths.get(rootDir.canonicalPath, "composeApp", "src", "commonMain", "composeResources").toFile()
        val stringResources = stringResourcesDir
            .listFiles { f -> f.isDirectory && f.name.startsWith("values") }
            ?.associate { d ->
                var language = d.name.replace("values", "")
                language = if (language.isEmpty()) "default" else language.drop(1)
                val resources = XML.v1.invoke().decodeFromString<XmlResources>(File(d, "strings.xml").readText(), null)
                Pair(language, resources.strings.associate { s -> Pair(s.name, s.value) })
            } ?:mapOf()
        val languages = stringResources.keys.sorted()
        val missingEntries = languages.associateWith { sortedSetOf<String>() }
        val languageLists = languages.associateWith { mutableListOf<String>() }
        val allKeys = stringResources.values
            .flatMap { v -> v.keys }
            .distinct()
            .sorted()
        val rows = allKeys.map { key ->
            listOf(key) + languages.map { language ->
                val value = stringResources[language]?.get(key)?.unescapeStringResource()?:""
                if (value.isEmpty()) missingEntries[language]?.add(key)
                languageLists[language]?.add(value.replace("\n", " # ").replace("\\n", " # "))
                value
            }
        }

        val newLanguages = allKeys
            .filter { k -> k.startsWith("language_") }
            .map { k -> k.replace("language_", "") }
            .filter { k -> !languages.contains(k) }

        val targetDir = Paths.get(rootDir.canonicalPath, "translation").toFile()
        if (!targetDir.exists() && !targetDir.mkdirs()) error("Could not create targetDirectory '${targetDir.canonicalPath}'")

        val keysFile = File(targetDir, "keys.txt")
        Logger.i("Writing keys to file: $keysFile ")
        keysFile.writeText(allKeys.joinToString("\n"))

        val missingEntriesFile = File(targetDir, "missing-entries.txt")
        Logger.i("Writing report to file: $missingEntriesFile ")
        missingEntriesFile.writeText("Missing Keys\n${"=".repeat(40)}\n\n${missingEntries.toList().joinToString("\n\n") { (language, keys) -> "$language\n${"-".repeat(40)}\n${keys.joinToString("\n")}" }}")

        val csvFile = File(targetDir, CSV_FILE)
        Logger.i("Writing string resources.csv: ${csvFile.canonicalPath}")
        csvFile.writeCsv(languages, rows)

        languageLists.forEach { (language, values) ->
            val targetFile = File(targetDir, "stringresources-$language.txt")
            Logger.i("Writing string resources for langugae '$language' to file: ${targetFile.canonicalPath}")
            targetFile.writeText(values.joinToString("\n"))
        }

        if (newLanguages.isNotEmpty()) {
            Logger.i("Writing language files for new languages: ${newLanguages.joinToString(", ")}")
            newLanguages.forEach { language ->
                val targetFile = File(targetDir, "stringresources-$language.txt")
                if (!targetFile.exists()) {
                    targetFile.writeText("") // touch
                }
            }
        }
    }

    /**
     * Grabs the csv file stored under <PROJECT_ROOT>/translations and writes them back to valid
     * resource files for KMP.
     *
     * The [rootDir] must point to the directory containing the composeApp folder.
     */
    fun updateTranslation(
        rootDir: File
    ) {
        val stringResourcesDir = Paths.get(rootDir.canonicalPath, "composeApp", "src", "commonMain", "composeResources").toFile()
        val sourceFile = Paths.get(rootDir.canonicalPath, "translation", CSV_FILE).toFile()
        val (keys, data) = sourceFile.readCsv()
        val languages = keys.drop(1)
        val resources = languages.map { XmlResources() }
        data.forEach { row ->
                val key = row.take(1).first()
                val values = row.drop(1)
                val default = languages.zip(values).toMap()["default"]?:"?"
                values.mapIndexed{ index, v ->
                    val value = v.ifBlank { default }
                    resources[index].strings.add(ResourceString(name = key, value = value.escapeStringResource()))
                }
            }
        resources.forEachIndexed { index, resource ->
            val language = languages[index]
            val dirName = if (language == "default") "values" else "values-$language"
            val targetDir = Paths.get(stringResourcesDir.canonicalPath, dirName).toFile()
            if (!targetDir.exists() && !targetDir.mkdirs()) {
                error("Could not create target directory: ${targetDir.canonicalPath}")
            }
            val targetFile = File(targetDir, "strings.xml")
            Logger.i("Writing resource file: ${targetFile.canonicalPath}")
            targetFile.writeText(resource.writeValueAsXmlString(expandSelfClosingTags = true))
        }
    }

    /**
     * Grabs the single text files stored under <PROJECT_ROOT>/translations and synthesizes them back
     * to the csv file which joins all translations.
     * Afterward method updateTranslation() is called to update the translations for KMP.
     *
     * The [rootDir] must point to the directory containing the composeApp folder.
     */
    fun joinUpdateTranslation(
        rootDir: File
    ) {
        val sourceDir = Paths.get(rootDir.canonicalPath, "translation").toFile()
        val targetFile = File(sourceDir, CSV_FILE)
        val resourceKeys = File(sourceDir, "keys.txt").readLines()
        val expectedNumberOfRows = resourceKeys.size

        val sourceFiles = sourceDir
            .listFiles { f -> f.isFile && f.name.startsWith("stringresources") && f.name.endsWith(".txt") }
        val languages = sourceFiles.map { f -> f.name.replace("stringresources-", "").replace(".txt", "") }
        val data = sourceFiles
            ?.associate { f ->
                val language = f.name
                    .replace("stringresources-", "")
                    .dropLast(4)
                val lines = f.readLines()
                if (lines.size != expectedNumberOfRows) error("Single file '${f.name}' has not the expected number of lines - not joining!")
                Pair(language, resourceKeys.zip(lines).toMap())
            }?:mapOf()
        val keys = listOf("key") + languages
        val rows = mutableListOf<List<String>>()
        resourceKeys.forEach { resourceKey ->
            rows.add((listOf(resourceKey) + languages.map { language ->
                data[language]?.get(resourceKey)?.replace(" # ", "\\n")?:""
            }))
        }
        Logger.i("Writing string resources.csv: ${targetFile.canonicalPath}")
        targetFile.writeCsv(keys, rows)
        updateTranslation(rootDir)
    }
}
