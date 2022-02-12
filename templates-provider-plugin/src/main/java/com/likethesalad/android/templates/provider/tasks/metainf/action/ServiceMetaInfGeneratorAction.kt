package com.likethesalad.android.templates.provider.tasks.metainf.action

import com.likethesalad.android.templates.common.utils.CommonConstants
import com.likethesalad.android.templates.provider.api.TemplatesProvider
import java.io.File

class ServiceMetaInfGeneratorAction(
    private val classpathDir: File,
    private val outputDir: File
) {

    fun execute() {
        val classPaths = classpathDir.walk().filter { it.isFile }.toList()
        val classFullNames = convertToClassNames(classPaths)
        val outputFile = getOutputFile()

        outputFile.writeText(classFullNames.joinToString("\n"))
    }

    private fun getOutputFile(): File {
        val file = File(outputDir, "META-INF/services/${TemplatesProvider::class.java.name}")
        val parent = file.parentFile

        if (!parent.exists()) {
            parent.mkdirs()
        }

        return file
    }

    private fun convertToClassNames(classPaths: List<File>): List<String> {
        return classPaths.map {
            convertToClassFullName(it)
        }
    }

    private fun convertToClassFullName(classFile: File): String {
        return "${CommonConstants.PROVIDER_PACKAGE_NAME}.${classFile.nameWithoutExtension}"
    }
}