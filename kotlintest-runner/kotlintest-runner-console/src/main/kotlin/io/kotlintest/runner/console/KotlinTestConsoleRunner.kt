@file:Suppress("UNCHECKED_CAST")

package io.kotlintest.runner.console

import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.runner.jvm.TestEngineListener

class KotlinTestConsoleRunner(val writer: TestEngineListener) {
  fun execute(specFQN: String, test: String?) {
    val spec = (Class.forName(specFQN) as Class<Spec>).kotlin
    val filter = if (test == null) emptyList() else listOf(SpecAwareTestFilter(test, spec))
    val runner = io.kotlintest.runner.jvm.TestEngine(listOf(spec), filter, Project.parallelism(), writer)
    runner.execute()
  }
}