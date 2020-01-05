package io.kotest.core.specs

import io.kotest.core.*
import io.kotest.core.tags.Tag
import io.kotest.extensions.SpecLevelExtension
import io.kotest.extensions.TestListener

class FakeSpec : AbstractSpec()

/**
 * A builder that allows [Spec] instances to be created using a simple DSL when this
 * class is the receiver of a lambda configuration parameter.
 */
open class SpecBuilder {

   /**
    * Config applied to each test case if not overridden per test case.
    */
   protected open val defaultTestCaseConfig: TestCaseConfig = TestCaseConfig()

   /**
    * Contains the root tests that have been added to this builder.
    */
   private var rootTestCases = emptyList<TestCase>()

   var acceptingTopLevelRegistration = true

   var beforeTest: BeforeTest? = null
   var afterTest: AfterTest? = null
   var afterAll: AfterAll? = null
   var beforeAll: BeforeAll? = null

   var isolationMode: IsolationMode? = null
   var testCaseOrder: TestCaseOrder? = null
   var assertionMode: AssertionMode? = null

   var tags: Set<Tag> = emptySet()

   var includes = emptyList<Spec>()

   var listeners = emptyList<TestListener>()
   var extensions = emptyList<SpecLevelExtension>()

   fun testCases(): List<TestCase> = rootTestCases

   /**
    * Adds a new root [TestCase] to this builders root tests.
    */
   protected fun addRootTestCase(
      name: String,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) {
      require(rootTestCases.none { it.name == name }) { "Cannot add test with duplicate name $name" }
      require(acceptingTopLevelRegistration) { "Cannot register root test at stage; likely an error in the nesting of tests" }
      this.rootTestCases = this.rootTestCases + createTestCase(name, test, config, type)
   }

   /**
    * Registers a new before-test callback to be executed before every [TestCase].
    */
   fun beforeTest(f: BeforeTest) {
      beforeTest = beforeTest.compose(f)
   }

   /**
    * Registers a new after-test callback to be executed after every [TestCase].
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   fun afterTest(f: AfterTest) {
      afterTest = afterTest.compose(f)
   }

   fun beforeAll(f: BeforeAll) {
      beforeAll = beforeAll.compose(f)
   }

   fun afterAll(f: AfterAll) {
      afterAll = afterAll.compose(f)
   }

   fun tags(vararg tags: Tag) {
      this.tags = this.tags + tags.toSet()
   }

   fun listeners(vararg listener: TestListener) {
      this.listeners = this.listeners + listener.toList()
   }

   fun extensions(vararg extensions: SpecLevelExtension) {
      this.extensions = this.extensions + extensions.toList()
   }

   fun include(spec: Spec) {
      includes = includes + spec
   }

   fun autoclose(closeable: AutoCloseable) {
      afterAll { closeable.close() }
   }
}

private fun <R> Function0<R>?.compose(f: () -> R): () -> R = when (this) {
   null -> f
   else -> { ->
      this()
      f()
   }
}

private fun <A, R> Function1<A, R>?.compose(f: (A) -> R): (A) -> R = when (this) {
   null -> f
   else -> { a ->
      this(a)
      f(a)
   }
}

private fun <A, B, R> Function2<A, B, R>?.compose(f: (A, B) -> R): (A, B) -> R = when (this) {
   null -> f
   else -> { a, b ->
      this(a, b)
      f(a, b)
   }
}

fun createSpec(name: String?, configure: SpecBuilder.() -> Unit): Spec {
   val builder = SpecBuilder()
   builder.configure()
   val spec = Spec(
      name = name,
      configure = configure,
      tests = builder.testCases(),
      beforeTest = builder.beforeTest,
      afterTest = builder.afterTest,
      beforeAll = builder.beforeAll,
      afterAll = builder.afterAll,
      isolationMode = builder.isolationMode,
      testCaseOrder = builder.testCaseOrder,
      tags = builder.tags,
      assertionMode = builder.assertionMode,
      listeners = builder.listeners,
      extensions = builder.extensions
   )
   return builder.includes.fold(spec) { acc, op -> acc + op }
}
