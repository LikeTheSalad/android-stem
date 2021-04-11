package com.likethesalad.placeholder.providers

import com.likethesalad.placeholder.PlaceholderExtension

interface PlaceholderExtensionProvider {

    fun getPlaceholderExtension(): PlaceholderExtension
}