package com.likethesalad.android.templates.provider.task.action.helpers

import com.google.common.truth.Truth
import com.likethesalad.android.templates.provider.tasks.service.action.helpers.ClassNameGenerator
import com.likethesalad.android.templates.provider.tasks.service.action.helpers.RandomStringGenerator
import com.likethesalad.tools.testing.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import java.util.Base64
import javax.lang.model.SourceVersion

class ClassNameGeneratorTest : BaseMockable() {

    @MockK
    lateinit var randomStringGenerator: RandomStringGenerator

    @MockK
    lateinit var encoder: Base64.Encoder

    private val packageName = "com.likethesalad.android.templates.provider.implementation"
    private val randomString = "someRandomString"
    private val identifier = "some-name"

    private lateinit var generator: ClassNameGenerator

    @Before
    fun setUp() {
        generator = ClassNameGenerator(randomStringGenerator, encoder)
    }

    @Test
    fun `Generate name`() {
        val rawStringEncoded = "MTIzZTQ1NjctZTg5Yi00MmQzLWE0NTYtNTU2NjQyNDQwMDAwX3NvbWUtbmFtZQ=="
        val expectedResult = "${packageName}.A_MTIzZTQ1NjctZTg5Yi00MmQzLWE0NTYtNTU2NjQyNDQwMDAwX3NvbWUtbmFtZQ__"

        verifyNameGenerated(rawStringEncoded, expectedResult)
    }

    @Test
    fun `Generate name when having plus char`() {
        val rawStringEncoded = "aBfgH18JK+89AA=="
        val expectedResult = "${packageName}.A_aBfgH18JK€89AA__"

        verifyNameGenerated(rawStringEncoded, expectedResult)
    }

    @Test
    fun `Generate name when having slash char`() {
        val rawStringEncoded = "aBfgH18JK/89AA=="
        val expectedResult = "${packageName}.A_aBfgH18JK$89AA__"

        verifyNameGenerated(rawStringEncoded, expectedResult)
    }

    @Test
    fun `Generate name when having subsequent non-alphanumeric chars`() {
        val rawStringEncoded = "aBfgH///18+JK++89AA=="
        val expectedResult = "${packageName}.A_aBfgH$$$18€JK€€89AA__"

        verifyNameGenerated(rawStringEncoded, expectedResult)
    }

    private fun verifyNameGenerated(rawStringEncoded: String, expectedResult: String) {
        val rawString = "${randomString}_$identifier"
        every { encoder.encodeToString(rawString.toByteArray()) }.returns(rawStringEncoded)
        every { randomStringGenerator.generate() }.returns(randomString)

        val result = generator.generate(identifier)

        Truth.assertThat(result).isEqualTo(expectedResult)
        Truth.assertThat(SourceVersion.isName(result)).isTrue()
    }
}