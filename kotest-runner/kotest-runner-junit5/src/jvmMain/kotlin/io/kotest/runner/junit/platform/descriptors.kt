package io.kotest.runner.junit.platform

import io.kotest.core.descriptors.Descriptor
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource

/**
 * Creates a new spec-level [TestDescriptor] from the given class, appending it to the
 * [engine] descriptor. The created descriptor will have segment type [Segment.Spec]
 * and will use [displayName].
 */
fun createDescriptorForSpec(
   descriptor: Descriptor.SpecDescriptor,
   displayName: String,
   engine: TestDescriptor
): TestDescriptor {
   val source = ClassSource.from(descriptor.kclass.java)
   val id = engine.uniqueId.append(descriptor)
   return createTestDescriptor(id, displayName, TestDescriptor.Type.CONTAINER, source, true).apply {
      engine.addChild(this)
   }
}

/**
 * Creates a [TestDescriptor] for the given [testCase] and attaches it to the [parent].
 * The created descriptor will have segment type [Segment.Test] and will use [displayName].
 */
fun createTestDescriptor(
   id: UniqueId,
   displayName: String,
   type: TestDescriptor.Type,
   source: TestSource?,
   mayRegisterTests: Boolean,
): TestDescriptor = object : AbstractTestDescriptor(id, displayName, source) {

   // there is a bug in gradle 4.7+ whereby CONTAINER_AND_TEST breaks test reporting or hangs the build, as it is not handled
   // see https://github.com/gradle/gradle/issues/4912
   // so we can't use CONTAINER_AND_TEST for our test scopes, but simply container
   // update jan 2020: Seems we can use CONTAINER_AND_TEST now in gradle 6, and CONTAINER is invisible in output
   // update sep 2021: gradle 7.1 seems we can use TEST for everything but CONTAINER_AND_TEST will not show without a contained test
   // update for 5.0.0.M2 - will just dynamically add tests after they have completed, and we can see the full tree
   // update 5.0.0.M3 - if we add dynamically afterwards then the timings are all messed up, seems gradle keeps the time itself
   override fun getType(): TestDescriptor.Type = type
   override fun mayRegisterTests(): Boolean = mayRegisterTests
}
