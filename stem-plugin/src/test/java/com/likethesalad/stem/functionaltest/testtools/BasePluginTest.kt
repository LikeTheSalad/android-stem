package com.likethesalad.stem.functionaltest.testtools

import com.google.common.truth.Truth
import com.likethesalad.tools.functional.testing.AndroidTestProject
import com.likethesalad.tools.functional.testing.descriptor.ProjectDescriptor
import org.gradle.testkit.runner.BuildResult
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File

open class BasePluginTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    protected fun getTempFile(vararg paths: String): File {
        return if (paths.isEmpty()) {
            temporaryFolder.root
        } else {
            File(temporaryFolder.root, paths.joinToString("/"))
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