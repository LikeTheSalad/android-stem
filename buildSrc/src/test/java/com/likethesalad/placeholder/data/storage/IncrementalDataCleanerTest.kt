package com.likethesalad.placeholder.data.storage

import com.google.common.truth.Truth
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class IncrementalDataCleanerTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var incrementalDirsProvider: IncrementalDirsProvider
    private lateinit var incrementalDataCleaner: IncrementalDataCleaner

    @Before
    fun setup() {
        incrementalDirsProvider = mockk()
        incrementalDataCleaner = IncrementalDataCleaner(incrementalDirsProvider)
    }

    @Test
    fun `Clear all incremental raw string files`() {
        val stringsDir = temporaryFolder.newFolder("strings")
        val files = addFiles("strings", "strings.json", "strings-es.json")
        every { incrementalDirsProvider.getRawStringsDir() }.returns(stringsDir)

        incrementalDataCleaner.clearRawStrings()

        assertDirWasEmptied(stringsDir, files)
    }

    @Test
    fun `Clear all incremental raw string files when it's empty`() {
        val stringsDir = temporaryFolder.newFolder("strings")
        val files = emptyList<File>()
        every { incrementalDirsProvider.getRawStringsDir() }.returns(stringsDir)

        incrementalDataCleaner.clearRawStrings()

        assertDirWasEmptied(stringsDir, files)
    }

    @Test
    fun `Clear all incremental template string files`() {
        val templatesDir = temporaryFolder.newFolder("templates")
        val files = addFiles("templates", "templates.json", "templates-es.json")
        every { incrementalDirsProvider.getTemplateStringsDir() }.returns(templatesDir)

        incrementalDataCleaner.clearTemplateStrings()

        assertDirWasEmptied(templatesDir, files)
    }

    private fun addFiles(folderName: String, vararg fileNames: String): List<File> {
        val filesAdded = mutableListOf<File>()
        for (fileName in fileNames) {
            filesAdded.add(temporaryFolder.newFile("$folderName/$fileName"))
        }

        return filesAdded
    }

    private fun assertDirWasEmptied(dir: File, oldFiles: List<File>) {
        for (file in oldFiles) {
            Truth.assertThat(file.exists()).isFalse()
        }
        Truth.assertThat(dir.exists()).isTrue()
        Truth.assertThat(dir.listFiles()).isEmpty()
    }
}