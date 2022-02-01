package com.likethesalad.android.templates.metadata.task.action.helpers

import com.likethesalad.android.templates.metadata.PluginMetadata
import java.util.Properties

class PluginMetadataMapper {

    companion object {
        private const val VERSION_PROPERTY_NAME = "version"
    }

    fun convertToProperties(metadata: PluginMetadata): Properties {
        val properties = Properties()
        properties.setProperty(VERSION_PROPERTY_NAME, metadata.version)

        return properties
    }

    fun convertToMetadata(properties: Properties): PluginMetadata {
        return PluginMetadata(properties.getProperty(VERSION_PROPERTY_NAME))
    }
}