package com.likethesalad.android.templates.provider.tasks.metainf.action

import com.likethesalad.android.templates.provider.api.TemplatesProvider
import java.io.File

class ServiceMetaInfGeneratorAction(
    private val classpathDir: File,
    private val outputDir: File
) {

    companion object {
        private val REGEX_CLASS_EXTENSION = Regex("\\.class\$")
    }

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
        val pathWithoutExtension = classFile.absolutePath.replace(REGEX_CLASS_EXTENSION, "")
        return pathWithoutExtension.replace("/", ".")
    }
}