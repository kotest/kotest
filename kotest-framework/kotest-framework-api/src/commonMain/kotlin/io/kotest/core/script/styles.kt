package io.kotest.core.script

import io.kotest.core.plan.Descriptor
import io.kotest.core.plan.TestName
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType

/**
 * Registers a root test, with the given name.
 */
fun test(name: String, test: suspend TestContext.() -> Unit) {
  // ScriptRuntime.registerRootTest(createTestName(name), false, TestType.Test, test)
   TODO()
}

/**
 * Registers a root test, with the given name.
 */
fun should(name: String, test: suspend TestContext.() -> Unit) {
   // ScriptRuntime.registerRootTest(createTestName(name), false, TestType.Test, test)
   TODO()
}

/**
 * Registers a root context scope, which allows further tests to be registered with test and should keywords.
 */
fun context(name: String, test: suspend ContextScope.() -> Unit) {
   TODO()
//   val testName = createTestName(name)
//   val description = ScriptSpec().description().append(testName, TestType.Container)
//   val d = description.toDescriptor(source()).append(
//      Name(testName.name),
//      DisplayName(testName.displayName),
//      TestType.Container,
//      Source.FileAndLineSource(source().fileName, source().lineNumber),
//   )
//   ScriptRuntime.registerRootTest(testName, false, TestType.Container) {
//      ContextScope(
//         description,
//         it,
//         d
//      ).test()
//   }
}

class ContextScope(
   val description: Descriptor,
   val testContext: TestContext,
) {

   suspend fun test(name: String, test: suspend TestContext.() -> Unit) {
      TODO()
//      val testName = createTestName(name)
//      registerNestedTest(
//         name = testName,
//         testContext = testContext,
//         xdisabled = false,
//         test = test,
//         config = TestCaseConfig(),
//         type = TestType.Test,
//         descriptor = descriptor.append(
//            Name(testName.name),
//            DisplayName(testName.displayName),
//            TestType.Test,
//            Source.FileAndLineSource(source().fileName, source().lineNumber),
//         ),
//      )
   }

   suspend fun should(name: String, test: suspend TestContext.() -> Unit) {
      TODO()
//      val testName = createTestName(name)
//      registerNestedTest(
//         name = testName,
//         testContext = testContext,
//         xdisabled = false,
//         test = test,
//         config = TestCaseConfig(),
//         type = TestType.Test,
//         descriptor = descriptor.append(
//            Name(testName.name),
//            DisplayName(testName.displayName),
//            TestType.Test,
//            Source.FileAndLineSource(source().fileName, source().lineNumber),
//         ),
//      )
   }
}

/**
 * Registers a root 'describe' scope, with the given name.
 */
fun describe(name: String, test: suspend DescribeScope.() -> Unit) {
   TODO()
//   val testName = createTestName(name, "Describe: ", null, false)
//   val description = ScriptSpec().description().append(testName, TestType.Container)
//   val d = description.toDescriptor(source()).append(
//      Name(testName.name),
//      DisplayName(testName.displayName),
//      TestType.Container,
//      Source.FileAndLineSource(source().fileName, source().lineNumber),
//   )
//   ScriptRuntime.registerRootTest(
//      testName,
//      false,
//      TestType.Container
//   ) { DescribeScope(description, it, d).test() }
}

class DescribeScope(
   val description: Descriptor,
   val testContext: TestContext,
) {

   /**
    * Registers a nested 'describe' scope, with the given name.
    */
   suspend fun describe(name: String, test: suspend DescribeScope.() -> Unit) {
      TODO()
//      val testName = createTestName(name, "Describe: ", null, false)
//      val d = descriptor.append(
//         Name(testName.name),
//         DisplayName(testName.displayName),
//         TestType.Container,
//         Source.FileAndLineSource(source().fileName, source().lineNumber),
//      )
//      registerNestedTest(
//         name = testName,
//         testContext = testContext,
//         xdisabled = false,
//         test = { DescribeScope(description.append(testName, TestType.Test), testContext, d).test() },
//         config = TestCaseConfig(),
//         type = TestType.Test,
//         descriptor = d,
//      )
   }

   suspend fun it(name: String, test: suspend TestContext.() -> Unit) {
      TODO()
//      val testName = createTestName(name)
//      registerNestedTest(
//         name = testName,
//         testContext = testContext,
//         xdisabled = false,
//         test = test,
//         config = TestCaseConfig(),
//         type = TestType.Test,
//         descriptor = descriptor.append(
//            Name(testName.name),
//            DisplayName(testName.displayName),
//            TestType.Test,
//            Source.FileAndLineSource(source().fileName, source().lineNumber),
//         ),
//      )
   }
}

private suspend fun registerNestedTest(
   name: TestName,
   xdisabled: Boolean,
   test: suspend TestContext.() -> Unit,
   config: TestCaseConfig,
   testContext: TestContext,
   type: TestType,
   descriptor: Descriptor,
) {
   TODO()
//   testContext.registerTestCase(
//      createNestedTest(
//         name = name,
//         xdisabled = xdisabled,
//         config = config,
//         type = type,
//         descriptor = descriptor,
//         factoryId = null,
//         test = test,
//      )
//   )
}
