package io.kotest.core.spec

import io.kotest.core.Tuple2
import io.kotest.core.config.configuration
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.reflect.KClass

/**
 * Base class for specs that allow for registration of tests via invoking [addTest].
 */
abstract class DslDrivenSpec : RegisterableSpec() {

   /**
    * Registers a callback that will execute after all tests in this spec have completed.
    * This is a convenience method for creating a [TestListener] and registering it to only
    * fire for this spec.
    */
   fun finalizeSpec(f: FinalizeSpec) {
      configuration.registerListener(object : TestListener {
         override suspend fun finalizeSpec(kclass: KClass<*>, results: Map<TestCase, TestResult>) {
            if (kclass == this@DslDrivenSpec::class) {
               f(Tuple2(kclass, results))
            }
         }
      })
   }

   /**
    * Registers a callback that will execute after all specs have completed.
    * This is a convenience method for creating a [ProjectListener] and registering it.
    */
   fun afterProject(f: AfterProject) {
      configuration.registerListener(object : ProjectListener {
         override suspend fun afterProject() {
            f()
         }
      })
   }

   @Deprecated("this makes no sense")
   fun aroundSpec(aroundSpecFn: AroundSpecFn) {
      extension(object : SpecExtension {
         override suspend fun intercept(spec: KClass<out Spec>, process: suspend () -> Unit) {
            aroundSpecFn(Tuple2(spec, process))
         }
      })
   }
}
