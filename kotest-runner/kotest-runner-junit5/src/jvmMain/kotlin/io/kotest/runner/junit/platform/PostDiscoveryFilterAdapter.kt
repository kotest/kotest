package io.kotest.runner.junit.platform

import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.filter.toTestFilterResult
import io.kotest.core.test.Description
import org.junit.platform.engine.UniqueId
import org.junit.platform.launcher.PostDiscoveryFilter

/**
 * Gradles uses a post discovery filter called [ClassMethodNameFilter] when a user runs gradle
 * with either `-- tests someClass` or by adding a test filter section to their gradle build.
 * This filter class makes all kinds of assumptions around what is a test and what isn't,
 * so we must fool it by creating a test description with a dummy TestSource.
 * This is liable to be buggy, and should be stripped out as soon as gradle
 * fix their bugs around junit 5 support, if ever.
 */
class PostDiscoveryFilterAdapter(
   private val filter: PostDiscoveryFilter,
   private val uniqueId: UniqueId
) : TestFilter {
   override fun filter(description: Description): TestFilterResult {
      val testDescriptor = description.toTestDescriptor(uniqueId)
      return filter.toPredicate().test(testDescriptor).toTestFilterResult()
   }
}
