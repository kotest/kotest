package io.kotest.runner.junit5

import io.kotest.core.spec.description
import io.kotest.core.spec.SpecConfiguration
import io.kotest.runner.jvm.SpecFilter
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.launcher.PostDiscoveryFilter
import java.util.*
import kotlin.reflect.KClass

// gradles uses a post discovery filter called [ClassMethodNameFilter] when a user runs gradle
// with either `-- tests someClass` or by adding a test filter section to their gradle build.
// This filter class makes all kinds of assumptions around what is a test and what isn't,
// so we must fool it by creating a dummy test descriptor.
// This is liable to be buggy, and should be stripped out as soon as gradle
// fix their bugs around junit 5 support, if ever.
class ClassMethodAdaptingFilter(private val filter: PostDiscoveryFilter,
                                private val uniqueId: UniqueId) : SpecFilter {
   override fun invoke(klass: KClass<out SpecConfiguration>): Boolean {
      val id = uniqueId.appendSpec(klass.description())
      val descriptor = object : AbstractTestDescriptor(id, klass.description().name) {
         override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER
         override fun getSource(): Optional<TestSource> = Optional.of(ClassSource.from(klass.java))
      }
      val parent = KotestEngineDescriptor(uniqueId, emptyList())
      parent.addChild(descriptor)
      return filter.apply(descriptor).included()
   }
}
