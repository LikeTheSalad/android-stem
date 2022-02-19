package com.likethesalad.stem.modules.resolveStrings.action

import com.likethesalad.stem.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.stem.modules.resolveStrings.ResolvePlaceholdersAction
import com.likethesalad.stem.providers.ActionProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResolvePlaceholdersActionProvider @Inject
constructor(private val resolvePlaceholdersActionFactory: ResolvePlaceholdersAction.Factory) :
    ActionProvider<ResolvePlaceholdersAction> {

    override fun provide(androidVariantContext: AndroidVariantContext): ResolvePlaceholdersAction {
        return resolvePlaceholdersActionFactory.create(androidVariantContext)
    }
}