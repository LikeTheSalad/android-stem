package com.likethesalad.placeholder.providers

import com.likethesalad.placeholder.base.TaskAction
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext

interface ActionProvider<T : TaskAction> {

    fun provide(androidVariantContext: AndroidVariantContext): T
}