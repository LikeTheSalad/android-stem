package com.likethesalad.stem.modules.templateStrings.action

import com.likethesalad.stem.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.stem.modules.templateStrings.GatherTemplatesAction
import com.likethesalad.stem.providers.ActionProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GatherTemplatesActionProvider @Inject
constructor(private val gatherTemplatesActionFactory: GatherTemplatesAction.Factory) :
    ActionProvider<GatherTemplatesAction> {

    override fun provide(androidVariantContext: AndroidVariantContext): GatherTemplatesAction {
        return gatherTemplatesActionFactory.create(androidVariantContext)
    }
}