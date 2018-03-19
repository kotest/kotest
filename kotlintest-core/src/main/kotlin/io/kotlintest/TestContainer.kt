package io.kotlintest

/**
 * Used to group together [TestCase] instances.
 *
 * All testcases must reside in a [TestContainer].
 *
 * A container has a display name which is used
 * for reporting and display purposes.
 */
data class TestContainer(val displayName: String, val spec: Spec) {

  internal val children = mutableListOf<TestContainer>()
  internal val testcases = mutableListOf<TestCase>()

  fun childContainers(): List<TestContainer> = children.toList()
  fun testCases(): List<TestCase> = testcases.toList()

  fun addTest(tc: TestCase) {
    if (testcases.any { it.displayName == tc.displayName })
      throw RuntimeException("Cannot add two test cases with the same name: '$displayName ${tc.displayName}'")
    testcases.add(tc)
  }

  fun addContainer(container: TestContainer) {
    if (testcases.any { it.displayName == container.displayName })
      throw RuntimeException("Cannot add two test containers with the same name: '$displayName ${container.displayName}'")
    children.add(container)
  }

  // returns all test cases in this container and all child containers
  fun flatten(): List<TestCase> {
    return testcases.toList().plus(children.flatMap { it.flatten() })
  }
}