package io.kotest.runner.junit5

import io.kotest.core.Description
import io.kotest.core.specs.SpecContainer
import io.kotest.runner.jvm.SpecFilter
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.launcher.PostDiscoveryFilter
import java.util.*

// gradles uses a post discovery filter called [ClassMethodNameFilter] when a user runs gradle
// with either `-- tests someClass` or by adding a test filter section to their gradle build.
// This filter class makes all kinds of assumptions around what is a test and what isn't,
// so we must fool it by creating a dummy test descriptor and passing that through.
// This is liable to be buggy, and should be stripped out as soon as gradle
// fix their bugs around junit 5 support, if ever.
internal class ClassMethodAdaptingFilter(
   private val uniqueId: UniqueId,
   private val filter: PostDiscoveryFilter
) : SpecFilter {

   override fun invoke(container: SpecContainer): Boolean {
      fun UniqueId.appendSpec(description: Description) = this.append("spec", description.name)!!
      val id = uniqueId.appendSpec(Description.spec(container.name.value))
      val descriptor = object : AbstractTestDescriptor(id, container.name.value) {
         override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER
         override fun getSource(): Optional<TestSource> = Optional.of(container.testSource())
      }
      val parent = KotestEngine.KotestEngineDescriptor(uniqueId, emptyList())
      parent.addChild(descriptor)
      return filter.apply(descriptor).included()
   }
}
