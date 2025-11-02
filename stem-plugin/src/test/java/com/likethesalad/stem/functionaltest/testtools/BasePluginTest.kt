package com.likethesalad.stem.functionaltest.testtools

import com.google.common.truth.Truth
import com.likethesalad.tools.functional.testing.AndroidTestProject
import com.likethesalad.tools.functional.testing.descriptor.ProjectDescriptor
import java.io.File
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
        Truth.assertThat(result.output).contains(text)
    }
}