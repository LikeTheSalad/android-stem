package com.likethesalad.placeholder.locator.entrypoints.templates.source

import com.likethesalad.placeholder.modules.common.helpers.dirs.TemplatesDirHandler
import com.likethesalad.tools.resource.collector.android.data.resdir.ResDir
import com.likethesalad.tools.resource.collector.android.data.variant.VariantTree
import com.likethesalad.tools.resource.locator.android.extension.configuration.source.base.ResDirResourceSourceConfiguration
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class TemplatesSourceConfiguration @AssistedInject constructor(
    @Assisted variantTree: VariantTree,
    @Assisted private val templatesDirHandler: TemplatesDirHandler
) : ResDirResourceSourceConfiguration(variantTree) {

    @AssistedFactory
    interface Factory {
        fun create(variantTree: VariantTree, templatesDirHandler: TemplatesDirHandler): TemplatesSourceConfiguration
    }

    override fun getSourceFiles(): Iterable<File> {
        return templatesDirHandler.templatesDirs.map { it.dir }
    }

    override fun getResDirs(): List<ResDir> {
        return templatesDirHandler.templatesDirs
    }
}