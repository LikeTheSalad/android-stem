package com.likethesalad.placeholder.data.storage.utils

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.ValuesXmlFiles
import com.likethesalad.placeholder.models.ValuesStrings
import com.likethesalad.placeholder.models.VariantXmlFiles
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class ValuesStringsBaseProviderTest {

    private lateinit var valuesStringsProvider: ValuesStringsProvider
    private lateinit var valuesStringsBaseProvider: ValuesStringsBaseProvider

    @Before
    fun setUp() {
        valuesStringsProvider = mockk()
        valuesStringsBaseProvider = ValuesStringsBaseProvider(valuesStringsProvider)
    }

    @Test
    fun `Get base values strings for base folder`() {
        val baseVariantXmlFiles = mockk<VariantXmlFiles>()
        val baseValuesStrings = valuesStringsBaseProvider.getBaseValuesStringsFor(
            "values",
            baseVariantXmlFiles
        )
        Truth.assertThat(baseValuesStrings).isNull()
        verify(exactly = 0) {
            valuesStringsProvider.getValuesStringsForFolderFromVariants(any(), any(), any())
        }
    }

//    @Test
//    fun `Get base values strings for non-base folder`() {
//        val folderName = "values-es"
//        val baseVariantName = "main"
//        val baseValuesXmlFiles = mockk<ValuesXmlFiles>()
//        val baseVariantXmlFiles = VariantXmlFiles(baseVariantName, baseValuesXmlFiles)
//
//        val result = valuesStringsBaseProvider.getBaseValuesStringsFor(folderName, baseVariantXmlFiles)
//
//        Truth.assertThat(result).isEqualTo(
//            ValuesStrings(baseVariantName, folderName, baseVariantXmlFiles, null)
//        )
//    }
}