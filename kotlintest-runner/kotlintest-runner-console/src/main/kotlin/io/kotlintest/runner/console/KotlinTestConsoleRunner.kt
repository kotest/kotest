@file:Suppress("UNCHECKED_CAST")

package io.kotlintest.runner.console

import io.kotlintest.Description
import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.TestCaseFilter
import io.kotlintest.TestFilterResult

class PrefixTestFilter(private val test: String) : TestCaseFilter {
  override fun filter(description: Description): TestFilterResult {
    return if (test.startsWith(description.tail().fullName())) TestFilterResult.Include else TestFilterResult.Exclude
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