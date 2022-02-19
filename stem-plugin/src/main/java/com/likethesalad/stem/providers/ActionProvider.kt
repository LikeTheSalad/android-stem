package com.likethesalad.stem.providers

import com.likethesalad.stem.modules.common.helpers.android.AndroidVariantContext

interface ActionProvider<T> {

    fun provide(androidVariantContext: AndroidVariantContext): T
}