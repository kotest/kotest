package io.kotest.core.spec.style

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.AfterTest
import io.kotest.core.spec.BeforeTest
import io.kotest.core.spec.SpecDsl
import io.kotest.core.test.Description
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.createTestName
import io.kotest.core.test.deriveTestConfig
import io.kotest.fp.Tuple2
import kotlin.time.Duration
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
interface ShouldSpecDsl : SpecDsl {

   @Deprecated("use context(\"parent test\")")
   operator fun String.invoke(init: suspend ShouldScope.() -> Unit) =
      addTest(this, { ShouldScope(this, this@ShouldSpecDsl).init() }, defaultConfig(), TestType.Container)

   /**
    * Adds a top level context scope to the spec.
    */
   fun context(name: String, init: suspend ContextScope.() -> Unit) {
      addTest(
         name,
         {
            ContextScope(Description.specUnsafe(this@ShouldSpecDsl).append(name), this, this@ShouldSpecDsl).init()
         },
         defaultConfig(),
         TestType.Container
      )
   }

   fun should(name: String, test: suspend TestContext.() -> Unit) =
      addTest(createTestName("should ", name), test, defaultConfig(), TestType.Test)

   fun should(name: String) = Testbuilder(this) { test, config ->
      addTest(createTestName("should ", name), test, config, TestType.Test)
   }

   class Testbuilder(
      private val specDsl: SpecDsl,
      private val register: (suspend TestContext.() -> Unit, TestCaseConfig) -> Unit
   ) {
      fun config(
         enabled: Boolean? = null,
         invocations: Int? = null,
         threads: Int? = null,
         tags: Set<Tag>? = null,
         timeout: Duration? = null,
         extensions: List<TestCaseExtension>? = null,
         enabledIf: EnabledIf? = null,
         test: suspend TestContext.() -> Unit
      ) {
         val config = specDsl.defaultConfig()
            .deriveTestConfig(enabled, tags, extensions, timeout, enabledIf, invocations, threads)
         register(test, config)
      }
   }

}


@KotestDsl
class ContextScope(
   private val description: Description,
   private val context: TestContext,
   private val spec: ShouldSpecDsl
) {

   fun beforeTest(f: BeforeTest) {
      spec.addListener(object : TestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            if (description.isAncestorOf(testCase.description)) {
               f(testCase)
            }
         }
      })
   }

   fun afterTest(f: AfterTest) {
      spec.addListener(object : TestListener {
         override suspend fun afterTest(testCase: TestCase, result: TestResult) {
            if (description.isAncestorOf(testCase.description)) {
               f(Tuple2(testCase, result))
            }
         }
      })
   }

   /**
    * Adds a nested context scope to the spec.
    */
   suspend fun context(name: String, init: suspend ContextScope.() -> Unit) {
      context.registerTestCase(
         name,
         { ContextScope(this@ContextScope.description.append(name), this, this@ContextScope.spec).init() },
         spec.defaultConfig(),
         TestType.Container
      )
   }

   inner class TestBuilder(val name: String) {
      @OptIn(ExperimentalTime::class)
      suspend fun config(
         enabled: Boolean? = null,
         tags: Set<Tag>? = null,
         timeout: Duration? = null,
         extensions: List<TestCaseExtension>? = null,
         enabledIf: EnabledIf? = null,
         test: suspend TestContext.() -> Unit
      ) {
         val config = spec.defaultConfig()
            .deriveTestConfig(enabled, tags, extensions, timeout, enabledIf)
         context.registerTestCase(name, test, config, TestType.Test)
      }
   }

   fun should(name: String) = TestBuilder(name)

   suspend fun should(name: String, test: suspend TestContext.() -> Unit) =
      context.registerTestCase(name, test, spec.defaultConfig(), TestType.Test)
}


@KotestDsl
@OptIn(ExperimentalTime::class)
class ShouldScope(val context: TestContext, private val dsl: SpecDsl) {

   @Deprecated("use context(\"parent test\") to add parent tests")
   suspend operator fun String.invoke(init: suspend ShouldScope.() -> Unit) =
      context.registerTestCase(
         this,
         { ShouldScope(this, this@ShouldScope.dsl).init() },
         dsl.defaultConfig(),
         TestType.Container
      )

   suspend fun should(name: String, test: suspend TestContext.() -> Unit) =
      context.registerTestCase(createTestName("should ", name), test, dsl.defaultConfig(), TestType.Test)

   inner class Testbuilder(val register: suspend (suspend TestContext.() -> Unit, TestCaseConfig) -> Unit) {
      suspend fun config(
         enabled: Boolean? = null,
         invocations: Int? = null,
         threads: Int? = null,
         tags: Set<Tag>? = null,
         timeout: Duration? = null,
         extensions: List<TestCaseExtension>? = null,
         enabledIf: EnabledIf? = null,
         test: suspend TestContext.() -> Unit
      ) {
         val config = dsl.defaultConfig().deriveTestConfig(enabled, tags, extensions, timeout, enabledIf, invocations, threads)
         register(test, config)
      }
   }

   suspend fun should(name: String) = Testbuilder { test, config ->
      context.registerTestCase(createTestName("should ", name), test, config, TestType.Test)
   }
}
