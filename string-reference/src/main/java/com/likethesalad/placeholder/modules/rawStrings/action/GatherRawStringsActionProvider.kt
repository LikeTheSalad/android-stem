package com.likethesalad.placeholder.modules.rawStrings.action

import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.rawStrings.GatherRawStringsAction
import com.likethesalad.placeholder.modules.rawStrings.GatherRawStringsActionFactory
import com.likethesalad.placeholder.providers.ActionProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GatherRawStringsActionProvider @Inject
constructor(private val gatherRawStringsActionFactory: GatherRawStringsActionFactory) :
    ActionProvider<GatherRawStringsAction> {

    override fun provide(androidVariantContext: AndroidVariantContext): GatherRawStringsAction {
        return gatherRawStringsActionFactory.create(androidVariantContext)
    }
}