package com.likethesalad.placeholder.modules.common.helpers.dirs

import com.likethesalad.placeholder.providers.ProjectDirsProvider
import com.likethesalad.tools.resource.api.android.environment.Variant
import com.likethesalad.tools.resource.collector.android.data.resdir.ResDir
import com.likethesalad.tools.resource.collector.android.data.variant.VariantTree
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class TemplatesDirHandler @AssistedInject constructor(
    @Assisted private val variantTree: VariantTree,
    private val sourceSetsHandler: SourceSetsHandler,
    private val projectDirsProvider: ProjectDirsProvider
) {

    private val projectDir: File by lazy { projectDirsProvider.getProjectDir() }
    private var templatesDirs: MutableList<ResDir>? = null

    @AssistedFactory
    interface Factory {
        fun create(variantTree: VariantTree): TemplatesDirHandler
    }

    fun createResDirs() {
        validateNotCreatedAlready()
        templatesDirs = mutableListOf()
        val variants = variantTree.getVariants()
        variants.take(variants.size - 1).forEach { variant ->
            createTemplatesResDir(variant)
        }
    }

    private fun validateNotCreatedAlready() {
        if (templatesDirs != null) {
            throw IllegalStateException("Res dirs have already been created")
        }
    }

    private fun createTemplatesResDir(variant: Variant) {
        val variantName = variant.name
        val dir = File(projectDir, "src/$variantName/templates")
        templatesDirs?.add(ResDir(variant, dir))
        sourceSetsHandler.addToSourceSets(dir, variantName)
    }

    fun getTemplatesDirs(): List<ResDir> {
        return templatesDirs!!
    }
}