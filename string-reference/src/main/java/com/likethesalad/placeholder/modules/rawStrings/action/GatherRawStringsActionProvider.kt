package com.likethesalad.placeholder.modules.rawStrings.action

import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.rawStrings.GatherRawStringsAction
import com.likethesalad.placeholder.providers.ActionProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GatherRawStringsActionProvider @Inject constructor() : ActionProvider<GatherRawStringsAction> {

    override fun provide(androidVariantContext: AndroidVariantContext): GatherRawStringsAction {
        TODO("Not yet implemented")
    }
}