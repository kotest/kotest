package io.kotest.core.spec.style

import io.kotest.core.spec.style.scopes.ScopeContext
import io.kotest.core.spec.style.scopes.RootTestRegistration
import io.kotest.core.spec.style.scopes.RootTestWithConfigBuilder
import io.kotest.core.spec.style.scopes.ShouldSpecContextScope
import io.kotest.core.test.Description
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import kotlin.time.ExperimentalTime

/**
 * Example:
 *
 * "some test" {
 *   "with context" {
 *      should("do something") {
 *        // test here
 *      }
 *    }
 *  }
 *
 *  or
 *
 *  should("do something") {
 *    // test here
 *  }
 */
@OptIn(ExperimentalTime::class)
interface ShouldSpecMethods {

   fun addRootTest(name: String, test: suspend TestContext.() -> Unit, config: TestCaseConfig, type: TestType)

   fun description(name: String): Description = Description.specUnsafe(this).append(name)

   fun defaultConfig(): TestCaseConfig

   fun lifecycle(): ScopeContext

   fun registration(): RootTestRegistration

   /**
    * Adds a top level context scope to the spec.
    */
   fun context(name: String, test: suspend ShouldSpecContextScope.() -> Unit) {
      addRootTest(
         name,
         { ShouldSpecContextScope(description(name), lifecycle()).test() },
         defaultConfig(),
         TestType.Container
      )
   }

   /**
    * Adds a top level test, with the given name and test function, with test config supplied
    * by invoking .config on the return of this function.
    */
   fun should(name: String) = RootTestWithConfigBuilder(name, registration())

   /**
    * Adds a top level test, with the given name and test function, with default test config.
    */
   fun should(name: String, test: suspend TestContext.() -> Unit) =
      addRootTest(name, test, defaultConfig(), TestType.Test)
}
