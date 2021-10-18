package io.kotest.engine.spec.interceptor

import io.kotest.core.config.Configuration
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.extensions.SpecWrapperExtension
import io.kotest.mpp.annotation
import io.kotest.mpp.newInstanceNoArgConstructorOrObjectInstance

/**
 * If the spec is annotated with the [ApplyExtension] annotation, registers any extensions
 * returned by that annotation.
 *
 * Each extension will be wrapped so that it only executes for that spec.
 *
 * Note: annotations are only available on the JVM.
 */
internal class ApplyExtensionsInterceptor(private val conf: Configuration) : SpecRefInterceptor {

   override suspend fun intercept(
      fn: suspend (SpecRef) -> Map<TestCase, TestResult>
   ): suspend (SpecRef) -> Map<TestCase, TestResult> = { ref ->

      val extensions = ref.kclass.annotation<ApplyExtension>()?.extensions?.map { extensionClass ->
         val extension = extensionClass.newInstanceNoArgConstructorOrObjectInstance()
         SpecWrapperExtension(extension, ref.kclass)
      } ?: emptyList()

      extensions.forEach { conf.register(it) }
      fn(ref).apply {
         extensions.forEach { conf.deregister(it) }
      }
   }
}
