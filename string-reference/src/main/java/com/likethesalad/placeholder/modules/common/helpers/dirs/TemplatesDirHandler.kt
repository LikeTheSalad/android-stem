package com.likethesalad.placeholder.modules.common.helpers.dirs

import com.likethesalad.placeholder.providers.ProjectDirsProvider
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
    private var sourceSetsCreated = false
    val templatesDirs: List<ResDir> by lazy { createTemplatesDirs() }

    private fun createTemplatesDirs(): List<ResDir> {
        return variantTree.getVariants().map {
            val dir = File(projectDir, "src/${it.name}/templates")
            ResDir(it, dir)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(variantTree: VariantTree): TemplatesDirHandler
    }

    fun configureSourceSets() {
        validateNotConfiguredAlready()
        sourceSetsCreated = true
        templatesDirs.forEach { resDir ->
            sourceSetsHandler.addToSourceSets(resDir.dir, resDir.variant.name)
        }
    }

    private fun validateNotConfiguredAlready() {
        if (sourceSetsCreated) {
            throw IllegalStateException("Res dirs have already been configured")
        }
    }
}