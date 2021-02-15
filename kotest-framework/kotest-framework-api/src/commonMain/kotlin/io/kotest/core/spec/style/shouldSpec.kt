package io.kotest.core.spec.style

import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.resolvedDefaultConfig
import io.kotest.core.spec.style.scopes.Lifecycle
import io.kotest.core.spec.style.scopes.RootTestRegistration
import io.kotest.core.spec.style.scopes.ShouldSpecRootScope
import io.kotest.core.test.TestCaseConfig
import io.kotest.matchers.Matcher
import io.kotest.matchers.should as should2

@Deprecated("Use funSpec")
/**
 * Creates a [TestFactory] from the given block.
 *
 * The receiver of the block is a [ShouldSpecTestFactoryConfiguration] which allows tests
 * to be defined using the 'should-spec' style.
 */
fun shouldSpec(block: ShouldSpecTestFactoryConfiguration.() -> Unit): TestFactory {
   val config = ShouldSpecTestFactoryConfiguration()
   config.block()
   return config.build()
}

class ShouldSpecTestFactoryConfiguration : TestFactoryConfiguration(), ShouldSpecRootScope {
   override fun lifecycle(): Lifecycle = Lifecycle.from(this)
   override fun defaultConfig(): TestCaseConfig = resolvedDefaultConfig()
   override fun registration(): RootTestRegistration = RootTestRegistration.from(this)
}

@Deprecated("Use FunSpec")
abstract class ShouldSpec(body: ShouldSpec.() -> Unit = {}) : DslDrivenSpec(), ShouldSpecRootScope {

   init {
      body()
   }

   override fun lifecycle(): Lifecycle = Lifecycle.from(this)
   override fun defaultConfig(): TestCaseConfig = resolvedDefaultConfig()
   override fun registration(): RootTestRegistration = RootTestRegistration.from(this)

   // need to overload this so that when doing "string" should <matcher> in a should spec, we don't
   // clash with the other should method
   infix fun String.should(matcher: Matcher<String>) = this should2 matcher
}
