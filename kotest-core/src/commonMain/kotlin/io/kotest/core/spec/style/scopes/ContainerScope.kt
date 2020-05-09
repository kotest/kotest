package io.kotest.core.spec.style.scopes

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.AfterTest
import io.kotest.core.spec.BeforeTest
import io.kotest.core.test.Description
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.fp.Tuple2

interface ContainerScope {

   /**
    * Returns the [Description] for the test that defined this scope.
    * This is needed when registering before/after callbacks since they need to know the
    * description (path) of the current scope in order to filter.
    */
   val description: Description

   /**
    * A [ScopeContext] is used by scopes to register callbacks and tests.
    */
   val context: ScopeContext

   /**
    * Registers a [BeforeTest] function that executes before every test in this scope.
    */
   fun beforeTest(f: BeforeTest) {
      context.addListener(object : TestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            if (description.isParentOf(testCase.description)) f(testCase)
         }
      })
   }

   /**
    * Registers an [AfterTest] function that executes after every test in this scope.
    */
   fun afterTest(f: AfterTest) {
      context.addListener(object : TestListener {
         override suspend fun afterTest(testCase: TestCase, result: TestResult) {
            if (description.isParentOf(testCase.description)) f(Tuple2(testCase, result))
         }
      })
   }

}
