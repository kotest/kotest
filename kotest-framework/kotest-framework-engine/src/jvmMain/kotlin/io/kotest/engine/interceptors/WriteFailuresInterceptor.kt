package io.kotest.engine.interceptors

import io.kotest.common.JVMOnly
import io.kotest.core.spec.Spec
import io.kotest.engine.EngineResult
import io.kotest.engine.listener.CollectingTestEngineListener
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
internal object WriteFailuresInterceptor : EngineInterceptor {

   override suspend fun intercept(
      context: EngineContext,
      execute: NextEngineInterceptor
   ): EngineResult {
      return if (context.projectConfigResolver.writeSpecFailureFile()) {
         val collector = CollectingTestEngineListener()
         val result = execute(context.mergeListener(collector))
         val failedSpecs = collector.tests
            .filterValues { it.isErrorOrFailure }
            .map { it.key.specClass }
            .toSet()
         writeSpecFailures(failedSpecs, context.projectConfigResolver.specFailureFilePath())
         result
      } else {
         execute(context)
      }
   }

   private fun writeSpecFailures(failures: Set<KClass<out Spec>>, filename: String) {
      val path = Paths.get(filename).toAbsolutePath()
      path.parent.toFile().mkdirs()
      val content = failures.distinct().joinToString("\n") { it.java.canonicalName }
      Files.write(path, content.toByteArray())
   }
}
