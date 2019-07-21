@file:Suppress("UNCHECKED_CAST")

package io.kotlintest.runner.console

import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.Tag
import io.kotlintest.runner.jvm.DiscoveryRequest
import io.kotlintest.runner.jvm.TestDiscovery
import io.kotlintest.runner.jvm.TestEngineListener
import org.slf4j.LoggerFactory

class KotlinTestConsoleRunner(private val writer: TestEngineListener) {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  fun execute(specFQN: String?, test: String?, includeTags: Set<Tag>, excludeTags: Set<Tag>) {

    val (specs, filter) = if (specFQN == null) {
      val result = TestDiscovery.discover(DiscoveryRequest(emptyList()))
      result.classes to null
    } else {
      val spec = (Class.forName(specFQN) as Class<Spec>).kotlin
      val filter = test?.let { SpecAwareTestFilter(it, spec) }
      listOf(spec) to filter
    }

    val runner = io.kotlintest.runner.jvm.TestEngine(
      specs,
      if (filter == null) emptyList() else listOf(filter),
      Project.parallelism(),
      includeTags,
      excludeTags,
      writer
    )
    runner.execute()
  }
}
