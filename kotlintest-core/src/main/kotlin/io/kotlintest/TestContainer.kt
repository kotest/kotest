package io.kotlintest

import kotlin.reflect.KClass

/**
 * Used to group together [TestCase] instances
 * for heirarchical display and execution order.
 *
 * All testcases must reside in a [TestContainer].
 *
 * A container has a name, which is used when outputting
 * the hierarchical location of tests.
 *
 * It also has a reference back to the parent spec
 * so that we can generate a link to the source file
 * for any given test.
 *
 * Fianlly it captures a closure of the body of the container.
 * This is a function which is invoked with a [TestContext],
 * which can, at runtime, register further [TestScope]s with the
 * test plan.
 *
 * This function is designed so that the closures which
 * are used by the spec DSLs can be executed a later
 * stage, rather than when the class is constructed.
 *
 * This allows side effects inside a container to be
 * deferred until the test engine is ready to execute
 * tests inside that particular container.
 */
class TestContainer(val description: Description,
                    val sourceClass: KClass<out Spec>,
                    val closure: (TestContext) -> Unit) : TestScope {
  override fun name(): String = description.name
  override fun description(): Description = description
}

fun lineNumber(): Int {
  val stack = Throwable().stackTrace
  return stack.dropWhile {
    it.className.startsWith("io.kotlintest")
  }[0].lineNumber
}