package com.likethesalad.stem.resolver

import com.google.common.truth.Truth
import com.likethesalad.stem.modules.resolveStrings.resolver.RecursiveLevelDetector
import com.likethesalad.stem.modules.resolveStrings.resolver.TemplateResolver
import com.likethesalad.stem.modules.templateStrings.models.StringsTemplatesModel
import com.likethesalad.tools.resource.api.android.attributes.plain
import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.environment.Variant
import com.likethesalad.tools.resource.api.android.impl.AndroidResourceScope
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import com.likethesalad.tools.resource.api.attributes.AttributeKey
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class TemplateResolverTest {

    private lateinit var templateResolver: TemplateResolver
    private lateinit var recursiveLevelDetectorSpy: RecursiveLevelDetector
    private val scope = AndroidResourceScope(Variant.Default, Language.Default)

    @Before
    fun setUp() {
        recursiveLevelDetectorSpy = spyk(RecursiveLevelDetector())
        templateResolver =
            TemplateResolver(
                recursiveLevelDetectorSpy
            )
    }

    @Test
    fun check_resolveTemplates_simple() {
        // Given:
        val templates = listOf(
            StringAndroidResource(
                "welcome",
                "Welcome \${name}",
                scope
            ),
            StringAndroidResource(
                "address_input",
                "The address is \${address} for the \${name}",
                scope
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
        Truth.assertThat(first.stringValue()).isEqualTo("Welcome The name")
        val second = result[1]
        Truth.assertThat(second.name()).isEqualTo("address_input")
        Truth.assertThat(second.stringValue()).isEqualTo("The address is The address for the The name")
        verify(exactly = 0) { recursiveLevelDetectorSpy.orderTemplatesByRecursiveLevel(any(), any()) }
    }

    @Test
    fun check_resolveTemplates_simple_keep_attrs() {
        // Given:
        val attrs = mapOf<AttributeKey, String>(
            plain("one_attr") to "one_value",
            plain("other_attr") to "other value",
            plain("name") to "the_name"
        )
        val templates = listOf(
            StringAndroidResource(
                attrs,
                "This is the name: \${name}",
                scope
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
        Truth.assertThat(first.stringValue()).isEqualTo("This is the name: The name")
        Truth.assertThat(first.attributes().asMap().mapKeys { it.key.getName() }).containsExactly(
            "plain|one_attr|", "one_value",
            "plain|other_attr|", "other value",
            "plain|name|", "the_name"
        )
    }

    @Test
    fun check_resolveTemplates_recursive() {
        // Given:
        val templates = listOf(
            StringAndroidResource(
                "welcome",
                "Welcome \${name}",
                scope
            ),
            StringAndroidResource(
                "address_input",
                "The address is \${address} for the \${name}",
                scope
            ),
            StringAndroidResource(
                "using_other_template",
                "Reused: \${welcome}",
                scope
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
        Truth.assertThat(first.stringValue()).isEqualTo("Welcome The name")
        val second = result[1]
        Truth.assertThat(second.name()).isEqualTo("address_input")
        Truth.assertThat(second.stringValue()).isEqualTo("The address is The address for the The name")
        val third = result[2]
        Truth.assertThat(third.name()).isEqualTo("using_other_template")
        Truth.assertThat(third.stringValue()).isEqualTo("Reused: Welcome The name")
        verify { recursiveLevelDetectorSpy.orderTemplatesByRecursiveLevel(any(), any()) }
    }
}