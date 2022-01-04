package com.likethesalad.placeholder.modules.templateStrings.data

import com.likethesalad.placeholder.modules.templateStrings.GatherTemplatesAction
import com.likethesalad.placeholder.providers.LanguageResourceFinderProvider

data class GatherTemplatesArgs(
    val gatherTemplatesAction: GatherTemplatesAction,
    val languageResourceFinderProvider: LanguageResourceFinderProvider
)