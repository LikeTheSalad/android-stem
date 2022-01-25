package com.likethesalad.placeholder.providers

import com.likethesalad.placeholder.PlaceholderExtension

interface PluginExtensionProvider {
    fun getPluginExtension(): PlaceholderExtension
}