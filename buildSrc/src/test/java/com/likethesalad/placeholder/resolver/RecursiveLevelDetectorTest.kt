package com.likethesalad.placeholder.resolver

import com.google.common.truth.Truth
import com.likethesalad.placeholder.models.StringResourceModel
import junit.framework.Assert.fail
import org.junit.Test

class RecursiveLevelDetectorTest {

    @Test
    fun check_orderTemplatesByRecursiveLevel() {
        // Given:
        val levelOneTemplate = StringResourceModel("template_name", "The name")
        val levelOneTemplate2 = StringResourceModel("template_description", "The description")
        val levelTwoTemplate = StringResourceModel("template_the_message", "Hello \${template_name}")
        val levelThreeTemplate = StringResourceModel("template_the_message_2", "Hello2 \${template_the_message}")
        val levelThreeTemplate2 = StringResourceModel("template_message_4", "The message4 \${template_the_message}")
        val levelFourTemplate = StringResourceModel("template_message_3", "The message3 \${template_the_message_2}")
        val levelFourTemplate2 =
            StringResourceModel("template_message_5", "The message5 \${template_the_message} \${template_message_4}")
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
        val template = StringResourceModel("template_name", "The name")
        val template2 =
            StringResourceModel("template_description", "The description and message \${template_the_message}")
        val template3 = StringResourceModel("template_the_message", "Hello \${template_description}")
        val templates = listOf(
            template,
            template2,
            template3
        )
        val recursiveLevelDetector = RecursiveLevelDetector()

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