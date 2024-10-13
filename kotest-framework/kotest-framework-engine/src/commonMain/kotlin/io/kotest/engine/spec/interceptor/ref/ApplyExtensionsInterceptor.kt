package io.kotest.engine.spec.interceptor.ref

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.extensions.wrapper
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.extensions.SpecWrapperExtension
import io.kotest.engine.flatMap
import io.kotest.engine.newInstanceNoArgConstructorOrObjectInstance
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.mpp.annotation

/**
 * If a [Spec] is annotated with the [ApplyExtension] annotation, registers any extensions
 * returned by that annotation.
 *
 * Each extension will be wrapped so that it only executes for that spec.
 *
 * Note: [ApplyExtension] is only applied on the JVM.
 */
internal class ApplyExtensionsInterceptor(private val registry: ExtensionRegistry) : SpecRefInterceptor {

   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {
      return runCatching {
         ref.kclass.annotation<ApplyExtension>()?.wrapper?.map { extensionClass ->
            val extension = extensionClass.newInstanceNoArgConstructorOrObjectInstance()
            SpecWrapperExtension(extension, ref.kclass)
         } ?: emptyList()
      }.flatMap { exts ->
         exts.forEach { registry.add(it) }
         next.invoke(ref).apply {
            exts.forEach { registry.remove(it) }
         }
      }
   }
}
