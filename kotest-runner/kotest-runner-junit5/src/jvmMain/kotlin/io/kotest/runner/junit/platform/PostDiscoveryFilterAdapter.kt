package io.kotest.runner.junit.platform

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.spec
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.filter.toTestFilterResult
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.MethodSource
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

   override fun filter(descriptor: Descriptor): TestFilterResult {
      val testDescriptor = createTestDescriptor(uniqueId, descriptor, descriptor.id.value)
      return filter
         .toPredicate()
         .test(testDescriptor)
         .toTestFilterResult("Excluded by JUnit ClassMethodNameFilter: $filter")
   }

   /**
    * Creates a new [TestDescriptor] from the given Kotest [descriptor], chaining from
    * the [root] uniqueId. The [TestSource] is fudged since JUnit makes assumptions that tests are methods.
    * This descriptor is only used by the filter adapter.
    */
   private fun createTestDescriptor(root: UniqueId, descriptor: Descriptor, displayName: String): TestDescriptor {

      val id: UniqueId = descriptor.chain().fold(root) { acc, desc -> acc.append(desc) }

      val source = when (descriptor) {
         is Descriptor.SpecDescriptor -> ClassSource.from(descriptor.kclass.java)
         // this isn't a method, but we can use MethodSource with the test name, so it's at least
         // somewhat compatible for top level tests.
         is Descriptor.TestDescriptor -> MethodSource.from(descriptor.spec().kclass.java.name, descriptor.path().value)
      }

      return createTestDescriptor(id, displayName, TestDescriptor.Type.CONTAINER, source, false)
   }

}
