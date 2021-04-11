package com.likethesalad.placeholder.modules.resolveStrings.action

import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.resolveStrings.ResolvePlaceholdersAction
import com.likethesalad.placeholder.providers.ActionProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResolvePlaceholdersActionProvider @Inject constructor() : ActionProvider<ResolvePlaceholdersAction> {

    override fun provide(androidVariantContext: AndroidVariantContext): ResolvePlaceholdersAction {
        TODO("Not yet implemented")
    }
}