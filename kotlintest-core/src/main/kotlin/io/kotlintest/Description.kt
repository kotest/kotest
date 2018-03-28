package io.kotlintest

import io.kotlintest.extensions.TestCaseExtension

/**
 * The description gives the full path to a [TestScope].
 *
 * It contains the name of every parent, with the root at index 0.
 * And it includes the name of the test scope it represents.
 *
 * This is useful when you want to use a [TestCaseExtension] for tests
 * which have different tree locations but the same final name.
 */
data class Description(val parents: List<String>, val name: String) {

  fun append(name: String) =
      Description(this.parents + listOf(this.name), name)

  fun hasParent(description: Description): Boolean = parents.containsAll(description.parents + listOf(description.name))

  fun fullName(): String = (parents + listOf(name)).joinToString(" ")

  /**
   * Returns a String version of this description, which is
   * the parents + this name concatenated with slashes.
   */
  fun id(): String = (parents + listOf(name)).joinToString("/")
}