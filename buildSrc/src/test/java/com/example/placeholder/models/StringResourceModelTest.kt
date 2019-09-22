package com.example.placeholder.models

import com.google.common.truth.Truth
import org.junit.Test

class StringResourceModelTest {

    @Test
    fun checkConstructor_name_setup() {
        // Given
        val map = mapOf("name" to "some_name")
        val content = "some content"

        // When
        val stringResourceModel = StringResourceModel(map, content)

        // Then
        Truth.assertThat(stringResourceModel.name).isEqualTo("some_name")
        Truth.assertThat(stringResourceModel.content).isEqualTo("some content")
    }

    @Test
    fun checkSecondaryConstructor_attributes_setup() {
        // Given
        val name = "some_name"
        val content = "some content"

        // When
        val stringResourceModel = StringResourceModel(name, content)

        // Then
        Truth.assertThat(stringResourceModel.attributes).containsExactly("name", "some_name")
        Truth.assertThat(stringResourceModel.content).isEqualTo("some content")
    }

    @Test
    fun checkComparability_based_on_name() {
        // Given
        val srmList = mutableListOf(
            StringResourceModel("my_name", "random content"),
            StringResourceModel("my_other_name", "random content"),
            StringResourceModel("my_name_2", "content for smr3"),
            StringResourceModel("aaa", "content for smr4")
        )

        // When
        srmList.sort()

        // Then
        Truth.assertThat(srmList).containsExactly(
            StringResourceModel("aaa", "content for smr4"),
            StringResourceModel("my_name", "random content"),
            StringResourceModel("my_name_2", "content for smr3"),
            StringResourceModel("my_other_name", "random content")
        ).inOrder()
    }
}