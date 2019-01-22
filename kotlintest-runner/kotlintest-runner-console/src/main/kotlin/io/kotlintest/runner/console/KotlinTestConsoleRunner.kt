@file:Suppress("UNCHECKED_CAST")

package io.kotlintest.runner.console

import io.kotlintest.Project
import io.kotlintest.Spec
import org.slf4j.LoggerFactory

class KotlinTestConsoleRunner {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  fun execute(specFQN: String, test: String) {
    val spec = (Class.forName(specFQN) as Class<Spec>).kotlin
    val listener = ConsoleTestEngineListener()
    val runner = io.kotlintest.runner.jvm.TestEngine(listOf(spec), Project.parallelism(), listener)
    runner.execute()
  }
}