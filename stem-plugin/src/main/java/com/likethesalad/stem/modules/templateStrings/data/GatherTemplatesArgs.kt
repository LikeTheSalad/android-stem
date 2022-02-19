package com.likethesalad.stem.modules.templateStrings.data

import com.likethesalad.stem.modules.templateStrings.GatherTemplatesAction
import com.likethesalad.tools.resource.locator.android.extension.configuration.data.ResourcesProvider

data class GatherTemplatesArgs(
    val gatherTemplatesAction: GatherTemplatesAction,
    val commonResourcesProvider: ResourcesProvider
)