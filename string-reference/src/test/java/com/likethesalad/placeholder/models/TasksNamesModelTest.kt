package com.likethesalad.placeholder.models

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.common.models.TasksNamesModel
import com.likethesalad.tools.android.plugin.data.AndroidVariantData
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class TasksNamesModelTest {

    @Test
    fun check_taskNames_for_variant() {
        // Given:
        val variant = "theVariant"

        // When:
        val tasksNamesModel = getTasksNamesFor(variant)

        // Then:
        Truth.assertThat(tasksNamesModel.templatesIdentifierName).isEqualTo("templatesTheVariantIdentifier")
        Truth.assertThat(tasksNamesModel.gatherStringTemplatesName).isEqualTo("gatherTheVariantStringTemplates")
        Truth.assertThat(tasksNamesModel.resolvePlaceholdersName).isEqualTo("resolveTheVariantPlaceholders")
        Truth.assertThat(tasksNamesModel.mergeResourcesName).isEqualTo("mergeTheVariantResources")
    }

    @Test
    fun check_taskNames_for_empty_variant() {
        // Given:
        val variant = ""

        // When:
        val tasksNamesModel = getTasksNamesFor(variant)

        // Then:
        Truth.assertThat(tasksNamesModel.templatesIdentifierName).isEqualTo("templatesIdentifier")
        Truth.assertThat(tasksNamesModel.gatherStringTemplatesName).isEqualTo("gatherStringTemplates")
        Truth.assertThat(tasksNamesModel.resolvePlaceholdersName).isEqualTo("resolvePlaceholders")
        Truth.assertThat(tasksNamesModel.mergeResourcesName).isEqualTo("mergeResources")
    }

    private fun getTasksNamesFor(variantName: String): TasksNamesModel {
        val androidVariantData = mockk<AndroidVariantData>()
        every { androidVariantData.getVariantName() }.returns(variantName)

        return TasksNamesModel(androidVariantData)
    }
}