package com.likethesalad.placeholder.models

import com.google.common.truth.Truth
import org.junit.Test

class TasksNamesModelTest {

    @Test
    fun check_taskNames_for_variant() {
        // Given:
        val variant = "theVariant"

        // When:
        val tasksNamesModel = TasksNamesModel(variant)

        // Then:
        Truth.assertThat(tasksNamesModel.gatherRawStringsName).isEqualTo("gatherTheVariantRawStrings")
        Truth.assertThat(tasksNamesModel.gatherStringTemplatesName).isEqualTo("gatherTheVariantStringTemplates")
        Truth.assertThat(tasksNamesModel.resolvePlaceholdersName).isEqualTo("resolveTheVariantPlaceholders")
        Truth.assertThat(tasksNamesModel.generateResValuesName).isEqualTo("generateTheVariantResValues")
        Truth.assertThat(tasksNamesModel.mergeResourcesName).isEqualTo("mergeTheVariantResources")
    }

    @Test
    fun check_taskNames_for_empty_variant() {
        // Given:
        val variant = ""

        // When:
        val tasksNamesModel = TasksNamesModel(variant)

        // Then:
        Truth.assertThat(tasksNamesModel.gatherRawStringsName).isEqualTo("gatherRawStrings")
        Truth.assertThat(tasksNamesModel.gatherStringTemplatesName).isEqualTo("gatherStringTemplates")
        Truth.assertThat(tasksNamesModel.resolvePlaceholdersName).isEqualTo("resolvePlaceholders")
        Truth.assertThat(tasksNamesModel.generateResValuesName).isEqualTo("generateResValues")
        Truth.assertThat(tasksNamesModel.mergeResourcesName).isEqualTo("mergeResources")
    }
}