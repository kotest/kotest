package io.kotest.core.specs

import io.kotest.core.tags.Tag
import io.kotest.core.TestType
import io.kotest.core.TestCaseConfig
import io.kotest.core.TestContext
import io.kotest.extensions.TestCaseExtension

/**
 * Creates a [Spec] from the given block.
 *
 * The receiver of the block is a [FunSpecBuilder] with allows tests
 * to be defined using the `fun spec` layout style.
 */
fun funSpec(name: String? = null, block: FunSpecBuilder.() -> Unit): Spec {
   val configure: SpecBuilder.() -> Unit = {
      val b = FunSpecBuilder()
      b.block()
   }
   return createSpec(name, configure)
}

abstract class FunSpec(body: FunSpecBuilder.() -> Unit = {}) : FunSpecBuilder() {

   init {
      body()
   }
}

open class FunSpecBuilder : SpecBuilder() {

   inner class TestBuilder(val name: String) {
      fun config(
          enabled: Boolean? = null,
          tags: Set<Tag>? = null,
          extensions: List<TestCaseExtension>? = null,
          test: suspend TestContext.() -> Unit
      ) {
         val config = TestCaseConfig(
            enabled = enabled ?: defaultTestCaseConfig.enabled,
            tags = tags ?: defaultTestCaseConfig.tags,
            extensions = extensions ?: defaultTestCaseConfig.extensions
         )
         addRootTestCase(name, test, config, TestType.Test)
      }
   }

   fun context(name: String, init: suspend ContextScope.() -> Unit) {
      addRootTestCase(name, { ContextScope(this).init() }, defaultTestCaseConfig, TestType.Container)
   }

   @KotestDsl
   inner class ContextScope(val context: TestContext) {

      suspend fun context(name: String, init: suspend ContextScope.() -> Unit) {
         context.registerTestCase(
            name,
            FakeSpec(),
            { ContextScope(this).init() },
            defaultTestCaseConfig,
            TestType.Container
         )
      }

      inner class TestBuilder(val name: String) {
         suspend fun config(
             enabled: Boolean? = null,
             tags: Set<Tag>? = null,
             extensions: List<TestCaseExtension>? = null,
             test: suspend TestContext.() -> Unit
         ) {
            val config = TestCaseConfig(
               enabled = enabled ?: defaultTestCaseConfig.enabled,
               tags = tags ?: defaultTestCaseConfig.tags,
               extensions = extensions ?: defaultTestCaseConfig.extensions
            )
            context.registerTestCase(name, FakeSpec(), test, config, TestType.Test)
         }
      }

      fun test(name: String) = TestBuilder(name)

      suspend fun test(name: String, test: suspend TestContext.() -> Unit) =
         context.registerTestCase(
            name,
            FakeSpec(), test, defaultTestCaseConfig, TestType.Test
         )
   }

   fun test(name: String) = TestBuilder(name)

   /**
    * Adds a new root test case, with the given name and test function, using
    * the default test case config for this builder.
    */
   fun test(name: String, test: suspend TestContext.() -> Unit) =
      addRootTestCase(name, test, defaultTestCaseConfig, TestType.Test)
}
