package com.likethesalad.placeholder.utils

import com.likethesalad.placeholder.modules.rawStrings.action.GatherRawStringsActionProvider
import com.likethesalad.placeholder.modules.resolveStrings.action.ResolvePlaceholdersActionProvider
import com.likethesalad.placeholder.modules.templateStrings.action.GatherTemplatesActionProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskActionProviderHolder @Inject constructor(
    val gatherRawStringsActionProvider: GatherRawStringsActionProvider,
    val gatherTemplatesActionProvider: GatherTemplatesActionProvider,
    val resolvePlaceholdersActionProvider: ResolvePlaceholdersActionProvider
)