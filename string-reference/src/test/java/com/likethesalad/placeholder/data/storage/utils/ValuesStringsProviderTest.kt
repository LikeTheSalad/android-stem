package com.likethesalad.placeholder.data.storage.utils

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.rawStrings.data.helpers.files.ValuesXmlFiles
import com.likethesalad.placeholder.modules.rawStrings.models.ValuesStrings
import com.likethesalad.placeholder.modules.rawStrings.models.VariantXmlFiles
import com.likethesalad.placeholder.modules.rawStrings.data.ValuesStringsProvider
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class ValuesStringsProviderTest {

    private lateinit var valuesStringsProvider: ValuesStringsProvider

    @Before
    fun setUp() {
        valuesStringsProvider =
            ValuesStringsProvider()
    }

    @Test
    fun `Get values strings for folder with null parent`() {
        val valuesXmlFilesMainMock = mockk<ValuesXmlFiles>()
        val valuesXmlFilesDebugMock = mockk<ValuesXmlFiles>()
        val valuesXmlFilesDemoMock = mockk<ValuesXmlFiles>()
        every { valuesXmlFilesMainMock.stringResources }.returns(emptySet())
        every { valuesXmlFilesDebugMock.stringResources }.returns(emptySet())
        every { valuesXmlFilesDemoMock.stringResources }.returns(emptySet())
        val valuesFolderName = "values"
        val variantXmlFilesList = listOf(
            VariantXmlFiles(
                "debug", mapOf(
                    "values" to valuesXmlFilesDebugMock,
                    "values-es" to valuesXmlFilesDebugMock
                )
            ),
            VariantXmlFiles(
                "main", mapOf(
                    "values" to valuesXmlFilesMainMock
                )
            ),
            VariantXmlFiles(
                "demo", mapOf(
                    "values-es" to valuesXmlFilesDemoMock
                )
            )
        )

        val result = valuesStringsProvider.getValuesStringsForFolderFromVariants(
            valuesFolderName,
            variantXmlFilesList,
            null
        )

        Truth.assertThat(result?.valuesXmlFiles).isEqualTo(valuesXmlFilesMainMock)
        Truth.assertThat(result?.valuesFolderName).isEqualTo(valuesFolderName)
        Truth.assertThat(result?.parentValuesStrings).isEqualTo(
            ValuesStrings(
                valuesFolderName,
                valuesXmlFilesDebugMock,
                null
            )
        )
    }

    @Test
    fun `Get values strings for folder with non null parent`() {
        val valuesXmlFilesMainMock = mockk<ValuesXmlFiles>()
        val valuesXmlFilesDebugMock = mockk<ValuesXmlFiles>()
        val valuesXmlFilesDemoMock = mockk<ValuesXmlFiles>()
        every { valuesXmlFilesMainMock.stringResources }.returns(emptySet())
        every { valuesXmlFilesDebugMock.stringResources }.returns(emptySet())
        every { valuesXmlFilesDemoMock.stringResources }.returns(emptySet())
        val valuesFolderName = "values"
        val variantXmlFilesList = listOf(
            VariantXmlFiles(
                "debug", mapOf(
                    "values" to valuesXmlFilesDebugMock,
                    "values-es" to valuesXmlFilesDebugMock
                )
            ),
            VariantXmlFiles(
                "main", mapOf(
                    "values" to valuesXmlFilesMainMock
                )
            ),
            VariantXmlFiles(
                "demo", mapOf(
                    "values-es" to valuesXmlFilesDemoMock
                )
            )
        )
        val parent = mockk<ValuesStrings>()

        val result = valuesStringsProvider.getValuesStringsForFolderFromVariants(
            valuesFolderName,
            variantXmlFilesList,
            parent
        )

        Truth.assertThat(result?.valuesXmlFiles).isEqualTo(valuesXmlFilesMainMock)
        Truth.assertThat(result?.valuesFolderName).isEqualTo(valuesFolderName)
        Truth.assertThat(result?.parentValuesStrings).isEqualTo(
            ValuesStrings(
                valuesFolderName,
                valuesXmlFilesDebugMock,
                parent
            )
        )
    }

    @Test
    fun `Get values strings for folder that does not exist in any variant`() {
        val valuesXmlFilesMainMock = mockk<ValuesXmlFiles>()
        val valuesXmlFilesDebugMock = mockk<ValuesXmlFiles>()
        val valuesXmlFilesDemoMock = mockk<ValuesXmlFiles>()
        every { valuesXmlFilesMainMock.stringResources }.returns(emptySet())
        every { valuesXmlFilesDebugMock.stringResources }.returns(emptySet())
        every { valuesXmlFilesDemoMock.stringResources }.returns(emptySet())
        val valuesFolderName = "values-es"
        val variantXmlFilesList = listOf(
            VariantXmlFiles(
                "debug", mapOf(
                    "values" to valuesXmlFilesDebugMock,
                    "values-it" to valuesXmlFilesDebugMock
                )
            ),
            VariantXmlFiles(
                "main", mapOf(
                    "values" to valuesXmlFilesMainMock
                )
            ),
            VariantXmlFiles(
                "demo", mapOf(
                    "values-it" to valuesXmlFilesDemoMock
                )
            )
        )
        val parent = mockk<ValuesStrings>()

        val result = valuesStringsProvider.getValuesStringsForFolderFromVariants(
            valuesFolderName,
            variantXmlFilesList,
            parent
        )

        Truth.assertThat(result).isNull()
    }
}