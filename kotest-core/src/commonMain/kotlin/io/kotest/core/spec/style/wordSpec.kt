package io.kotest.core.spec.style

import io.kotest.core.config.Project
import io.kotest.core.factory.TestFactory
import io.kotest.core.spec.Spec
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.test.TestCaseConfig

/**
 * Creates a [TestFactory] from the given block.
 *
 * The receiver of the block is a [WordSpecTestFactoryConfiguration] which allows tests
 * to be defined using the 'word-spec' style.
 */
fun wordSpec(block: WordSpecTestFactoryConfiguration.() -> Unit): TestFactory {
   val config = WordSpecTestFactoryConfiguration()
   config.block()
   return config.build()
}

/**
 * Decorates a [TestFactoryConfiguration] with the WordSpec DSL.
 */
class WordSpecTestFactoryConfiguration : TestFactoryConfiguration(), WordSpecDsl {
   override fun defaultConfig(): TestCaseConfig = defaultTestConfig ?: Project.testCaseConfig()
   override val addTest = ::addDynamicTest
}

abstract class WordSpec(body: WordSpec.() -> Unit = {}) : Spec(), WordSpecDsl {
   override fun defaultConfig(): TestCaseConfig =
      defaultTestConfig ?: defaultTestCaseConfig() ?: Project.testCaseConfig()

   override val addTest = ::addRootTestCase

   init {
      body()
   }

   // need to overload this so that when doing "string" should haveLength(5) in a word spec, we don't
   // clash with the other should method
   //infix fun String?.should(matcher: Matcher<String?>) = TODO()
}
