package io.kotest.core.spec.style

import io.kotest.core.*
import io.kotest.core.factory.TestFactory
import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.spec.SpecDsl
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.deriveTestConfig
import io.kotest.extensions.TestCaseExtension

/**
 * Creates a [TestFactory] from the given block.
 *
 * The receiver of the block is a [StringSpecTestFactoryConfiguration] which allows tests
 * to be defined using the 'string-spec' style.
 */
fun stringSpec(block: StringSpecTestFactoryConfiguration.() -> Unit): TestFactory {
   val config = StringSpecTestFactoryConfiguration()
   config.block()
   return config.build()
}

/**
 * Decorates a [TestFactoryConfiguration] with the StringSpec DSL.
 */
class StringSpecTestFactoryConfiguration : TestFactoryConfiguration(), StringSpecDsl {
   override val addTest = ::addDynamicTest
}

abstract class StringSpec(body: StringSpec.() -> Unit = {}) : SpecConfiguration(), StringSpecDsl {
   override val addTest = ::addRootTestCase

   init {
      body()
   }
}

/**
 * Defines the DSL for creating tests in the 'StringSpec' style.
 *
 * Example:
 *
 * "my test" {
 *   1 + 1 shouldBe 2
 * }
 *
 */
interface StringSpecDsl : SpecDsl {

   fun String.config(
      enabled: Boolean? = null,
      tags: Set<Tag>? = null,
      extensions: List<TestCaseExtension>? = null,
      test: suspend TestContext.() -> Unit
   ) {
      val config = defaultTestCaseConfig.deriveTestConfig(enabled, tags, extensions)
      addTest(this, test, config, TestType.Test)
   }

   /**
    * Adds a String Spec test using the default test case config.
    */
   operator fun String.invoke(test: suspend TestContext.() -> Unit) =
      addTest(this, test, defaultTestCaseConfig, TestType.Test)
}
