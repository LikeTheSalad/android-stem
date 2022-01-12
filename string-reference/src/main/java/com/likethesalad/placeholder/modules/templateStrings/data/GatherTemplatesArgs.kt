package com.likethesalad.placeholder.modules.templateStrings.data

import com.likethesalad.placeholder.modules.templateStrings.GatherTemplatesAction
import com.likethesalad.placeholder.providers.LanguageResourcesHandlerProvider

data class GatherTemplatesArgs(
    val gatherTemplatesAction: GatherTemplatesAction,
    val languageResourcesHandlerProvider: LanguageResourcesHandlerProvider
)