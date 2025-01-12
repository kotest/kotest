package io.kotest.runner.junit.platform

import io.kotest.core.descriptors.Descriptor
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import kotlin.jvm.optionals.getOrNull

/**
 * Returns the [TestDescriptor] corresponding to the given spec.
 * Specs are always registered when the test suite is created, so this is expected to never fail.
 */
internal fun EngineDescriptor.getSpecTestDescriptor(descriptor: Descriptor.SpecDescriptor): TestDescriptor {
   val id = deriveSpecUniqueId(descriptor.id)
   return findByUniqueId(id).getOrNull() ?: error("Could not find spec TestDescriptor for ${descriptor.id}")
}

/**
 * Creates a [TestDescriptor] from the given spec.
 * This descriptor needs to be added to the engine parent.
 */
internal fun createSpecTestDescriptor(
   engine: EngineDescriptor,
   descriptor: Descriptor.SpecDescriptor,
   displayName: String,
): TestDescriptor {
   val id = engine.deriveSpecUniqueId(descriptor.id)
   val source = ClassSource.from(descriptor.kclass.java)
   return object : AbstractTestDescriptor(id, displayName, source) {
      override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER
      override fun mayRegisterTests(): Boolean = true
   }
}

/**
 * Creates a [TestDescriptor] for the [id], [displayName] and [source].
 *
 * Test case descriptors can be either TEST or CONTAINER depending on if they contain nested tests.
 */
internal fun createTestTestDescriptor(
   id: UniqueId,
   displayName: String,
   type: TestDescriptor.Type,
   source: TestSource?,
): TestDescriptor = object : AbstractTestDescriptor(id, displayName, source) {

   // there is a bug in gradle 4.7+ whereby CONTAINER_AND_TEST breaks test reporting or hangs the build, as it is not handled
   // see https://github.com/gradle/gradle/issues/4912
   // so we can't use CONTAINER_AND_TEST for our test scopes, but simply container
   // update jan 2020: Seems we can use CONTAINER_AND_TEST now in gradle 6, and CONTAINER is invisible in output
   // update sep 2021: gradle 7.1 seems we can use TEST for everything but CONTAINER_AND_TEST will not show without a contained test
   // update for 5.0.0.M2 - will just dynamically add tests after they have completed, and we can see the full tree
   // update 5.0.0.M3 - if we add dynamically afterwards then the timings are all messed up, seems gradle keeps the time itself
   override fun getType(): TestDescriptor.Type = type
   override fun mayRegisterTests(): Boolean = type == TestDescriptor.Type.CONTAINER
}
