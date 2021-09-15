package io.kotest.runner.junit.platform

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
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
fun createTestDescriptor(
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
   testCase: TestCase,
   displayName: String,
   parent: TestDescriptor,
   type: TestDescriptor.Type,
): TestDescriptor {

   val source = ClassSource.from(testCase.descriptor.spec().kclass.java)
   // FileSource.from(File(desc.source.filename), FilePosition.from(desc.source.lineNumber))

   // there is a bug in gradle 4.7+ whereby CONTAINER_AND_TEST breaks test reporting or hangs the build, as it is not handled
   // see https://github.com/gradle/gradle/issues/4912
   // so we can't use CONTAINER_AND_TEST for our test scopes, but simply container
   // update jan 2020: Seems we can use CONTAINER_AND_TEST now in gradle 6, and CONTAINER is invisible in output
   // update sep 2021: gradle 7.1 seems we can use TEST for everything but CONTAINER_AND_TEST will not show without a contained test
   // update for 5.0.0.M2 - will just dynamically add tests after they have completed, and we can see the full tree
//   val type = when (testCase.type) {
//      TestType.Container -> TestDescriptor.Type.CONTAINER_AND_TEST
//      TestType.Test -> TestDescriptor.Type.TEST
//   }

   val mayRegisterTests = testCase.type == TestType.Container
   val id = parent.uniqueId.append(testCase.descriptor)

   return createTestDescriptor(id, displayName, type, source, mayRegisterTests).apply {
      parent.addChild(this)
   }
}

fun createTestDescriptor(
   id: UniqueId,
   displayName: String,
   type: TestDescriptor.Type,
   source: TestSource?,
   mayRegisterTests: Boolean
): AbstractTestDescriptor = object : AbstractTestDescriptor(id, displayName, source) {
   override fun getType(): TestDescriptor.Type = type
   override fun mayRegisterTests(): Boolean = mayRegisterTests
}
