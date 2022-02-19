package com.likethesalad.stem.utils

import com.likethesalad.stem.modules.resolveStrings.action.ResolvePlaceholdersActionProvider
import com.likethesalad.stem.modules.templateStrings.action.GatherTemplatesActionProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskActionProviderHolder @Inject constructor(
    val gatherTemplatesActionProvider: GatherTemplatesActionProvider,
    val resolvePlaceholdersActionProvider: ResolvePlaceholdersActionProvider
)