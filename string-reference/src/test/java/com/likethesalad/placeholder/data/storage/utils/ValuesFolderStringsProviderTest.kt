package com.likethesalad.placeholder.data.storage.utils

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.common.models.StringResourceModel
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
        valuesFolderStringsProvider =
            ValuesFolderStringsProvider()
    }

    @Test
    fun `Get values strings for folder with null parent`() {
        val valuesXmlFilesMainMock = mockk<ValuesFolderXmlFiles>()
        val valuesXmlFilesDebugMock = mockk<ValuesFolderXmlFiles>()
        val valuesXmlFilesDemoMock = mockk<ValuesFolderXmlFiles>()
        every { valuesXmlFilesMainMock.stringResources }.returns(emptySet())
        every { valuesXmlFilesDebugMock.stringResources }.returns(emptySet())
        every { valuesXmlFilesDemoMock.stringResources }.returns(emptySet())
        val valuesFolderName = "values"
        val variantXmlFilesList = listOf(
            VariantXmlFiles(
                "debug", listOf(
                    valuesXmlFilesDebugMock,
                    valuesXmlFilesDebugMock
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
        val valuesXmlFilesMainMock = mockk<ValuesFolderXmlFiles>()
        val valuesXmlFilesDebugMock = mockk<ValuesFolderXmlFiles>()
        val valuesXmlFilesDemoMock = mockk<ValuesFolderXmlFiles>()
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
        val valuesXmlFilesMainMock = mockk<ValuesFolderXmlFiles>()
        val valuesXmlFilesDebugMock = mockk<ValuesFolderXmlFiles>()
        val valuesXmlFilesDemoMock = mockk<ValuesFolderXmlFiles>()
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
        val parent = mockk<ValuesFolderStrings>()

        val result = valuesFolderStringsProvider.getValuesStringsForFolderFromVariants(
            valuesFolderName,
            variantXmlFilesList,
            parent
        )

        Truth.assertThat(result).isNull()
    }

    private fun createValuesXmlFilesMock(
        folderName: String,
        stringResources: Set<StringResourceModel> = emptySet()
    ): ValuesFolderXmlFiles {
        val mock = mockk<ValuesFolderXmlFiles>()
        every { mock.valuesFolderName }.returns(folderName)
        every { mock.stringResources }.returns(stringResources)

        return mock
    }
}