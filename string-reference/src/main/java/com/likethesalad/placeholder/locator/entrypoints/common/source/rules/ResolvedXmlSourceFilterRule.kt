package com.likethesalad.placeholder.locator.entrypoints.common.source.rules

import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantBuildResolvedDir
import com.likethesalad.placeholder.providers.ProjectDirsProvider
import com.likethesalad.tools.resource.collector.android.filter.BaseAndroidXmlSourceFilterRule
import com.likethesalad.tools.resource.collector.android.source.AndroidXmlResourceSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResolvedXmlSourceFilterRule @Inject constructor(
    projectDirsProvider: ProjectDirsProvider
) : BaseAndroidXmlSourceFilterRule() {

    private val resolvedDirPath by lazy {
        "${projectDirsProvider.getBuildDir()}/${VariantBuildResolvedDir.RESOLVED_DIR_BUILD_RELATIVE_PATH}"
    }

    override fun doExclude(source: AndroidXmlResourceSource): Boolean {
        return source.getFileSource().absolutePath.startsWith(resolvedDirPath)
    }
}