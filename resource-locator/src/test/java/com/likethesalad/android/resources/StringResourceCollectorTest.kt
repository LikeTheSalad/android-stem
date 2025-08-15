package com.likethesalad.android.resources

import java.io.File
import java.nio.file.Paths
import kotlin.io.path.absolutePathString
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class StringResourceCollectorTest {
    @TempDir
    lateinit var outputDir: File

    companion object {
        private val ASSETS_DIR = File(Paths.get("src", "test", "assets").absolutePathString())
    }

    @Test
    fun `Verify basic res dir`() {
        val resDirs = mutableListOf<File>()
        resDirs.add(getInputDir("basic/main/res"))

        StringResourceCollector.collectStringResources(listOf(resDirs), outputDir)

        verifyDirsContentsAreEqual(outputDir, getOutputDir("basic"))
    }

    private fun getInputDir(path: String) = File(ASSETS_DIR, "inputs/$path")

    private fun getOutputDir(path: String) = File(ASSETS_DIR, "outputs/$path")

    private fun verifyDirsContentsAreEqual(dir1: File, dir2: File) {
        val dir1Files = dir1.listFiles()?.asList() ?: emptyList()
        val dir2Files = dir2.listFiles()?.asList() ?: emptyList()
        if (dir1Files.isEmpty() && dir2Files.isEmpty()) {
            return
        }
        checkRootContentFileNames(dir1Files, dir2Files)
        dir1Files.forEach { dir1File ->
            if (dir1File.isFile) {
                checkIfFileIsInList(dir1File, dir2Files)
            } else {
                verifyDirsContentsAreEqual(dir1File, dir2Files.first { it.name == dir1File.name })
            }
        }
    }

    private fun checkRootContentFileNames(dirFiles1: List<File>, dirFiles2: List<File>) {
        val dirFileNames1 = dirFiles1.map { it.name }
        val dirFileNames2 = dirFiles2.map { it.name }
        Assertions.assertThat(dirFileNames2).containsExactlyElementsOf(dirFileNames1)
    }

    private fun checkIfFileIsInList(file: File, list: List<File>) {
        val fileWithSameName = list.first { it.name == file.name }
        Assertions.assertThat(fileWithSameName.readText()).isEqualTo(file.readText())
    }
}