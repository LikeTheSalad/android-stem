package com.likethesalad.placeholder.modules.templateStrings.collector

import com.google.common.truth.Truth
import com.likethesalad.tools.resource.api.android.AndroidResourceScope
import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class TemplatesCollectionProviderTest {

    @Test
    fun `Get resources by language and sorted by resource name`() {
        val customLanguage = Language.Custom("es")
        val resource1 = createResource("resource1", Language.Default)
        val resource2 = createResource("resource2", customLanguage)
        val resource3 = createResource("resource3", customLanguage)
        val resource4 = createResource("resource4", Language.Custom("it"))
        val instance = TemplatesCollectionProvider(listOf(resource3, resource1, resource4, resource2))

        val result = instance.getCollectionByLanguage(customLanguage)

        Truth.assertThat(result!!.getAllResources()).containsExactly(
            resource2, resource3
        ).inOrder()
    }

    @Test
    fun `Get list of unique languages found across resources`() {
        val language1 = Language.Default
        val language2 = Language.Custom("es")
        val language3 = Language.Custom("jp")
        val resource1 = createResource("someResource1", language1)
        val resource2 = createResource("someResource2", language1)
        val resource3 = createResource("someResource3", language1)
        val resource4 = createResource("someResource4", language3)
        val resource5 = createResource("someResource5", language2)
        val resource6 = createResource("someResource6", language2)
        val instance = TemplatesCollectionProvider(
            listOf(
                resource1, resource2, resource3, resource4, resource5, resource6
            )
        )

        val result = instance.listLanguages()

        Truth.assertThat(result.size).isEqualTo(3)
        Truth.assertThat(result).containsExactly(language1, language2, language3)
    }

    private fun createResource(name: String, language: Language): StringAndroidResource {
        val resourceMock = mockk<StringAndroidResource>()
        val scopeMock = mockk<AndroidResourceScope>()
        every { resourceMock.getAndroidScope() }.returns(scopeMock)
        every { scopeMock.language }.returns(language)
        every { resourceMock.name() }.returns(name)

        return resourceMock
    }
}