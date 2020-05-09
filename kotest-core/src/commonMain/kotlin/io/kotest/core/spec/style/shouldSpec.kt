package io.kotest.core.spec.style

import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.spec.style.scopes.Lifecycle
import io.kotest.core.spec.style.scopes.RootTestRegistration
import io.kotest.core.spec.style.scopes.ShouldSpecScope
import io.kotest.core.test.TestCaseConfig

/**
 * Creates a [TestFactory] from the given block.
 *
 * The receiver of the block is a [ShouldSpecTestFactoryConfiguration] which allows tests
 * to be defined using the 'should-spec' style.
 *
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
fun shouldSpec(block: ShouldSpecTestFactoryConfiguration.() -> Unit): TestFactory {
   val config = ShouldSpecTestFactoryConfiguration()
   config.block()
   return config.build()
}

class ShouldSpecTestFactoryConfiguration : TestFactoryConfiguration(), ShouldSpecScope {
   override fun lifecycle(): Lifecycle = Lifecycle.from(this)
   override fun defaultConfig(): TestCaseConfig = resolvedDefaultConfig()
   override fun registration(): RootTestRegistration = RootTestRegistration.from(this)
}

abstract class ShouldSpec(body: ShouldSpec.() -> Unit = {}) : DslDrivenSpec(), ShouldSpecScope {

   init {
      body()
   }

   override fun lifecycle(): Lifecycle = Lifecycle.from(this)
   override fun defaultConfig(): TestCaseConfig = resolvedDefaultConfig()
   override fun registration(): RootTestRegistration = RootTestRegistration.from(this)

   // need to overload this so that when doing "string" should haveLength(5) in a word spec, we don't
   // clash with the other should method
   // infix fun String.should(matcher: Matcher<String>) = TODO()
}
