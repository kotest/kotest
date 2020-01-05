@file:Suppress("UNCHECKED_CAST")

package io.kotest.runner.console

import io.kotest.core.tags.Tag
import io.kotest.runner.jvm.TestEngineListener
import org.slf4j.LoggerFactory

class KotestConsoleRunner(private val writer: TestEngineListener) {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  fun execute(specFQN: String?, test: String?, includeTags: Set<Tag>, excludeTags: Set<Tag>) {

//    val (specs, filter) = if (specFQN == null) {
//      val result = TestDiscovery.discover(DiscoveryRequest(emptyList()))
//      result.classes to null
//    } else {
//      val spec = (Class.forName(specFQN) as Class<SpecInterface>).kotlin
//      val filter = test?.let { SpecAwareTestFilter(it, spec) }
//      listOf(spec) to filter
//    }
//
//    val runner = io.kotest.runner.jvm.TestEngine(
//      specs,
//      if (filter == null) emptyList<TestCaseFilter>() else listOf(filter),
//      Project.parallelism(),
//      includeTags,
//      excludeTags,
//      writer
//    )
//    runner.execute()
  }
}
