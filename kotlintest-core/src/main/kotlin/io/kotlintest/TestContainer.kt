package io.kotlintest

/**
 * Used to group together [TestCase] instances
 * as well as supporting lazily discovered
 * nested containers.
 *
 * All testcases must reside in a [TestContainer].
 *
 * A container has a name, which is used when outputting
 * the hierarchical location of tests. It also has a
 * reference back to the parent spec.
 *
 * Most importantly, it has a body function. This
 * function, when invoked, returns any nested containers
 * and any tests directly registered in this container.
 *
 * This function is designed so that the closures which
 * are used by the spec DSLs can be executed a later
 * stage, rather than when the class is constructed.
 *
 * This allows side effects inside a container to be
 * deferred until the test engine is ready
 * to execute tests inside that particular container.
 */
class TestContainer(val displayName: String, val spec: Spec, val body: () -> Pair<List<TestContainer>, List<TestCase>>)

/**
 * A container used by the spec DSL to allow registering
 * of [TestCase] and [TestContainer] instances.
 *
 * This class should be extended and used as the receiver
 * type for context creation functions in the DSL.
 */
open class TestScope {

  internal val containers = mutableListOf<TestContainer>()
  internal val testcases = mutableListOf<TestCase>()

  fun addTest(tc: TestCase) {
    if (testcases.any { it.displayName == tc.displayName })
      throw RuntimeException("Cannot add two tests with the same name inside the same scope: '${tc.displayName}'")
    testcases.add(tc)
  }

  fun addTest(name: String, spec: Spec, test: () -> Unit, config: TestCaseConfig): TestCase {
    val tc = TestCase(name, spec, test, config)
    addTest(tc)
    return tc
  }

  fun addContainer(container: TestContainer) {
    if (containers.any { it.displayName == container.displayName })
      throw RuntimeException("Cannot add two tests with the same name inside the same scope: '${container.displayName}'")
    containers.add(container)
  }

  fun <T : TestScope> addContainer(name: String, spec: Spec, scopeFn: () -> T, init: T.() -> Unit) {
    val container = TestContainer(name, spec, {
      val scope = scopeFn()
      scope.init()
      scope.toResult()
    })
    addContainer(container)
  }

  fun toResult() = containers.toList() to testcases.toList()
}