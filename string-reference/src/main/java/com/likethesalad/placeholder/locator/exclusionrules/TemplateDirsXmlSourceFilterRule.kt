package com.likethesalad.placeholder.locator.exclusionrules

import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.tools.resource.collector.android.filter.BaseAndroidXmlSourceFilterRule
import com.likethesalad.tools.resource.collector.android.source.AndroidXmlResourceSource

class TemplateDirsXmlSourceFilterRule(androidVariantContext: AndroidVariantContext) : BaseAndroidXmlSourceFilterRule() {

    private val templatesDirHandler by lazy { androidVariantContext.templatesDirHandler }
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