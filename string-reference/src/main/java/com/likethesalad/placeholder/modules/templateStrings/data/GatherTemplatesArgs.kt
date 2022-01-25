package com.likethesalad.placeholder.modules.templateStrings.data

import com.likethesalad.placeholder.modules.templateStrings.GatherTemplatesAction
import com.likethesalad.tools.resource.locator.android.extension.configuration.data.ResourcesProvider

data class GatherTemplatesArgs(
    val gatherTemplatesAction: GatherTemplatesAction,
    val resourcesProvider: ResourcesProvider
)