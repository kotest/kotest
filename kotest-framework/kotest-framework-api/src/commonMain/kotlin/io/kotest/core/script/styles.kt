package io.kotest.core.script

import io.kotest.core.test.Description
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createTestName

/**
 * Registers a root test, with the given name.
 */
fun test(name: String, test: suspend TestContext.() -> Unit) {
   ScriptRuntime.registerRootTest(createTestName(name), false, TestType.Test, test)
}

/**
 * Registers a root context scope, which allows further tests to be registered with test and should keywords.
 */
fun context(name: String, test: suspend ContextScope.() -> Unit) {
   val testName = createTestName(name)
   val description = ScriptSpec().description().append(testName, TestType.Container)
   ScriptRuntime.registerRootTest(testName, false, TestType.Container) { ContextScope(description, it).test() }
}

class ContextScope(
   val description: Description,
   val testContext: TestContext,
) {

   suspend fun test(name: String, test: suspend TestContext.() -> Unit) {
      val testName = createTestName(name)
      registerNestedTest(
         name = testName,
         testContext = testContext,
         xdisabled = false,
         test = test,
         config = TestCaseConfig(),
         type = TestType.Test
      )
   }

   suspend fun should(name: String, test: suspend TestContext.() -> Unit) {
      val testName = createTestName(name)
      registerNestedTest(
         name = testName,
         testContext = testContext,
         xdisabled = false,
         test = test,
         config = TestCaseConfig(),
         type = TestType.Test
      )
   }
}

/**
 * Registers a root 'describe' scope, with the given name.
 */
fun describe(name: String, test: suspend DescribeScope.() -> Unit) {
   val testName = createTestName("Describe: ", name, false)
   val description = ScriptSpec().description().append(testName, TestType.Container)
   ScriptRuntime.registerRootTest(
      testName,
      false,
      TestType.Container
   ) { DescribeScope(description, it).test() }
}

class DescribeScope(
   val description: Description,
   val testContext: TestContext,
) {

   /**
    * Registers a nested 'describe' scope, with the given name.
    */
   suspend fun describe(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = createTestName("Describe: ", name, false)
      registerNestedTest(
         name = testName,
         testContext = testContext,
         xdisabled = false,
         test = { DescribeScope(description.append(testName, TestType.Test), testContext).test() },
         config = TestCaseConfig(),
         type = TestType.Test
      )
   }

   suspend fun it(name: String, test: suspend TestContext.() -> Unit) {
      val testName = createTestName(name)
      registerNestedTest(
         name = testName,
         testContext = testContext,
         xdisabled = false,
         test = test,
         config = TestCaseConfig(),
         type = TestType.Test
      )
   }
}

private suspend fun registerNestedTest(
   name: DescriptionName.TestName,
   xdisabled: Boolean,
   test: suspend TestContext.() -> Unit,
   config: TestCaseConfig,
   testContext: TestContext,
   type: TestType
) {
   val activeConfig = if (xdisabled) config.copy(enabled = false) else config
   testContext.registerTestCase(name, test, activeConfig, type)
}
