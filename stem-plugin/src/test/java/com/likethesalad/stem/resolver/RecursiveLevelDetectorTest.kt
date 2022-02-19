package com.likethesalad.stem.resolver

import com.google.common.truth.Truth
import com.likethesalad.stem.modules.resolveStrings.resolver.RecursiveLevelDetector
import com.likethesalad.stem.modules.resolveStrings.resolver.TemplateContainerFinder
import com.likethesalad.tools.resource.api.android.AndroidResourceScope
import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.environment.Variant
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import junit.framework.TestCase.fail
import org.junit.Test

class RecursiveLevelDetectorTest {

    private val scope = AndroidResourceScope(Variant.Default, Language.Default)

    @Test
    fun check_orderTemplatesByRecursiveLevel() {
        // Given:
        val levelOneTemplate =
            StringAndroidResource(
                "name",
                "The name",
                scope
            )
        val levelOneTemplate2 =
            StringAndroidResource(
                "description",
                "The description",
                scope
            )
        val levelTwoTemplate =
            StringAndroidResource(
                "the_message",
                "Hello \${name}",
                scope
            )
        val levelThreeTemplate =
            StringAndroidResource(
                "the_message_2",
                "Hello2 \${the_message}",
                scope
            )
        val levelThreeTemplate2 =
            StringAndroidResource(
                "message_4",
                "The message4 \${the_message}",
                scope
            )
        val levelFourTemplate =
            StringAndroidResource(
                "message_3",
                "The message3 \${the_message_2}",
                scope
            )
        val levelFourTemplate2 =
            StringAndroidResource(
                "message_5",
                "The message5 \${the_message} \${message_4}",
                scope
            )
        val templates = listOf(
            levelOneTemplate,
            levelTwoTemplate,
            levelThreeTemplate,
            levelOneTemplate2,
            levelFourTemplate,
            levelThreeTemplate2,
            levelFourTemplate2
        )
        val recursiveLevelDetector = RecursiveLevelDetector()
        val templateContainerFinder = TemplateContainerFinder(templates.map { it.name() })

        // When:
        val result = recursiveLevelDetector.orderTemplatesByRecursiveLevel(templates, templateContainerFinder)

        // Then:
        Truth.assertThat(result.size).isEqualTo(4)
        Truth.assertThat(result[0]).containsExactly(
            levelOneTemplate, levelOneTemplate2
        )
        Truth.assertThat(result[1]).containsExactly(
            levelTwoTemplate
        )
        Truth.assertThat(result[2]).containsExactly(
            levelThreeTemplate, levelThreeTemplate2
        )
        Truth.assertThat(result[3]).containsExactly(
            levelFourTemplate, levelFourTemplate2
        )
    }

    @Test
    fun check_orderTemplatesByRecursiveLevel_verify_circular_dependency() {
        // Given:
        val template = StringAndroidResource(
            "name",
            "The name",
            scope
        )
        val template2 =
            StringAndroidResource(
                "description",
                "The description and message \${the_message}",
                scope
            )
        val template3 = StringAndroidResource(
            "the_message",
            "Hello \${description}",
            scope
        )
        val templates = listOf(
            template,
            template2,
            template3
        )
        val recursiveLevelDetector = RecursiveLevelDetector()
        val templateContainerFinder = TemplateContainerFinder(templates.map { it.name() })

        // When:
        try {
            recursiveLevelDetector.orderTemplatesByRecursiveLevel(templates, templateContainerFinder)
            fail()
        } catch (e: IllegalArgumentException) {
            // Then:
            Truth.assertThat(e.message).isEqualTo("Circular dependency on string templates")
        }
    }
}