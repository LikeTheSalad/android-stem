package com.likethesalad.placeholder.modules.templateStrings.data

import com.likethesalad.placeholder.modules.templateStrings.GatherTemplatesAction
import com.likethesalad.tools.resource.locator.android.extension.configuration.data.ResourcesProvider
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

data class GatherTemplatesArgs(
    val gatherTemplatesAction: GatherTemplatesAction,
    val commonResourcesProvider: ResourcesProvider,
    val templateIdsSource: Provider<RegularFile>
)