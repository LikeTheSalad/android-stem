package com.likethesalad.placeholder.models

import com.google.common.truth.Truth
import org.junit.Test

class ResStringsTest {

    @Test
    fun `Get strings previously set`() {
        val resStrings = ResStrings(
            listOf(
                StringResourceModel("my_str_name", "The content"),
                StringResourceModel("my_str_name2", "The content2")
            )
        )

        val listOfStrings = resStrings.mergedStrings

        Truth.assertThat(listOfStrings).containsExactly(
            StringResourceModel("my_str_name", "The content"),
            StringResourceModel("my_str_name2", "The content2")
        )
    }

    @Test
    fun `Get strings from parent`() {
        val parentResStrings = ResStrings(
            listOf(StringResourceModel("the_parent_string", "Parent value"))
        )

        val resStrings = ResStrings(
            listOf(StringResourceModel("child_string", "Child value")),
            parentResStrings
        )

        Truth.assertThat(resStrings.mergedStrings).containsExactly(
            StringResourceModel("the_parent_string", "Parent value"),
            StringResourceModel("child_string", "Child value")
        )
    }

    @Test
    fun `Override parent strings content with the same name`() {
        val parentResStrings = ResStrings(
            listOf(
                StringResourceModel("the_parent_string", "Parent value"),
                StringResourceModel("common_string", "Parent common string")
            )
        )

        val resStrings = ResStrings(
            listOf(
                StringResourceModel("child_string", "Child value"),
                StringResourceModel("common_string", "Child common string")
            ),
            parentResStrings
        )

        Truth.assertThat(resStrings.mergedStrings).containsExactly(
            StringResourceModel("the_parent_string", "Parent value"),
            StringResourceModel("child_string", "Child value"),
            StringResourceModel("common_string", "Child common string")
        )
    }

    @Test
    fun `Get child attributes for overridden string`() {
        // Given
        val commonStringName = "common_string"
        val commonParentString = StringResourceModel(
            mapOf(
                "name" to commonStringName,
                "other" to "something"
            ),
            "Parent string content"
        )
        val parentResStrings = ResStrings(
            listOf(
                commonParentString,
                StringResourceModel("other_parent_string", "Some parent string")
            )
        )

        val resStrings = ResStrings(
            listOf(
                StringResourceModel(
                    mapOf(
                        "name" to commonStringName,
                        "child_attr" to "Other child attribute"
                    ),
                    "Child parent string"
                )
            ), parentResStrings
        )

        // Then
        Truth.assertThat(resStrings.mergedStrings).containsExactly(
            StringResourceModel(
                mapOf(
                    "name" to commonStringName,
                    "child_attr" to "Other child attribute"
                ),
                "Child parent string"
            ),
            StringResourceModel("other_parent_string", "Some parent string")
        )
    }

    @Test
    fun `Get strings sorted alphabetically by name`() {
        // Given
        val parentResStrings = ResStrings(
            listOf(StringResourceModel("the_parent_string", "Parent value"))
        )

        val resStrings = ResStrings(
            listOf(
                StringResourceModel("child_string", "Child value"),
                StringResourceModel("another_string", "Child another value")
            ),
            parentResStrings
        )

        // Then
        Truth.assertThat(resStrings.mergedStrings).containsExactly(
            StringResourceModel("another_string", "Child another value"),
            StringResourceModel("child_string", "Child value"),
            StringResourceModel("the_parent_string", "Parent value")
        ).inOrder()
    }

    @Test
    fun `Get if it contains local templates`() {
        // Given
        val resStringsWithTemplates = ResStrings(
            listOf(
                StringResourceModel("string_name", "String value"),
                StringResourceModel("template_some_string", "String value \${string_name}")
            )
        )
        val resStringsWithNoTemplates = ResStrings(
            listOf(
                StringResourceModel("string_name", "String value"),
                StringResourceModel("some_string", "String value2")
            )
        )

        // Then
        Truth.assertThat(resStringsWithTemplates.hasLocalTemplates).isTrue()
        Truth.assertThat(resStringsWithNoTemplates.hasLocalTemplates).isFalse()
    }

    @Test
    fun `Get merged templates sorted by name`() {
        // Given
        val parentStrings = ResStrings(
            listOf(
                StringResourceModel("string_name", "String value"),
                StringResourceModel("template_some_string", "String value \${string_name}"),
                StringResourceModel("template_other_string", "String value2")
            )
        )

        val resStrings = ResStrings(
            listOf(
                StringResourceModel("child_string_name", "Child value"),
                StringResourceModel("template_child_string", "Child template"),
                StringResourceModel("template_some_string", "Child value common")
            ), parentStrings
        )

        // Then
        Truth.assertThat(resStrings.mergedTemplates).containsExactly(
            StringResourceModel("template_child_string", "Child template"),
            StringResourceModel("template_other_string", "String value2"),
            StringResourceModel("template_some_string", "Child value common")
        )
    }

    @Test
    fun `Get if it has values for templates`() {
        // Given
        val resStringsWithValuesForLocal = getResStringsWithValuesForLocalTemplates()
        val resStringsWithValuesForParent = getResStringsWithValuesForParentTemplates()
        val resStringsWithValuesForMerged = getResStringsWithValuesForMergedTemplates()
        val resStringsWithNoValuesForTemplates = getResStringsWithNoValuesForTemplates()

        // Then
        Truth.assertThat(resStringsWithValuesForLocal.hasLocalValuesForTemplates).isTrue()
        Truth.assertThat(resStringsWithValuesForParent.hasLocalValuesForTemplates).isTrue()
        Truth.assertThat(resStringsWithValuesForMerged.hasLocalValuesForTemplates).isTrue()
        Truth.assertThat(resStringsWithNoValuesForTemplates.hasLocalValuesForTemplates).isFalse()
    }

    private fun getResStringsWithNoValuesForTemplates(): ResStrings {
        return ResStrings(
            listOf(
                StringResourceModel("some_string", "Some content"),
                StringResourceModel("template_other_string", "Some content with \${another_string}")
            )
        )
    }

    private fun getResStringsWithValuesForLocalTemplates(): ResStrings {
        return ResStrings(
            listOf(
                StringResourceModel("some_string", "Some content"),
                StringResourceModel("template_other_string", "Some content with \${some_string}")
            )
        )
    }

    private fun getResStringsWithValuesForParentTemplates(): ResStrings {
        val parentResStrings = ResStrings(
            listOf(
                StringResourceModel("template_other_string", "Some content with \${some_string}")
            )
        )
        return ResStrings(
            listOf(
                StringResourceModel("some_string", "Some content")
            ), parentResStrings
        )
    }

    private fun getResStringsWithValuesForMergedTemplates(): ResStrings {
        val parentResStrings = ResStrings(
            listOf(
                StringResourceModel("template_other_string", "Some content with \${random_string}")
            )
        )
        return ResStrings(
            listOf(
                StringResourceModel("template_other_string", "Some content with \${some_string}"),
                StringResourceModel("some_string", "Some content")
            ), parentResStrings
        )
    }
}