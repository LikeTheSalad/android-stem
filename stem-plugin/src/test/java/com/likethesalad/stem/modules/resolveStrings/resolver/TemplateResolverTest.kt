package com.likethesalad.stem.modules.resolveStrings.resolver

import com.google.common.truth.Truth
import com.likethesalad.android.protos.Attribute
import com.likethesalad.android.protos.StringResource
import com.likethesalad.stem.configuration.StemConfiguration
import com.likethesalad.stem.modules.templateStrings.models.StringsTemplatesModel
import com.likethesalad.stem.testutils.createForTest
import com.likethesalad.stem.testutils.named
import com.likethesalad.stem.tools.extensions.name
import com.likethesalad.tools.resource.api.android.environment.Language
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
        templateResolver =
            TemplateResolver(
                StemConfiguration.createForTest(),
                recursiveLevelDetectorSpy
            )
    }

    @Test
    fun check_resolveTemplates_simple() {
        // Given:
        val templates = listOf(
            StringResource.named(
                "welcome",
                "Welcome \${name}"
            ),
            StringResource.named(
                "address_input",
                "The address is \${address} for the \${name}"
            )
        )
        val values = mapOf(
            "name" to "The name",
            "address" to "The address"
        )
        val templatesModel =
            StringsTemplatesModel(
                Language.Default, templates, values
            )

        // When:
        val result = templateResolver.resolveTemplates(templatesModel)

        // Then:
        Truth.assertThat(result.size).isEqualTo(2)
        val first = result[0]
        Truth.assertThat(first.name()).isEqualTo("welcome")
        Truth.assertThat(first.text).isEqualTo("Welcome The name")
        val second = result[1]
        Truth.assertThat(second.name()).isEqualTo("address_input")
        Truth.assertThat(second.text).isEqualTo("The address is The address for the The name")
        verify(exactly = 0) { recursiveLevelDetectorSpy.orderTemplatesByRecursiveLevel(any(), any()) }
    }

    @Test
    fun check_resolveTemplates_simple_keep_attrs() {
        // Given:
        val attrs = listOf(
            Attribute("one_attr", "one_value", null),
            Attribute("other_attr", "other value", null)
        )
        val templates = listOf(
            StringResource.named(
                "the_name",
                "This is the name: \${name}",
                attrs,
            )
        )
        val values = mapOf(
            "name" to "The name"
        )
        val templatesModel =
            StringsTemplatesModel(
                Language.Default, templates, values
            )

        // When:
        val result = templateResolver.resolveTemplates(templatesModel)

        // Then:
        Truth.assertThat(result.size).isEqualTo(1)
        val first = result.first()
        Truth.assertThat(first.name()).isEqualTo("the_name")
        Truth.assertThat(first.text).isEqualTo("This is the name: The name")
        Truth.assertThat(first.attributes.associate { it.name to it.text }).containsExactly(
            "one_attr", "one_value",
            "other_attr", "other value",
            "name", "the_name"
        )
    }

    @Test
    fun check_resolveTemplates_recursive() {
        // Given:
        val templates = listOf(
            StringResource.named(
                "welcome",
                "Welcome \${name}"
            ),
            StringResource.named(
                "address_input",
                "The address is \${address} for the \${name}"
            ),
            StringResource.named(
                "using_other_template",
                "Reused: \${welcome}"
            )
        )
        val values = mapOf(
            "name" to "The name",
            "address" to "The address",
            "welcome" to "Welcome \${name}"
        )
        val templatesModel =
            StringsTemplatesModel(
                Language.Default, templates, values
            )

        // When:
        val result = templateResolver.resolveTemplates(templatesModel)

        // Then:
        Truth.assertThat(result.size).isEqualTo(3)
        val first = result[0]
        Truth.assertThat(first.name()).isEqualTo("welcome")
        Truth.assertThat(first.text).isEqualTo("Welcome The name")
        val second = result[1]
        Truth.assertThat(second.name()).isEqualTo("address_input")
        Truth.assertThat(second.text).isEqualTo("The address is The address for the The name")
        val third = result[2]
        Truth.assertThat(third.name()).isEqualTo("using_other_template")
        Truth.assertThat(third.text).isEqualTo("Reused: Welcome The name")
        verify { recursiveLevelDetectorSpy.orderTemplatesByRecursiveLevel(any(), any()) }
    }
}