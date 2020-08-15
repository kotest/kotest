package io.kotest.engine.spec

import io.kotest.core.Tuple2
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.AfterProject
import io.kotest.core.spec.FinalizeSpec
import io.kotest.core.InlineCallbacks
import io.kotest.core.InlineConfiguration
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.config.Project
import kotlin.reflect.KClass

/**
 * A test suite is an abstract container of tests.
 *
 * The concrete implementation of suites are [Spec]s and [TestFactoryConfiguration].
 */
abstract class TestSuite : InlineCallbacks, InlineConfiguration {

   /**
    * Registers a callback that will execute after all tests in this spec have completed.
    * This is a convenience method for creating a [TestListener] and registering it to only
    * fire for this spec.
    */
   fun finalizeSpec(f: FinalizeSpec) {
      Project.registerListener(object : TestListener {
         override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
            if (kclass == this@TestSuite::class) {
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
      Project.registerListener(object : ProjectListener {
         override suspend fun afterProject() {
            f()
         }
      })
   }
}
