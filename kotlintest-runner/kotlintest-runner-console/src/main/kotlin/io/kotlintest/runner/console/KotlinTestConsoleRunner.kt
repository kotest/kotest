@file:Suppress("UNCHECKED_CAST")

package io.kotlintest.runner.console

import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.runner.jvm.TestEngineListener

class KotlinTestConsoleRunner(private val writer: TestEngineListener) {
  fun execute(specFQN: String?, test: String?) {

    val spec = if (specFQN != null) (Class.forName(specFQN) as Class<Spec>).kotlin else null
    val filter = if (test != null && spec != null) SpecAwareTestFilter(test, spec) else null

    val runner = io.kotlintest.runner.jvm.TestEngine(
        if (spec == null) emptyList() else listOf(spec),
        if (filter == null) emptyList() else listOf(filter),
        Project.parallelism(),
        writer
    )
    runner.execute()
  }
}