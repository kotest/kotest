package io.kotest.engine.spec.interceptor

import io.kotest.common.flatMap
import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.extensions.wrapper
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
internal class ApplyExtensionsInterceptor(private val registry: ExtensionRegistry) : SpecRefInterceptor {

   override suspend fun intercept(
      ref: SpecRefContainer,
      fn: suspend (SpecRefContainer) -> Result<Pair<SpecRefContainer, Map<TestCase, TestResult>>>
   ): Result<Pair<SpecRefContainer, Map<TestCase, TestResult>>> {
      return runCatching {
         ref.specRef.kclass.annotation<ApplyExtension>()?.wrapper?.map { extensionClass ->
            val extension = extensionClass.newInstanceNoArgConstructorOrObjectInstance()
            SpecWrapperExtension(extension, ref.specRef.kclass)
         } ?: emptyList()
      }.flatMap { exts ->
         exts.forEach { registry.add(it) }
         fn(ref).apply {
            exts.forEach { registry.remove(it) }
         }
      }
   }
}
