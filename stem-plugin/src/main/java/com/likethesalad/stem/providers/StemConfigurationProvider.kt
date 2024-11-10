package com.likethesalad.stem.providers

import com.likethesalad.android.templates.common.configuration.StemConfiguration

interface StemConfigurationProvider {
    fun getStemConfiguration(): StemConfiguration
}