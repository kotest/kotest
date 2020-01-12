package io.kotest.core

import io.kotest.extensions.TagExtension
import io.kotest.extensions.TestCaseExtension

object ProjectConfiguration {

   fun tags(): Tags {
      val tags = tagExtensions().map { it.tags() }
      return if (tags.isEmpty()) Tags.Empty else tags.reduce { a, b -> a.combine(b) }
   }

}

expect fun testCaseFilters(): List<TestCaseFilter>

expect fun testCaseExtensions(): List<TestCaseExtension>

expect fun tagExtensions(): List<TagExtension>
