package com.likethesalad.placeholder.modules.templateStrings.collector

import com.google.common.truth.Truth
import com.likethesalad.tools.resource.api.android.environment.Variant
import com.likethesalad.tools.resource.collector.android.data.resdir.ResDir
import com.likethesalad.tools.resource.collector.android.source.providers.ResDirResourceSourceProvider
import com.likethesalad.tools.resource.collector.source.ResourceSource
import com.likethesalad.tools.testing.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Test
import java.io.File

class TemplatesResourceSourceProviderTest : BaseMockable() {

    @MockK
    lateinit var resDirResourceSourceProviderFactory: ResDirResourceSourceProvider.Factory

    @Test
    fun `Get templates sources`() {
        val source1 = mockk<ResourceSource>()
        val source2 = mockk<ResourceSource>()
        val source3 = mockk<ResourceSource>()
        val templateDir1 = createTemplateDir(Variant.Default, listOf(source1, source2))
        val templateDir2 = createTemplateDir(Variant.Custom("demo"), listOf(source3))

        val result = createInstance(listOf(templateDir1, templateDir2)).getSources()

        Truth.assertThat(result).containsExactly(source1, source2, source3)
    }

    private fun createTemplateDir(variant: Variant, sources: List<ResourceSource>): ResDir {
        val dir = mockk<File>()
        val resDirSourceProvider = mockk<ResDirResourceSourceProvider>()
        val resDir = ResDir(variant, dir)
        every { resDirResourceSourceProviderFactory.create(resDir) }.returns(resDirSourceProvider)
        every { resDirSourceProvider.getSources() }.returns(sources)
        return resDir
    }

    private fun createInstance(templatesDirs: List<ResDir>): TemplatesResourceSourceProvider {
        return TemplatesResourceSourceProvider(templatesDirs, resDirResourceSourceProviderFactory)
    }
}