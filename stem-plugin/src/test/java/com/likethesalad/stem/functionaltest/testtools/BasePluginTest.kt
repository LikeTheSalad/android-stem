package com.likethesalad.stem.functionaltest.testtools

import com.likethesalad.tools.AndroidTestProject
import com.likethesalad.tools.descriptor.ProjectDescriptor
import java.io.File
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.BuildResult
import org.junit.jupiter.api.io.TempDir

open class BasePluginTest {

    @TempDir
    lateinit var temporaryFolder: File

    protected fun getTempFile(vararg paths: String): File {
        return if (paths.isEmpty()) {
            temporaryFolder
        } else {
            File(temporaryFolder, paths.joinToString("/"))
        }
    }

    protected fun createProject(vararg descriptors: ProjectDescriptor): AndroidTestProject {
        val project = AndroidTestProject(getTempFile())
        descriptors.forEach {
            project.addSubproject(it)
        }
        return project
    }

    protected fun verifyResultContainsText(result: BuildResult, text: String) {
        assertThat(result.output).contains(text)
    }
}