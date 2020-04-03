@file:Suppress("FunctionName", "unused")

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
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.createTestName
import io.kotest.core.test.deriveTestConfig
import io.kotest.fp.Tuple2
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@Suppress("FunctionName")
@OptIn(ExperimentalTime::class)
interface BehaviorSpecDsl : SpecDsl {

   fun Given(name: String, test: suspend GivenContext.() -> Unit) = addGivenContext(name, test)
   fun given(name: String, test: suspend GivenContext.() -> Unit) = addGivenContext(name, test)

   private fun addGivenContext(name: String, test: suspend GivenContext.() -> Unit) {
      val testName = createTestName("Given: ", name)
      addTest(
         testName,
         { GivenContext(Description.specUnsafe(this@BehaviorSpecDsl).append(testName), this, this@BehaviorSpecDsl).test() },
         defaultConfig(),
         TestType.Container
      )
   }

   @KotestDsl
   class TestScope(val name: String, val context: TestContext, val dsl: BehaviorSpecDsl) {
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
         context.registerTestCase(name, { test.invoke(this) }, config, TestType.Test)
      }
   }

}

@KotestDsl
class ThenContext(val context: TestContext)

@KotestDsl
class GivenContext(
   private val description: Description,
   private val context: TestContext,
   private val spec: BehaviorSpecDsl
) {

   fun beforeTest(f: BeforeTest) {
      spec.addListener(object : TestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            if (description.isParentOf(testCase.description)) f(testCase)
         }
      })
   }

   fun afterTest(f: AfterTest) {
      spec.addListener(object : TestListener {
         override suspend fun afterTest(testCase: TestCase, result: TestResult) {
            if (description.isParentOf(testCase.description)) f(Tuple2(testCase, result))
         }
      })
   }

   suspend fun And(name: String, test: suspend GivenAndContext.() -> Unit) = addAndContext(name, test)
   suspend fun and(name: String, test: suspend GivenAndContext.() -> Unit) = addAndContext(name, test)

   private suspend fun addAndContext(name: String, test: suspend GivenAndContext.() -> Unit) {
      val testName = createTestName("And: ", name)
      context.registerTestCase(
         testName,
         { GivenAndContext(this@GivenContext.description, this, this@GivenContext.spec).test() },
         spec.defaultConfig(),
         TestType.Container
      )
   }

   suspend fun When(name: String, test: suspend WhenContext.() -> Unit) = addWhenContext(name, test)
   suspend fun `when`(name: String, test: suspend WhenContext.() -> Unit) = addWhenContext(name, test)

   private suspend fun addWhenContext(name: String, test: suspend WhenContext.() -> Unit) {
      val testName = createTestName("When: ", name)
      context.registerTestCase(
         testName,
         { WhenContext(this@GivenContext.description.append(testName), this, this@GivenContext.spec).test() },
         spec.defaultConfig(),
         TestType.Container
      )
   }

   suspend fun Then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)
   suspend fun then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)

   private suspend fun addThenContext(name: String, test: suspend ThenContext.() -> Unit) {
      context.registerTestCase(
         createTestName("Then: ", name),
         { ThenContext(this).test() },
         spec.defaultConfig(),
         TestType.Container
      )
   }

   fun then(name: String) = BehaviorSpecDsl.TestScope(name, context, spec)
}

@KotestDsl
class GivenAndContext(
   private val description: Description,
   private val context: TestContext,
   private val spec: BehaviorSpecDsl
) {

   fun beforeTest(f: BeforeTest) {
      spec.addListener(object : TestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            if (description.isParentOf(testCase.description)) f(testCase)
         }
      })
   }

   fun afterTest(f: AfterTest) {
      spec.addListener(object : TestListener {
         override suspend fun afterTest(testCase: TestCase, result: TestResult) {
            if (description.isParentOf(testCase.description)) f(Tuple2(testCase, result))
         }
      })
   }

   suspend fun And(name: String, test: suspend GivenAndContext.() -> Unit) = addAndContext(name, test)
   suspend fun and(name: String, test: suspend GivenAndContext.() -> Unit) = addAndContext(name, test)

   private suspend fun addAndContext(name: String, test: suspend GivenAndContext.() -> Unit) {
      val testName = createTestName("And: ", name)
      context.registerTestCase(
         testName,
         { GivenAndContext(this@GivenAndContext.description.append(testName), this, this@GivenAndContext.spec).test() },
         spec.defaultConfig(),
         TestType.Container
      )
   }

   suspend fun When(name: String, test: suspend WhenContext.() -> Unit) = addWhenContext(name, test)
   suspend fun `when`(name: String, test: suspend WhenContext.() -> Unit) = addWhenContext(name, test)

   private suspend fun addWhenContext(name: String, test: suspend WhenContext.() -> Unit) {
      val testName = createTestName("When: ", name)
      context.registerTestCase(
         testName,
         { WhenContext(this@GivenAndContext.description.append(testName), this, this@GivenAndContext.spec).test() },
         spec.defaultConfig(),
         TestType.Container
      )
   }

   suspend fun Then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)
   suspend fun then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)

   private suspend fun addThenContext(name: String, test: suspend ThenContext.() -> Unit) {
      context.registerTestCase(
         createTestName("Then: ", name),
         { ThenContext(this).test() },
         spec.defaultConfig(),
         TestType.Container
      )
   }

   fun then(name: String) = BehaviorSpecDsl.TestScope(name, context, spec)
}


@KotestDsl
class WhenContext(
   private val description: Description,
   private val context: TestContext,
   private val spec: BehaviorSpecDsl
) {

   fun beforeTest(f: BeforeTest) {
      spec.addListener(object : TestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            if (description.isParentOf(testCase.description)) f(testCase)
         }
      })
   }

   fun afterTest(f: AfterTest) {
      spec.addListener(object : TestListener {
         override suspend fun afterTest(testCase: TestCase, result: TestResult) {
            if (description.isParentOf(testCase.description)) f(Tuple2(testCase, result))
         }
      })
   }

   suspend fun And(name: String, test: suspend WhenAndContext.() -> Unit) = addAndContext(name, test)
   suspend fun and(name: String, test: suspend WhenAndContext.() -> Unit) = addAndContext(name, test)

   private suspend fun addAndContext(name: String, test: suspend WhenAndContext.() -> Unit) {
      context.registerTestCase(
         createTestName("And: ", name),
         { WhenAndContext(this, this@WhenContext.spec).test() },
         spec.defaultConfig(),
         TestType.Container
      )
   }

   suspend fun Then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)
   suspend fun then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)

   private suspend fun addThenContext(name: String, test: suspend ThenContext.() -> Unit) {
      context.registerTestCase(
         createTestName("Then: ", name),
         { ThenContext(this).test() },
         spec.defaultConfig(),
         TestType.Test
      )
   }

   fun then(name: String) = BehaviorSpecDsl.TestScope(name, context, spec)
}


@KotestDsl
class WhenAndContext(val context: TestContext, private val dsl: BehaviorSpecDsl) {
   suspend fun And(name: String, test: suspend WhenAndContext.() -> Unit) = addAndContext(name, test)
   suspend fun and(name: String, test: suspend WhenAndContext.() -> Unit) = addAndContext(name, test)

   private suspend fun addAndContext(name: String, test: suspend WhenAndContext.() -> Unit) {
      context.registerTestCase(
         createTestName("And: ", name),
         { WhenAndContext(this, this@WhenAndContext.dsl).test() },
         dsl.defaultConfig(),
         TestType.Container
      )
   }

   suspend fun Then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)
   suspend fun then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)

   private suspend fun addThenContext(name: String, test: suspend ThenContext.() -> Unit) {
      context.registerTestCase(
         createTestName("Then: ", name),
         { ThenContext(this).test() },
         dsl.defaultConfig(),
         TestType.Test
      )
   }

   fun then(name: String) = BehaviorSpecDsl.TestScope(name, context, dsl)
}
