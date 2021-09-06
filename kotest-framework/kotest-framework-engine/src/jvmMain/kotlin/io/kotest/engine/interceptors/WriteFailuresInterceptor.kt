package io.kotest.engine.interceptors

import io.kotest.common.JVMOnly
import io.kotest.core.config.configuration
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestStatus
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.CompositeTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.reflect.KClass

/**
 * Writes failed specs to a file so that the [io.kotest.engine.spec.FailureFirstSorter]
 * can use the file to run failed specs first.
 *
 * Note: This is a JVM only feature.
 */
@JVMOnly
internal class WriteFailuresInterceptor(private val filename: String) : EngineInterceptor {

   override suspend fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: suspend (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult {
      val collector = CollectingTestEngineListener()
      val comp = CompositeTestEngineListener(listOf(listener, collector))
      val result = execute(suite, comp)
      if (configuration.writeSpecFailureFile) {
         val failedSpecs = collector.tests
            .filterValues { it.status == TestStatus.Failure || it.status == TestStatus.Error }
            .map { it.key.spec::class }
            .toSet()
         writeSpecFailures(failedSpecs)
      }
      return result
   }

   private fun writeSpecFailures(failures: Set<KClass<out Spec>>) {
      val path = Paths.get(filename).toAbsolutePath()
      path.parent.toFile().mkdirs()
      val content = failures.distinct().joinToString("\n") { it.java.canonicalName }
      Files.write(path, content.toByteArray())
   }
}
