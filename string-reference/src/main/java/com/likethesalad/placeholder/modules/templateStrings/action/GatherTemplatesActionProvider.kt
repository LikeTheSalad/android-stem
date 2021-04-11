package com.likethesalad.placeholder.modules.templateStrings.action

import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.templateStrings.GatherTemplatesAction
import com.likethesalad.placeholder.providers.ActionProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GatherTemplatesActionProvider @Inject constructor() : ActionProvider<GatherTemplatesAction> {

    override fun provide(androidVariantContext: AndroidVariantContext): GatherTemplatesAction {
        TODO("Not yet implemented")
    }
}