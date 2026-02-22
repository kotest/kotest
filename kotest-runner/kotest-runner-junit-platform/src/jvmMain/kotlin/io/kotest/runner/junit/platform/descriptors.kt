package io.kotest.runner.junit.platform

import io.kotest.common.env
import io.kotest.common.isIntellij
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.test.TestCase
import io.kotest.engine.names.LocationEmbedder
import io.kotest.engine.test.names.DisplayNameFormatting
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.engine.support.descriptor.MethodSource
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KClass

internal const val TRUNCATE_TEST_NAMES_ENV = "KOTEST_TRUNCATE_TEST_NAMES"
internal const val MAX_TRUNCATED_NAME_LENGTH = 64

/**
 * Finds and returns the [org.junit.platform.engine.TestDescriptor] corresponding to the
 * given [Descriptor.SpecDescriptor] that was previously added to the engine.
 *
 * If the engine descriptor does not contain the spec descriptor, then null is returned.
 */
internal fun findTestDescriptorForSpec(root: EngineDescriptor, descriptor: Descriptor.SpecDescriptor): TestDescriptor? {
   val id = createUniqueIdForSpec(root.uniqueId, descriptor.id)
   return root.findByUniqueId(id).getOrNull()
}

/**
 * Creates a [org.junit.platform.engine.TestDescriptor] from the given spec.
 * This descriptor needs to be added to the parent engine descriptor.
 */
internal fun createSpecTestDescriptor(
   root: EngineDescriptor,
   descriptor: Descriptor.SpecDescriptor,
   displayName: String,
): TestDescriptor {
   val id = createUniqueIdForSpec(root.uniqueId, descriptor.id)
   val source = ClassSource.from(descriptor.id.value)
   return object : AbstractTestDescriptor(id, displayName, source) {
      override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER
      override fun mayRegisterTests(): Boolean = true
   }
}

/**
 * Creates a [org.junit.platform.engine.TestDescriptor] for the [id], [displayName] and [source].
 *
 * Test case descriptors can be either TEST or CONTAINER depending on if they contain nested tests.
 */
internal fun createTestTestDescriptor(
   id: UniqueId,
   displayName: String,
   type: TestDescriptor.Type,
   source: TestSource,
): TestDescriptor = object : AbstractTestDescriptor(id, displayName, source) {

   // there is a bug in gradle 4.7+ whereby CONTAINER_AND_TEST breaks test reporting or hangs the build, as it is not handled
   // see https://github.com/gradle/gradle/issues/4912
   // so we can't use CONTAINER_AND_TEST for our test scopes, but simply container
   // update jan 2020: Seems we can use CONTAINER_AND_TEST now in gradle 6, and CONTAINER is invisible in output
   // update sep 2021: Gradle 7.1 seems we can use TEST for everything but CONTAINER_AND_TEST will not show without a contained test
   // update for 5.0.0.M2 - will just dynamically add tests after they have completed, and we can see the full tree
   // update 5.0.0.M3 - if we add dynamically afterward then the timings are all messed up, seems Gradle keeps the time itself
   override fun getType(): TestDescriptor.Type = type
   override fun mayRegisterTests(): Boolean = type == TestDescriptor.Type.CONTAINER
}

internal fun createTestDescriptorWithMethodSource(
   root: EngineDescriptor,
   testCase: TestCase,
   type: TestDescriptor.Type,
   formatter: DisplayNameFormatting,
): TestDescriptor {
   val id = createUniqueIdForTest(root.uniqueId, testCase.descriptor)
   val testDescriptor = createTestTestDescriptor(
      id = id,
      displayName = if (isIntellij())
         LocationEmbedder.embeddedTestName(testCase.descriptor, formatter.format(testCase))
      else {
         val name = formatter.format(testCase)
         if (type == TestDescriptor.Type.CONTAINER && env(TRUNCATE_TEST_NAMES_ENV) == "true")
            truncateTestName(name)
         else
            name
      },
      type = type,
      // For CONTAINER types, use ClassSource (like v5.9.1) to ensure a proper tree structure in Android Studio.
      // Android Studio does not display MethodSource containers correctly, hence using ClassSource for them.
      // gradle-junit-platform hides tests if we don't send a source at all
      // surefire-junit-platform (maven) needs a MethodSource to separate test cases from each other
      // and produce a more correct XML report with the test case name.
      source = when (type) {
         TestDescriptor.Type.CONTAINER -> ClassSource.from(testCase.spec::class.java)
         else -> getMethodSource(testCase.spec::class, id)
      },
   )
   return testDescriptor
}

internal fun getMethodSource(kclass: KClass<*>, id: UniqueId): MethodSource = MethodSource.from(
   /* className = */ kclass.java.name,
   /* methodName = */ id.segments.filter { it.type == Segment.Test.value }.joinToString("/") { it.value }
)

internal fun truncateTestName(name: String): String =
   if (name.length <= MAX_TRUNCATED_NAME_LENGTH) name
   else name.take(MAX_TRUNCATED_NAME_LENGTH - 3) + "..."
