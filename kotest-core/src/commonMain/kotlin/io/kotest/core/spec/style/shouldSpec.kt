package io.kotest.core.spec.style

import io.kotest.core.config.Project
import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
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

class ShouldSpecTestFactoryConfiguration : TestFactoryConfiguration(), ShouldSpecDsl {
   override fun defaultConfig(): TestCaseConfig = defaultTestConfig ?: Project.testCaseConfig()
   override val addTest = ::addDynamicTest
}

abstract class ShouldSpec(body: ShouldSpec.() -> Unit = {}) : DslDrivenSpec(), ShouldSpecDsl {
   override fun defaultConfig(): TestCaseConfig =
      defaultTestConfig ?: defaultTestCaseConfig() ?: Project.testCaseConfig()

   override val addTest = ::addRootTestCase

   init {
      body()
   }

   // need to overload this so that when doing "string" should haveLength(5) in a word spec, we don't
   // clash with the other should method
   // infix fun String.should(matcher: Matcher<String>) = TODO()
}
