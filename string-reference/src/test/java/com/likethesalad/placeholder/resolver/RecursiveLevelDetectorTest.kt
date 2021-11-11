package com.likethesalad.placeholder.resolver

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.resolveStrings.resolver.RecursiveLevelDetector
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
                "template_name",
                "The name",
                scope
            )
        val levelOneTemplate2 =
            StringAndroidResource(
                "template_description",
                "The description",
                scope
            )
        val levelTwoTemplate =
            StringAndroidResource(
                "template_the_message",
                "Hello \${template_name}",
                scope
            )
        val levelThreeTemplate =
            StringAndroidResource(
                "template_the_message_2",
                "Hello2 \${template_the_message}",
                scope
            )
        val levelThreeTemplate2 =
            StringAndroidResource(
                "template_message_4",
                "The message4 \${template_the_message}",
                scope
            )
        val levelFourTemplate =
            StringAndroidResource(
                "template_message_3",
                "The message3 \${template_the_message_2}",
                scope
            )
        val levelFourTemplate2 =
            StringAndroidResource(
                "template_message_5",
                "The message5 \${template_the_message} \${template_message_4}",
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
        val recursiveLevelDetector =
            RecursiveLevelDetector()

        // When:
        val result = recursiveLevelDetector.orderTemplatesByRecursiveLevel(templates)

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
            "template_name",
            "The name",
            scope
        )
        val template2 =
            StringAndroidResource(
                "template_description",
                "The description and message \${template_the_message}",
                scope
            )
        val template3 = StringAndroidResource(
            "template_the_message",
            "Hello \${template_description}",
            scope
        )
        val templates = listOf(
            template,
            template2,
            template3
        )
        val recursiveLevelDetector =
            RecursiveLevelDetector()

        // When:
        try {
            recursiveLevelDetector.orderTemplatesByRecursiveLevel(templates)
            fail()
        } catch (e: IllegalArgumentException) {
            // Then:
            Truth.assertThat(e.message).isEqualTo("Circular dependency on string templates")
        }
    }
}