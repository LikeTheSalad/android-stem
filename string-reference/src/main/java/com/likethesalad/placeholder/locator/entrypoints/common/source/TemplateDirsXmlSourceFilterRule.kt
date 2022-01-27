package com.likethesalad.placeholder.locator.entrypoints.common.source

import com.likethesalad.placeholder.modules.common.helpers.dirs.TemplatesDirHandler
import com.likethesalad.tools.resource.collector.android.data.variant.VariantTree
import com.likethesalad.tools.resource.collector.android.filter.BaseAndroidXmlSourceFilterRule
import com.likethesalad.tools.resource.collector.android.source.AndroidXmlResourceSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TemplateDirsXmlSourceFilterRule @AssistedInject constructor(
    @Assisted variantTree: VariantTree,
    templatesDirHandlerFactory: TemplatesDirHandler.Factory
) : BaseAndroidXmlSourceFilterRule() {

    @AssistedFactory
    interface Factory {
        fun create(variantTree: VariantTree): TemplateDirsXmlSourceFilterRule
    }

    private val templatesDirHandler by lazy { templatesDirHandlerFactory.create(variantTree) }
    private val templateBaseDirRegex: Regex by lazy {
        val dirPaths = templatesDirHandler.templatesDirs.map { it.dir.path }
        Regex(dirPaths.fold("") { accumulated: String, currentPath: String ->
            val startsWithRegex = getStartsWithRegex(currentPath)
            if (accumulated.isEmpty()) startsWithRegex else "$accumulated|$startsWithRegex"
        })
    }

    private fun getStartsWithRegex(path: String): String {
        return "^${path}.+"
    }

    override fun doExclude(source: AndroidXmlResourceSource): Boolean {
        return templateBaseDirRegex.matches(source.getFileSource().path)
    }
}