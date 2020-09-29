package com.likethesalad.placeholder.providers

import com.likethesalad.placeholder.models.PlaceholderExtension

interface PlaceholderExtensionProvider {

    fun getPlaceholderExtension(): PlaceholderExtension
}