package io.kotest.core.plan

import io.kotest.common.ExperimentalKotest
import io.kotest.core.internal.isEnabled
import io.kotest.core.internal.tags.allTags
import io.kotest.core.source
import io.kotest.core.test.TestCase

@ExperimentalKotest
suspend fun TestCase.toNode(): TestNode {
   return TestNode(
      parent = spec::class.toNode(),
      name = NodeName.fromTestName(this.description.name),
      type = this.type,
      source = source(),
      tags = this.allTags(),
      enabled = this.isEnabled(),
      severity = this.config.severity,
   )
}
