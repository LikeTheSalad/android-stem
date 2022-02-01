package com.likethesalad.android.templates.metadata.task.action.helpers

import com.google.common.truth.Truth
import com.likethesalad.android.templates.metadata.PluginMetadata
import org.junit.Test
import java.util.Properties

class PluginMetadataMapperTest {

    private val pluginMetadataMapper = PluginMetadataMapper()

    @Test
    fun `Convert metadata to properties`() {
        val metadata = PluginMetadata("1.0.0")

        val result = pluginMetadataMapper.convertToProperties(metadata)

        Truth.assertThat(result.getProperty("version")).isEqualTo("1.0.0")
    }

    @Test
    fun `Convert properties to metadata`() {
        val properties = Properties()
        properties.setProperty("version", "1.2.0")

        val result = pluginMetadataMapper.convertToMetadata(properties)

        Truth.assertThat(result).isEqualTo(
            PluginMetadata("1.2.0")
        )
    }
}