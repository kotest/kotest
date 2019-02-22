@file:Suppress("UNCHECKED_CAST")

package io.kotlintest.runner.console

import io.kotlintest.Description
import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestCaseFilter
import io.kotlintest.TestFilterResult

/**
 * A [TestCaseFilter] that acccepts tests based on nested groups.
 * Eg, "test parent / test" accepts both the "test parent" and "test" [TestCase] instances.
 */
class NestedTestFilter(test: String) : TestCaseFilter {

  private val groups = test.split(" / ")

  override fun filter(description: Description): TestFilterResult {
    val include = description.tail().names().withIndex().all { (index, value) ->
      index < groups.size && groups[index] == value
    }
    return if (include) TestFilterResult.Include else TestFilterResult.Exclude
  }
}

class KotlinTestConsoleRunner {
  fun execute(specFQN: String, test: String) {
    val spec = (Class.forName(specFQN) as Class<Spec>).kotlin
    val listener = ConsoleTestEngineListener()
    val filter = NestedTestFilter(test)
    val runner = io.kotlintest.runner.jvm.TestEngine(listOf(spec), listOf(filter), Project.parallelism(), listener)
    runner.execute()
  }
}