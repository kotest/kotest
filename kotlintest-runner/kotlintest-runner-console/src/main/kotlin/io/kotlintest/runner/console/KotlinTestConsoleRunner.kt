@file:Suppress("UNCHECKED_CAST")

package io.kotlintest.runner.console

import io.kotlintest.Description
import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.TestCaseFilter
import io.kotlintest.TestFilterResult

/**
 * A [TestCaseFilter] that only accepts tests which are a prefix of the given test name
 */
class PrefixTestFilter(private val test: String) : TestCaseFilter {
  override fun filter(description: Description): TestFilterResult {
    val prefix = description.tail().fullName()
    println("$test startsWith $prefix")
    return if (test.startsWith(prefix)) TestFilterResult.Include else TestFilterResult.Exclude
  }
}

class KotlinTestConsoleRunner {
  fun execute(specFQN: String, test: String) {
    val spec = (Class.forName(specFQN) as Class<Spec>).kotlin
    val listener = ConsoleTestEngineListener()
    val filter = PrefixTestFilter(test)
    val runner = io.kotlintest.runner.jvm.TestEngine(listOf(spec), listOf(filter), Project.parallelism(), listener)
    runner.execute()
  }
}