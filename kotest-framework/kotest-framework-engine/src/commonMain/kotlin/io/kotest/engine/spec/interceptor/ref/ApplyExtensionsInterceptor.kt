package io.kotest.engine.spec.interceptor.ref

import io.kotest.common.JVMOnly
import io.kotest.common.reflection.annotation
import io.kotest.common.reflection.bestName
import io.kotest.common.reflection.instantiations
import io.kotest.core.Logger
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.extensions.ExtensionRegistry
import io.kotest.engine.flatMap
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.TestResultBuilder

/**
 * If a [Spec] is annotated with the [ApplyExtension] annotation, registers any extensions
 * returned by that annotation.
 *
 * Each extension will be wrapped so that it only executes for that spec.
 *
 * Note: [ApplyExtension] is only applied on the JVM.
 */
@JVMOnly
internal class ApplyExtensionsInterceptor(
   private val listener: TestEngineListener,
   private val registry: ExtensionRegistry
) : SpecRefInterceptor {

   private val logger = Logger(ApplyExtensionsInterceptor::class)

   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {
      return runCatching {
         val classes = ref.kclass.annotation<ApplyExtension>()?.extensions?.toList() ?: emptyList()
         classes.map { instantiations.newInstanceNoArgConstructorOrObjectInstance(it) }
      }.onFailure {
         // spec would not have been started at this point, so we need to start it so we can fail it with an error
         listener.specStarted(ref)
         listener.specFinished(ref, TestResultBuilder.builder().withError(it).build())
      }.flatMap { exts ->

         exts.forEach {
            logger.log { Pair(ref.kclass.bestName(), "Registering extension $it") }
            registry.add(it, ref.kclass)
         }

         next.invoke(ref).apply {
            exts.forEach {
               logger.log { Pair(ref.kclass.bestName(), "Removing extension $it") }
               registry.remove(it, ref.kclass)
            }
         }
      }
   }
}
