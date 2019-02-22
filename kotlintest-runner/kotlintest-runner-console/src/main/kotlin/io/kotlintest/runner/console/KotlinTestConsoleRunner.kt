@file:Suppress("UNCHECKED_CAST")

package io.kotlintest.runner.console

import io.kotlintest.Project
import io.kotlintest.Spec

class KotlinTestConsoleRunner {
  fun execute(specFQN: String, test: String) {
    val spec = (Class.forName(specFQN) as Class<Spec>).kotlin
    val listener = ConsoleTestEngineListener()
    val filter = SpecAwareTestFilter(test, spec)
    val runner = io.kotlintest.runner.jvm.TestEngine(listOf(spec), listOf(filter), Project.parallelism(), listener)
    runner.execute()
  }
}