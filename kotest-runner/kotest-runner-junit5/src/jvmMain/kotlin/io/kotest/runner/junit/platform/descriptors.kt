package io.kotest.runner.junit.platform

import io.kotest.core.descriptors.Descriptor
import io.kotest.mpp.log
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource

/**
 * Creates a [TestDescriptor] from the given class, and attaches it to the engine,
 * if one does not already exist.
 *
 * The created Test Descriptor will have segment type [Segment.Spec] and will use [displayName].
 */
fun getSpecDescriptor(
   engine: TestDescriptor,
   descriptor: Descriptor.SpecDescriptor,
   displayName: String,
): TestDescriptor {
   val id = engine.uniqueId.append(Segment.Spec.value, descriptor.id.value)
   log { "Looking for $id in ${engine.children.map { it.uniqueId }.joinToString(", ")}" }
   return engine.findByUniqueId(id).orElseGet { null }
      ?: createAndRegisterSpecDescription(engine, descriptor, displayName)
}

private fun createAndRegisterSpecDescription(
   engine: TestDescriptor,
   descriptor: Descriptor.SpecDescriptor,
   displayName: String,
): TestDescriptor {
   val id = engine.uniqueId.append(Segment.Spec.value, descriptor.id.value)
   val source = ClassSource.from(descriptor.kclass.java)
   val testDescriptor: TestDescriptor = object : AbstractTestDescriptor(id, displayName, source) {
      override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER
      override fun mayRegisterTests(): Boolean = true
   }
   log { "Registering spec level TestDescriptor for $id" }
   engine.addChild(testDescriptor)
   return testDescriptor
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
