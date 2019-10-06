package com.likethesalad.placeholder.resolver

import com.google.common.truth.Truth
import com.likethesalad.placeholder.models.StringResourceModel
import com.likethesalad.placeholder.models.StringsTemplatesModel
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class TemplateResolverTest {

    private lateinit var templateResolver: TemplateResolver
    private lateinit var recursiveLevelDetectorSpy: RecursiveLevelDetector

    @Before
    fun setUp() {
        recursiveLevelDetectorSpy = spyk(RecursiveLevelDetector())
        templateResolver = TemplateResolver(recursiveLevelDetectorSpy)
    }

    @Test
    fun check_resolveTemplates_simple() {
        // Given:
        val templates = listOf(
            StringResourceModel("template_welcome", "Welcome \${name}"),
            StringResourceModel("template_address_input", "The address is \${address} for the \${name}")
        )
        val values = mapOf(
            "name" to "The name",
            "address" to "The address"
        )
        val templatesModel = StringsTemplatesModel("values", templates, values)

        // When:
        val result = templateResolver.resolveTemplates(templatesModel)

        // Then:
        Truth.assertThat(result.size).isEqualTo(2)
        val first = result[0]
        Truth.assertThat(first.name).isEqualTo("welcome")
        Truth.assertThat(first.content).isEqualTo("Welcome The name")
        val second = result[1]
        Truth.assertThat(second.name).isEqualTo("address_input")
        Truth.assertThat(second.content).isEqualTo("The address is The address for the The name")
        verify(exactly = 0) { recursiveLevelDetectorSpy.orderTemplatesByRecursiveLevel(any()) }
    }

    @Test
    fun check_resolveTemplates_simple_keep_attrs() {
        // Given:
        val attrs = mapOf(
            "one_attr" to "one_value",
            "other_attr" to "other value",
            "name" to "template_the_name"
        )
        val templates = listOf(
            StringResourceModel(attrs, "This is the name: \${name}")
        )
        val values = mapOf(
            "name" to "The name"
        )
        val templatesModel = StringsTemplatesModel("values", templates, values)

        // When:
        val result = templateResolver.resolveTemplates(templatesModel)

        // Then:
        Truth.assertThat(result.size).isEqualTo(1)
        val first = result.first()
        Truth.assertThat(first.name).isEqualTo("the_name")
        Truth.assertThat(first.content).isEqualTo("This is the name: The name")
        Truth.assertThat(first.attributes).containsExactly(
            "one_attr", "one_value",
            "other_attr", "other value",
            "name", "the_name"
        )
    }

    @Test
    fun check_resolveTemplates_recursive() {
        // Given:
        val templates = listOf(
            StringResourceModel("template_welcome", "Welcome \${name}"),
            StringResourceModel("template_address_input", "The address is \${address} for the \${name}"),
            StringResourceModel("template_using_other_template", "Reused: \${template_welcome}")
        )
        val values = mapOf(
            "name" to "The name",
            "address" to "The address",
            "template_welcome" to "Welcome \${name}"
        )
        val templatesModel = StringsTemplatesModel("values", templates, values)

        // When:
        val result = templateResolver.resolveTemplates(templatesModel)

        // Then:
        Truth.assertThat(result.size).isEqualTo(3)
        val first = result[0]
        Truth.assertThat(first.name).isEqualTo("welcome")
        Truth.assertThat(first.content).isEqualTo("Welcome The name")
        val second = result[1]
        Truth.assertThat(second.name).isEqualTo("address_input")
        Truth.assertThat(second.content).isEqualTo("The address is The address for the The name")
        val third = result[2]
        Truth.assertThat(third.name).isEqualTo("using_other_template")
        Truth.assertThat(third.content).isEqualTo("Reused: Welcome The name")
        verify { recursiveLevelDetectorSpy.orderTemplatesByRecursiveLevel(any()) }
    }
}