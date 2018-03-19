package io.kotlintest

/**
 * Used to group together [TestCase] instances.
 *
 * All testcases must reside in a [TestContainer].
 *
 * A container has a display name which is used
 * for reporting and display purposes.
 */
data class TestContainer(val name: String, val spec: Spec) {

  internal val children = mutableListOf<TestContainer>()
  internal val testcases = mutableListOf<TestCase>()

  fun childContainers(): List<TestContainer> = children.toList()
  fun testCases(): List<TestCase> = testcases.toList()

  fun addTest(tc: TestCase) {
    testcases.add(tc)
  }

  fun addContainer(container: TestContainer) {
    children.add(container)
  }

  // returns all test cases in this container and all child containers
  fun flatten(): List<TestCase> {
    return testcases.toList().plus(children.flatMap { it.flatten() })
  }
}