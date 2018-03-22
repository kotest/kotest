package io.kotlintest

/**
 * Used to group together [TestCase] instances.
 *
 * All testcases must reside in a [TestScope].
 *
 * A scope has a display name which is used
 * for reporting and display purposes.
 *
 * A scope has a body function, which is
 * executed at test time to discover child
 * tests and nested scopes.
 */
data class TestScope(val displayName: String, val spec: Spec, val body: () -> Any?) {

  internal val children = mutableListOf<TestScope>()
  internal val testcases = mutableListOf<TestCase>()

  fun childContainers(): List<TestScope> = children.toList()
  fun testCases(): List<TestCase> = testcases.toList()

  fun addTest(tc: TestCase) {
    if (testcases.any { it.displayName == tc.displayName })
      throw RuntimeException("Cannot add two test cases with the same name: '$displayName ${tc.displayName}'")
    testcases.add(tc)
  }

  fun addScope(scope: TestScope) {
    if (testcases.any { it.displayName == scope.displayName })
      throw RuntimeException("Cannot add two test scopes with the same name: '$displayName ${scope.displayName}'")
    children.add(scope)
  }

  // returns all test cases in this container and all child containers
  fun flatten(): List<TestCase> {
    return testcases.toList().plus(children.flatMap { it.flatten() })
  }

  companion object {
    class EmptySpec : AbstractSpec()

    fun empty() = TestScope("", EmptySpec(), { })
  }
}