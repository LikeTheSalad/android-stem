package com.likethesalad.stem.modules.resolveStrings.resolver

import com.likethesalad.android.protos.StringResource
import com.likethesalad.stem.configuration.StemConfiguration
import com.likethesalad.stem.testutils.createForTest
import com.likethesalad.stem.testutils.named
import com.likethesalad.stem.tools.extensions.name
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

class RecursiveLevelDetectorTest {

    @Test
    fun check_orderTemplatesByRecursiveLevel() {
        // Given:
        val levelOneTemplate =
            StringResource.named(
                "name",
                "The name",
            )
        val levelOneTemplate2 =
            StringResource.named(
                "description",
                "The description"
            )
        val levelTwoTemplate =
            StringResource.named(
                "the_message",
                "Hello \${name}"
            )
        val levelThreeTemplate =
            StringResource.named(
                "the_message_2",
                "Hello2 \${the_message}"
            )
        val levelThreeTemplate2 =
            StringResource.named(
                "message_4",
                "The message4 \${the_message}"
            )
        val levelFourTemplate =
            StringResource.named(
                "message_3",
                "The message3 \${the_message_2}"
            )
        val levelFourTemplate2 =
            StringResource.named(
                "message_5",
                "The message5 \${the_message} \${message_4}"
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
        val templateContainerFinder = getTemplateContainerFinder(templates)

        // When:
        val result = recursiveLevelDetector.orderTemplatesByRecursiveLevel(templates, templateContainerFinder)

        // Then:
        assertThat(result.size).isEqualTo(4)
        assertThat(result[0]).containsExactly(
            levelOneTemplate, levelOneTemplate2
        )
        assertThat(result[1]).containsExactly(
            levelTwoTemplate
        )
        assertThat(result[2]).containsExactly(
            levelThreeTemplate, levelThreeTemplate2
        )
        assertThat(result[3]).containsExactly(
            levelFourTemplate, levelFourTemplate2
        )
    }

    @Test
    fun check_orderTemplatesByRecursiveLevel_verify_circular_dependency() {
        // Given:
        val template = StringResource.named(
            "name",
            "The name"
        )
        val template2 =
            StringResource.named(
                "description",
                "The description and message \${the_message}"
            )
        val template3 = StringResource.named(
            "the_message",
            "Hello \${description}"
        )
        val templates = listOf(
            template,
            template2,
            template3
        )
        val recursiveLevelDetector = RecursiveLevelDetector()
        val templateContainerFinder = getTemplateContainerFinder(templates)

        // When:
        try {
            recursiveLevelDetector.orderTemplatesByRecursiveLevel(templates, templateContainerFinder)
            fail()
        } catch (e: IllegalArgumentException) {
            // Then:
            assertThat(e.message).isEqualTo("Circular dependency on string templates")
        }
    }

    private fun getTemplateContainerFinder(templates: List<StringResource>): TemplateContainerFinder {
        val configuration = StemConfiguration.createForTest()
        return TemplateContainerFinder(configuration, templates.map { it.name() })
    }
}