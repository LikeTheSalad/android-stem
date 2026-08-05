package com.likethesalad.stem.modules.common.helpers.resources.utils

import com.likethesalad.android.protos.Attribute
import com.likethesalad.android.protos.StringResource
import com.likethesalad.stem.testutils.named
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import org.junit.jupiter.api.Test
import org.w3c.dom.Element

/**
 * Reproduces https://github.com/LikeTheSalad/android-stem/issues/332.
 *
 * [XmlUtils] previously held one shared DocumentBuilder and one shared Transformer, neither of
 * which is thread safe. Gradle runs the tasks that reach this code directly on its own task
 * execution threads, so parallel variant tasks raced on those instances.
 */
class XmlUtilsConcurrencyTest {

    private val noOpNsProvider = object : XmlUtils.NamespaceNameProvider {
        override fun getNameFor(namespaceValue: String): String {
            throw UnsupportedOperationException()
        }
    }

    /**
     * Verifies that concurrent calls do not share a DocumentBuilder. Before the regression was
     * fixed, this failed with SAXException "FWK005 parse may not be called while parsing".
     */
    @Test
    fun checkStringResourceModelToElementUnderConcurrentUse() {
        runConcurrently { threadIndex, iteration ->
            val name = "string_${threadIndex}_$iteration"
            val text = "content $threadIndex-$iteration"

            val element = XmlUtils.stringResourceModelToElement(
                StringResource.named(name, text, listOf(Attribute("extra", "extra_$threadIndex", null))),
                noOpNsProvider
            )

            check(element.textContent == text) {
                "Expected text <$text> but was <${element.textContent}>"
            }
            check(element.attributes.getNamedItem("name").textContent == name) {
                "Expected name <$name> but was <${element.attributes.getNamedItem("name").textContent}>"
            }
        }
    }

    /**
     * Verifies that concurrent calls do not share a Transformer. [XmlUtils.getContents] swallows
     * TransformerException and falls back to Node.getTextContent(), which drops inline markup, so
     * the original race corrupted string resources silently rather than failing the build. The
     * inline `<b>` tag below makes that corruption observable.
     */
    @Test
    fun checkGetContentsUnderConcurrentUse() {
        // Given: one element per thread, built up front so that only getContents runs concurrently.
        val expectedTexts = (0 until THREAD_COUNT).map { "content $it <b>bold $it</b>" }
        val elements: List<Element> = expectedTexts.mapIndexed { index, text ->
            XmlUtils.stringResourceModelToElement(
                StringResource.named("string_$index", text, emptyList()),
                noOpNsProvider
            )
        }

        runConcurrently { threadIndex, _ ->
            val expected = expectedTexts[threadIndex]
            val contents = XmlUtils.getContents(elements[threadIndex])

            check(contents == expected) {
                "Expected contents <$expected> but was <$contents>"
            }
        }
    }

    private fun runConcurrently(body: (threadIndex: Int, iteration: Int) -> Unit) {
        val executor = Executors.newFixedThreadPool(THREAD_COUNT)
        val barrier = CyclicBarrier(THREAD_COUNT)
        val failureCount = AtomicInteger()
        val firstFailure = AtomicReference<Exception>()

        try {
            val futures = (0 until THREAD_COUNT).map { threadIndex ->
                executor.submit {
                    for (iteration in 0 until ITERATIONS) {
                        barrier.await()
                        try {
                            body(threadIndex, iteration)
                        } catch (e: Exception) {
                            failureCount.incrementAndGet()
                            firstFailure.compareAndSet(null, e)
                        }
                    }
                }
            }
            futures.forEach { it.get(TIMEOUT_SECONDS, TimeUnit.SECONDS) }
        } finally {
            executor.shutdownNow()
        }

        firstFailure.get()?.let { cause ->
            throw AssertionError("${failureCount.get()} concurrent operations failed", cause)
        }
    }

    companion object {
        private const val THREAD_COUNT = 8
        private const val ITERATIONS = 500
        private const val TIMEOUT_SECONDS = 60L
    }
}
