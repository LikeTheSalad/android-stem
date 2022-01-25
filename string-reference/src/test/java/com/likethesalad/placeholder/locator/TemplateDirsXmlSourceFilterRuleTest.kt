package com.likethesalad.placeholder.locator

import com.google.common.truth.Truth
import com.likethesalad.placeholder.locator.entrypoints.templates.source.TemplateDirsXmlSourceFilterRule
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.common.helpers.dirs.TemplatesDirHandler
import com.likethesalad.tools.resource.api.android.environment.Variant
import com.likethesalad.tools.resource.collector.android.data.resdir.ResDir
import com.likethesalad.tools.resource.collector.android.source.AndroidXmlResourceSource
import com.likethesalad.tools.testing.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class TemplateDirsXmlSourceFilterRuleTest : BaseMockable() {

    @MockK
    lateinit var androidVariantContext: AndroidVariantContext

    @MockK
    lateinit var templatesDirHandler: TemplatesDirHandler

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var templateDirsXmlSourceFilterRule: TemplateDirsXmlSourceFilterRule

    @Before
    fun setUp() {
        every { androidVariantContext.templatesDirHandler }.returns(templatesDirHandler)

        templateDirsXmlSourceFilterRule = TemplateDirsXmlSourceFilterRule(androidVariantContext)
    }

    @Test
    fun `Exclude files within templates dirs`() {
        val templateDir1 = createDir("some/template/dir")
        val templateDir2 = createDir("some/template/dir2")
        val normalDir = createDir("some/normal/dir")
        val template1 = File(templateDir1, "template1.xml")
        val template2 = File(templateDir1, "template2.xml")
        val template3 = File(templateDir2, "template3.xml")
        val normalFile = File(normalDir, "someFile.xml")
        val normalFile2 = File(normalDir, "someFile2.xml")
        val templateDirs = listOf(templateDir1, templateDir2)
        every { templatesDirHandler.templatesDirs }.returns(templateDirs.map { ResDir(Variant.Default, it) })

        checkFileExcluded(template1, true)
        checkFileExcluded(normalFile, false)
        checkFileExcluded(template2, true)
        checkFileExcluded(template3, true)
        checkFileExcluded(normalFile2, false)
    }

    private fun checkFileExcluded(file: File, expected: Boolean) {
        Truth.assertThat(templateDirsXmlSourceFilterRule.exclude(toAndroidResourceSource(file))).isEqualTo(expected)
    }

    private fun toAndroidResourceSource(file: File): AndroidXmlResourceSource {
        val mock = mockk<AndroidXmlResourceSource>()
        every { mock.getFileSource() }.returns(file)

        return mock
    }

    private fun createDir(path: String): File {
        val dirPath = path.split("/").toTypedArray()
        return temporaryFolder.newFolder(*dirPath)
    }
}