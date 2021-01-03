package com.likethesalad.placeholder.data

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.rawStrings.data.helpers.files.ValuesFolderXmlFiles
import org.junit.Test
import java.io.File

class ValuesFolderXmlFilesTest {

    @Test
    fun `Get string resources from files`() {
        val valuesFilesSet = setOf(
            getResourceFile("strings_1.xml"),
            getResourceFile("strings_2.xml")
        )

        val valuesFiles =
            ValuesFolderXmlFiles(
                "values",
                valuesFilesSet
            )

        Truth.assertThat(valuesFiles.valuesFolderName).isEqualTo("values")
        Truth.assertThat(valuesFiles.xmlFiles).containsExactlyElementsIn(valuesFilesSet)
    }

    private fun getResourceFile(fileName: String): File {
        return File(javaClass.getResource("raw/$fileName").file)
    }
}