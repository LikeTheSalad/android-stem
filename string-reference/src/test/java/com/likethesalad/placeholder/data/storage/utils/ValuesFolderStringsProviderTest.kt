package com.likethesalad.placeholder.data.storage.utils

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.rawStrings.data.ValuesFolderStringsProvider
import com.likethesalad.placeholder.modules.rawStrings.data.helpers.files.ValuesFolderXmlFiles
import com.likethesalad.placeholder.modules.rawStrings.models.ValuesFolderStrings
import com.likethesalad.placeholder.modules.rawStrings.models.VariantXmlFiles
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class ValuesFolderStringsProviderTest {

    private lateinit var valuesFolderStringsProvider: ValuesFolderStringsProvider

    @Before
    fun setUp() {
        valuesFolderStringsProvider = ValuesFolderStringsProvider()
    }

    @Test
    fun `Get values strings for folder with null parent`() {
        val valuesXmlFilesDebugMock = createValuesFolderXmlFiles("values")
        val valuesEsXmlFilesDebugMock = createValuesFolderXmlFiles("values-es")
        val valuesXmlFilesMainMock = createValuesFolderXmlFiles("values")
        val valuesEsXmlFilesDemoMock = createValuesFolderXmlFiles("values-es")
        val valuesFolderName = "values"
        val variantXmlFilesList = listOf(
            createVariantXmlFiles(
                "debug", valuesXmlFilesDebugMock, valuesEsXmlFilesDebugMock
            ),
            createVariantXmlFiles(
                "main", valuesXmlFilesMainMock
            ),
            createVariantXmlFiles(
                "demo", valuesEsXmlFilesDemoMock
            )
        )

        val result = valuesFolderStringsProvider.getValuesStringsForFolderFromVariants(
            valuesFolderName,
            variantXmlFilesList,
            null
        )

        Truth.assertThat(result?.valuesFolderXmlFiles).isEqualTo(valuesXmlFilesMainMock)
        Truth.assertThat(result?.valuesFolderName).isEqualTo(valuesFolderName)
        Truth.assertThat(result?.parentValuesFolderStrings).isEqualTo(
            ValuesFolderStrings(
                valuesFolderName,
                valuesXmlFilesDebugMock,
                null
            )
        )
    }

    @Test
    fun `Get values strings for folder with non null parent`() {
        val valuesXmlFilesMainMock = createValuesFolderXmlFiles("values")
        val valuesXmlFilesDebugMock = createValuesFolderXmlFiles("values")
        val valuesEsXmlFilesDebugMock = createValuesFolderXmlFiles("values-es")
        val valuesEsXmlFilesDemoMock = createValuesFolderXmlFiles("values-es")
        val valuesFolderName = "values"
        val variantXmlFilesList = listOf(
            createVariantXmlFiles(
                "debug", valuesXmlFilesDebugMock, valuesEsXmlFilesDebugMock
            ),
            createVariantXmlFiles(
                "main", valuesXmlFilesMainMock
            ),
            createVariantXmlFiles(
                "demo", valuesEsXmlFilesDemoMock
            )
        )
        val parent = mockk<ValuesFolderStrings>()

        val result = valuesFolderStringsProvider.getValuesStringsForFolderFromVariants(
            valuesFolderName,
            variantXmlFilesList,
            parent
        )

        Truth.assertThat(result?.valuesFolderXmlFiles).isEqualTo(valuesXmlFilesMainMock)
        Truth.assertThat(result?.valuesFolderName).isEqualTo(valuesFolderName)
        Truth.assertThat(result?.parentValuesFolderStrings).isEqualTo(
            ValuesFolderStrings(
                valuesFolderName,
                valuesXmlFilesDebugMock,
                parent
            )
        )
    }

    @Test
    fun `Get values strings for folder that does not exist in any variant`() {
        val valuesXmlFilesMainMock = createValuesFolderXmlFiles("values")
        val valuesXmlFilesDebugMock = createValuesFolderXmlFiles("values")
        val valuesItXmlFilesDebugMock = createValuesFolderXmlFiles("values-it")
        val valuesItXmlFilesDemoMock = createValuesFolderXmlFiles("values-it")
        val valuesFolderName = "values-es"
        val variantXmlFilesList = listOf(
            createVariantXmlFiles(
                "debug", valuesXmlFilesDebugMock, valuesItXmlFilesDebugMock
            ),
            createVariantXmlFiles(
                "main", valuesXmlFilesMainMock
            ),
            createVariantXmlFiles(
                "demo", valuesItXmlFilesDemoMock
            )
        )
        val parent = mockk<ValuesFolderStrings>()

        val result = valuesFolderStringsProvider.getValuesStringsForFolderFromVariants(
            valuesFolderName,
            variantXmlFilesList,
            parent
        )

        Truth.assertThat(result).isNull()
    }

    private fun createVariantXmlFiles(
        variantName: String,
        vararg valuesFolderXmlFiles: ValuesFolderXmlFiles
    ): VariantXmlFiles {
        val mock = mockk<VariantXmlFiles>()
        every { mock.variantName }.returns(variantName)
        every { mock.valuesFolderXmlFiles }.returns(valuesFolderXmlFiles.toList())
        every { mock.findValuesFolderXmlFilesByName(any()) } answers { callOriginal() }

        return mock
    }

    private fun createValuesFolderXmlFiles(folderName: String): ValuesFolderXmlFiles {
        return mockk<ValuesFolderXmlFiles>().also {
            every { it.valuesFolderName }.returns(folderName)
        }
    }
}