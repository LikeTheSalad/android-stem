package com.likethesalad.placeholder.providers

import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext

interface ActionProvider<T> {

    fun provide(androidVariantContext: AndroidVariantContext): T
}