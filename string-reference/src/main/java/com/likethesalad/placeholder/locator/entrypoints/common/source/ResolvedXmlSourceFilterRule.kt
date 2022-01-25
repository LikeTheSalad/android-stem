package com.likethesalad.placeholder.locator.entrypoints.common.source

import com.likethesalad.tools.resource.collector.android.filter.BaseAndroidXmlSourceFilterRule
import com.likethesalad.tools.resource.collector.android.source.AndroidXmlResourceSource

class ResolvedXmlSourceFilterRule(private val resolvedDirPath: String) : BaseAndroidXmlSourceFilterRule() {

    override fun doExclude(source: AndroidXmlResourceSource): Boolean {
        return source.getFileSource().absolutePath.startsWith(resolvedDirPath)
    }
}