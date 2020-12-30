package com.likethesalad.placeholder.models

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.common.models.StringResourceModel
import com.likethesalad.placeholder.modules.rawStrings.data.helpers.files.ValuesFolderXmlFiles
import com.likethesalad.placeholder.modules.rawStrings.models.ValuesFolderStrings
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class ValuesFolderStringsTest {

    @Test
    fun `Get strings previously set`() {
        val resStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "my_str_name",
                        "The content"
                    ),
                    StringResourceModel(
                        "my_str_name2",
                        "The content2"
                    )
                )
            )
        )

        val listOfStrings = resStrings.mergedStrings

        Truth.assertThat(listOfStrings).containsExactly(
            StringResourceModel(
                "my_str_name",
                "The content"
            ),
            StringResourceModel(
                "my_str_name2",
                "The content2"
            )
        )
    }

    @Test
    fun `Get strings from parent`() {
        val parentResStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "the_parent_string",
                        "Parent value"
                    )
                )
            )
        )

        val resStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "child_string",
                        "Child value"
                    )
                )
            ),
            parentResStrings
        )

        Truth.assertThat(resStrings.mergedStrings).containsExactly(
            StringResourceModel(
                "the_parent_string",
                "Parent value"
            ),
            StringResourceModel(
                "child_string",
                "Child value"
            )
        )
    }

    @Test
    fun `Override parent strings content with the same name`() {
        val parentResStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "the_parent_string",
                        "Parent value"
                    ),
                    StringResourceModel(
                        "common_string",
                        "Parent common string"
                    )
                )
            )
        )

        val resStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "child_string",
                        "Child value"
                    ),
                    StringResourceModel(
                        "common_string",
                        "Child common string"
                    )
                )
            ),
            parentResStrings
        )

        Truth.assertThat(resStrings.mergedStrings).containsExactly(
            StringResourceModel(
                "the_parent_string",
                "Parent value"
            ),
            StringResourceModel(
                "child_string",
                "Child value"
            ),
            StringResourceModel(
                "common_string",
                "Child common string"
            )
        )
    }

    @Test
    fun `Get child attributes for overridden string`() {
        // Given
        val commonStringName = "common_string"
        val commonParentString =
            StringResourceModel(
                mapOf(
                    "name" to commonStringName,
                    "other" to "something"
                ),
                "Parent string content"
            )
        val parentResStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    commonParentString,
                    StringResourceModel(
                        "other_parent_string",
                        "Some parent string"
                    )
                )
            )
        )

        val resStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        mapOf(
                            "name" to commonStringName,
                            "child_attr" to "Other child attribute"
                        ),
                        "Child parent string"
                    )
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
            StringResourceModel(
                "other_parent_string",
                "Some parent string"
            )
        )
    }

    @Test
    fun `Get strings sorted alphabetically by name`() {
        // Given
        val parentResStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "the_parent_string",
                        "Parent value"
                    )
                )
            )
        )

        val resStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "child_string",
                        "Child value"
                    ),
                    StringResourceModel(
                        "another_string",
                        "Child another value"
                    )
                )
            ),
            parentResStrings
        )

        // Then
        Truth.assertThat(resStrings.mergedStrings).containsExactly(
            StringResourceModel(
                "another_string",
                "Child another value"
            ),
            StringResourceModel(
                "child_string",
                "Child value"
            ),
            StringResourceModel(
                "the_parent_string",
                "Parent value"
            )
        ).inOrder()
    }

    @Test
    fun `Get merged templates sorted by name`() {
        // Given
        val parentStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "string_name",
                        "String value"
                    ),
                    StringResourceModel(
                        "template_some_string",
                        "String value \${string_name}"
                    ),
                    StringResourceModel(
                        "template_other_string",
                        "String value2"
                    )
                )
            )
        )

        val resStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "child_string_name",
                        "Child value"
                    ),
                    StringResourceModel(
                        "template_child_string",
                        "Child template"
                    ),
                    StringResourceModel(
                        "template_some_string",
                        "Child value common"
                    )
                )
            ), parentStrings
        )

        // Then
        Truth.assertThat(resStrings.mergedTemplates).containsExactly(
            StringResourceModel(
                "template_child_string",
                "Child template"
            ),
            StringResourceModel(
                "template_other_string",
                "String value2"
            ),
            StringResourceModel(
                "template_some_string",
                "Child value common"
            )
        )
    }

    @Test
    fun `Get values folder name suffix`() {
        assertValuesFolderNameSuffix("values", "")
        assertValuesFolderNameSuffix("values-es", "-es")
        assertValuesFolderNameSuffix("values-es-rVE", "-es-rVE")
    }

    @Test
    fun `Get true when client has templates`() {
        // Given
        val parentStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "string_name",
                        "String value"
                    ),
                    StringResourceModel(
                        "template_some_string",
                        "String value \${string_name}"
                    ),
                    StringResourceModel(
                        "template_other_string",
                        "String value2"
                    )
                )
            )
        )

        val resStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "child_string_name",
                        "Child value"
                    ),
                    StringResourceModel(
                        "template_child_string",
                        "Child template"
                    ),
                    StringResourceModel(
                        "template_some_string",
                        "Child value common"
                    )
                )
            ), parentStrings
        )

        // Then
        Truth.assertThat(resStrings.hasTemplatesOrValues).isTrue()
    }

    @Test
    fun `Get true when client has values`() {
        // Given
        val parentStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "string_name",
                        "String value"
                    ),
                    StringResourceModel(
                        "template_some_string",
                        "String value \${string_name}"
                    ),
                    StringResourceModel(
                        "template_other_string",
                        "String value2"
                    )
                )
            )
        )

        val resStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "string_name",
                        "Child value"
                    )
                )
            ), parentStrings
        )

        // Then
        Truth.assertThat(resStrings.hasTemplatesOrValues).isTrue()
    }

    @Test
    fun `Get true when client has values from grand parent templates`() {
        // Given
        val parentStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "string_name",
                        "String value"
                    ),
                    StringResourceModel(
                        "template_some_string",
                        "String value \${string_name}"
                    ),
                    StringResourceModel(
                        "template_other_string",
                        "String value2"
                    )
                )
            )
        )

        val resStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "some_string_name",
                        "Child value"
                    )
                )
            ), parentStrings
        )

        val resStrings2 = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "string_name",
                        "Environment value"
                    )
                )
            ), resStrings
        )

        // Then
        Truth.assertThat(resStrings2.hasTemplatesOrValues).isTrue()
    }

    @Test
    fun `Get true when child has neither values nor templates but parent does`() {
        // Given
        val parentStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "string_name",
                        "String value"
                    ),
                    StringResourceModel(
                        "template_some_string",
                        "String value \${string_name}"
                    ),
                    StringResourceModel(
                        "template_other_string",
                        "String value2"
                    )
                )
            )
        )

        val resStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "custom_client_string",
                        "Child value"
                    )
                )
            ), parentStrings
        )

        // Then
        Truth.assertThat(resStrings.hasTemplatesOrValues).isTrue()
    }

    @Test
    fun `Get false when child and parent have neither values nor templates`() {
        // Given
        val parentStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "string_name",
                        "String value"
                    ),
                    StringResourceModel(
                        "some_string",
                        "String value2"
                    ),
                    StringResourceModel(
                        "other_string",
                        "String value3"
                    )
                )
            )
        )

        val resStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "custom_client_string",
                        "Child value"
                    )
                )
            ), parentStrings
        )

        // Then
        Truth.assertThat(resStrings.hasTemplatesOrValues).isFalse()
    }

    private fun assertValuesFolderNameSuffix(valuesFolderName: String, expectedSuffix: String) {
        val resStrings = ValuesFolderStrings(
            valuesFolderName,
            getValuesStringFiles(emptySet())
        )
        Truth.assertThat(resStrings.valuesSuffix).isEqualTo(expectedSuffix)
    }

    private fun getResStringsWithNoValuesForTemplates(): ValuesFolderStrings {
        return ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "some_string",
                        "Some content"
                    ),
                    StringResourceModel(
                        "template_other_string",
                        "Some content with \${another_string}"
                    )
                )
            )
        )
    }

    private fun getResStringsWithValuesForLocalTemplates(): ValuesFolderStrings {
        return ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "some_string",
                        "Some content"
                    ),
                    StringResourceModel(
                        "template_other_string",
                        "Some content with \${some_string}"
                    )
                )
            )
        )
    }

    private fun getResStringsWithValuesForParentTemplates(): ValuesFolderStrings {
        val parentResStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "template_other_string",
                        "Some content with \${some_string}"
                    )
                )
            )
        )
        return ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "some_string",
                        "Some content"
                    )
                )
            ), parentResStrings
        )
    }

    private fun getResStringsWithValuesForMergedTemplates(): ValuesFolderStrings {
        val parentResStrings = ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "template_other_string",
                        "Some content with \${random_string}"
                    )
                )
            )
        )
        return ValuesFolderStrings(
            "values",
            getValuesStringFiles(
                setOf(
                    StringResourceModel(
                        "template_other_string",
                        "Some content with \${some_string}"
                    ),
                    StringResourceModel(
                        "some_string",
                        "Some content"
                    )
                )
            ), parentResStrings
        )
    }

    private fun getValuesStringFiles(stringResources: Set<StringResourceModel>): ValuesFolderXmlFiles {
        val valuesStringFilesMock = mockk<ValuesFolderXmlFiles>()
        every { valuesStringFilesMock.stringResources }.returns(stringResources)
        return valuesStringFilesMock
    }
}