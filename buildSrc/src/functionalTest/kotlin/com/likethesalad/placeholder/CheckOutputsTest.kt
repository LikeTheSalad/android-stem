package com.likethesalad.placeholder

import com.likethesalad.placeholder.testutils.app.layout.AndroidAppProjectDescriptor
import com.likethesalad.placeholder.testutils.base.BaseAndroidProjectTest
import com.likethesalad.placeholder.testutils.base.layout.ProjectDescriptor
import org.gradle.testkit.runner.BuildResult
import org.junit.Test

class CheckOutputsTest : BaseAndroidProjectTest() {

    @Test
    fun `verify basic app outputs`() {
        val projectName = "app"
        val descriptor = AndroidAppProjectDescriptor(projectName)
        descriptor.projectDirectoryBuilder.register()

        createProjectAndRunStringResolver()
    }

    protected fun createProjectAndRunStringResolver(
        projectDescriptor: ProjectDescriptor,
        variantName: String
    ): BuildResult {
        return createProjectAndRun(projectDescriptor, listOf("resolve${variantName.capitalize()}Placeholders"))
    }

    override fun getAndroidBuildPluginVersion(): String = "3.3.3"
}