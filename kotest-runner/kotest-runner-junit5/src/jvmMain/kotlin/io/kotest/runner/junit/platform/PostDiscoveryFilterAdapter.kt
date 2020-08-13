package io.kotest.runner.junit.platform

import io.kotest.core.filters.TestFilter
import io.kotest.core.test.Description
import org.junit.platform.engine.UniqueId
import org.junit.platform.launcher.PostDiscoveryFilter


//class ClassMethodAdaptingFilter(
//   private val filter: PostDiscoveryFilter,
//   private val uniqueId: UniqueId
//) : SpecFilter {
//   override fun invoke(klass: KClass<out Spec>): Boolean {
//      val id = uniqueId.appendSpec(klass.description())
//      val descriptor = object : AbstractTestDescriptor(id, klass.description().name.displayName()) {
//         override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER
//         override fun getSource(): Optional<TestSource> = Optional.of(ClassSource.from(klass.java))
//      }
//      val parent = KotestEngineDescriptor(uniqueId, emptyList(), emptyList())
//      parent.addChild(descriptor)
//      return filter.apply(descriptor).included()
//   }
//}

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
   override fun filter(description: Description): Boolean {
      val testDescriptor = description.toTestDescriptor(uniqueId)
      return filter.toPredicate().test(testDescriptor)
   }
}
