package io.kotest.core.specs

import io.kotest.core.tags.Tag
import io.kotest.core.TestType
import io.kotest.core.TestCaseConfig
import io.kotest.core.TestContext
import io.kotest.extensions.TestCaseExtension

/**
 * Creates and configures a [Spec] from the given block.
 *
 * The receiver of the block is a [FunSpecBuilder] with allows tests
 * to be defined using the `fun spec` layout style.
 */
fun stringSpec(name: String? = null, block: StringSpecBuilder.() -> Unit): Spec {
   val configure: SpecBuilder.() -> Unit = {
      val b = StringSpecBuilder()
      b.block()
   }
   return createSpec(name, configure)
}

abstract class StringSpec(body: StringSpecBuilder.() -> Unit = {}) : StringSpecBuilder() {

   init {
      body()
   }
}

open class StringSpecBuilder : SpecBuilder() {

   fun String.config(
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
      addRootTestCase(this, test, config, TestType.Test)
   }

   operator fun String.invoke(test: suspend TestContext.() -> Unit) =
      addRootTestCase(this, test, defaultTestCaseConfig, TestType.Test)
}
