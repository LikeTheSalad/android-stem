package com.likethesalad.tools.android.descriptor

import com.likethesalad.tools.android.blocks.AndroidBlockItem
import com.likethesalad.tools.blocks.GradleBlockItem
import com.likethesalad.tools.blocks.impl.plugins.GradlePluginDeclaration
import com.likethesalad.tools.descriptor.ProjectDescriptor
import java.io.File

open class AndroidProjectDescriptor(
    name: String,
    inputDir: File,
    pluginId: String,
    private val blockItems: List<GradleBlockItem> = emptyList(),
    private val namespace: String = "some.localpackage.localname.forlocal.$name",
    private val compileSdkVersion: Int = 28
) : ProjectDescriptor(name, inputDir) {

    private val androidBlockItems by lazy { blockItems.filterIsInstance<AndroidBlockItem>() }
    private val gradleBlockItems by lazy { blockItems.filter { it !in androidBlockItems } }

    init {
        pluginsBlock.addPlugin(GradlePluginDeclaration(pluginId))
    }

    override fun getBuildGradleContents(): String {
        return """
            ${pluginsBlock.getItemText()}
            
            android {
                namespace = "$namespace"
                compileSdk $compileSdkVersion
                
                ${placeBlockItems(androidBlockItems)}
            }
            
            ${placeBlockItems(gradleBlockItems)}
            
            ${dependenciesBlock.getItemText()}
        """.trimIndent()
    }
}