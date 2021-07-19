package io.kotest.runner.junit.platform

import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.plan.Descriptor
import io.kotest.core.plan.DisplayName
import io.kotest.core.plan.toDescriptor
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.mpp.bestName
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.FilePosition
import org.junit.platform.engine.support.descriptor.FileSource
import org.junit.platform.engine.support.descriptor.MethodSource
import java.io.File
import kotlin.reflect.KClass
import org.junit.platform.engine.TestDescriptor as JUnitTestDescriptor

fun engineId(): UniqueId = UniqueId.forEngine("kotest")

/**
 * Returns a new [UniqueId] by appending this description to the receiver.
 */
fun UniqueId.append(descriptor: Descriptor): UniqueId {
   return when (descriptor) {
      is Descriptor.SpecDescriptor -> this.append(Segment.Spec.value, descriptor.displayName.value)
      is Descriptor.TestDescriptor -> this.append(Segment.Test.value, descriptor.displayName.value)
   }
}

sealed class Segment {
   abstract val value: String

   object Spec : Segment() {
      override val value: String = "spec"
   }

   object Script : Segment() {
      override val value: String = "script"
   }

   object Test : Segment() {
      override val value: String = "test"
   }
}

/**
 * Creates a new spec-level [JUnitTestDescriptor] from the given class, appending it to the
 * parent [JUnitTestDescriptor]. The created descriptor will have segment type [Segment.Spec].
 */
fun KClass<out Spec>.descriptor(parent: JUnitTestDescriptor): JUnitTestDescriptor {
   val source = ClassSource.from(java)
   return parent.append(toDescriptor(), JUnitTestDescriptor.Type.CONTAINER, source, Segment.Spec)
}

/**
 * Creates a new spec-level [JUnitTestDescriptor] from the given spec name, appending it to the
 * parent [JUnitTestDescriptor]. The created descriptor will have segment type [Segment.Spec].
 */
fun Descriptor.SpecDescriptor.descriptor(parent: JUnitTestDescriptor): JUnitTestDescriptor {
   val source = ClassSource.from(this.kclass.bestName())
   return parent.append(displayName.value, JUnitTestDescriptor.Type.CONTAINER, source, Segment.Script)
}

/**
 * Creates a [JUnitTestDescriptor] for the given [TestCase] and attaches it to the receiver as a child.
 * The created descriptor will have segment type [Segment.Test].
 */
fun JUnitTestDescriptor.descriptor(testCase: TestCase): JUnitTestDescriptor {

   val pos = if (testCase.source.lineNumber <= 0) null else FilePosition.from(testCase.source.lineNumber)
   val source = FileSource.from(File(testCase.source.fileName), pos)
   // there is a bug in gradle 4.7+ whereby CONTAINER_AND_TEST breaks test reporting or hangs the build, as it is not handled
   // see https://github.com/gradle/gradle/issues/4912
   // so we can't use CONTAINER_AND_TEST for our test scopes, but simply container
   // update jan 2020: Seems we can use CONTAINER_AND_TEST now in gradle 6, and CONTAINER is invisible in output
   val type = when (testCase.type) {
      TestType.Container -> if (System.getProperty(KotestEngineProperties.gradle5) == "true") JUnitTestDescriptor.Type.CONTAINER else TestDescriptor.Type.CONTAINER_AND_TEST
      TestType.Test -> JUnitTestDescriptor.Type.TEST
   }
   return append(testCase.descriptor, type, source, Segment.Test)
}

/**
 * Creates a [JUnitTestDescriptor] for the given [Descriptor.TestDescriptor] and attaches it to the receiver as a child.
 * The created descriptor will have segment type [Segment.Test].
 */
fun JUnitTestDescriptor.descriptor(desc: Descriptor.TestDescriptor): JUnitTestDescriptor {

   val source = FileSource.from(File(desc.source.filename), FilePosition.from(desc.source.lineNumber))

   // there is a bug in gradle 4.7+ whereby CONTAINER_AND_TEST breaks test reporting or hangs the build, as it is not handled
   // see https://github.com/gradle/gradle/issues/4912
   // so we can't use CONTAINER_AND_TEST for our test scopes, but simply container
   // update jan 2020: Seems we can use CONTAINER_AND_TEST now in gradle 6, and CONTAINER is invisible in output
   val type = when (desc.type) {
      TestType.Container -> if (System.getProperty(KotestEngineProperties.gradle5) == "true") JUnitTestDescriptor.Type.CONTAINER else TestDescriptor.Type.CONTAINER_AND_TEST
      TestType.Test -> JUnitTestDescriptor.Type.TEST
   }
   return append(desc.displayName, type, source, Segment.Test)
}

/**
 * Creates a new [JUnitTestDescriptor] appended to the receiver and adds it as a child of the receiver.
 */
fun JUnitTestDescriptor.append(
   description: Descriptor,
   type: JUnitTestDescriptor.Type,
   source: TestSource?,
   segment: Segment
): JUnitTestDescriptor = append(
   description.displayName,
   type,
   source,
   segment
)

/**
 * Creates a new [JUnitTestDescriptor] appended to the receiver and adds it as a child of the receiver.
 */
fun JUnitTestDescriptor.append(
   displayName: DisplayName,
   type: JUnitTestDescriptor.Type,
   source: TestSource?,
   segment: Segment
): JUnitTestDescriptor = append(
   displayName.value,
   type,
   source,
   segment
)

/**
 * Creates a new [TestDescriptor] appended to the receiver and adds it as a child of the receiver.
 */
fun JUnitTestDescriptor.append(
   name: String,
   type: JUnitTestDescriptor.Type,
   source: TestSource?,
   segment: Segment
): JUnitTestDescriptor {
   val descriptor =
      object : AbstractTestDescriptor(this.uniqueId.append(segment.value, name), name, source) {
         override fun getType(): JUnitTestDescriptor.Type = type
         override fun mayRegisterTests(): Boolean = type != JUnitTestDescriptor.Type.TEST
      }
   this.addChild(descriptor)
   return descriptor
}

/**
 * Returns a new [JUnitTestDescriptor] created from this [Description].
 * The [TestSource] is fudged since JUnit makes assumptions that tests are methods.
 */
fun Descriptor.toTestDescriptor(root: UniqueId): JUnitTestDescriptor {

   val id = this.chain().fold(root) { acc, op -> acc.append(op) }

   val source = when (this) {
      is Descriptor.SpecDescriptor -> ClassSource.from(this.kclass.java)
      // this isn't a method, but we can use MethodSource with the test name so it's at least
      // compatible for top level tests.
      is Descriptor.TestDescriptor -> MethodSource.from(this.spec().kclass.java.name, this.testPath().value)
   }

   val type = when (this) {
      is Descriptor.SpecDescriptor -> JUnitTestDescriptor.Type.CONTAINER
      is Descriptor.TestDescriptor -> when (this.type) {
         TestType.Container -> JUnitTestDescriptor.Type.CONTAINER
         TestType.Test -> JUnitTestDescriptor.Type.TEST
      }
   }

   return object : AbstractTestDescriptor(id, this.displayName.value, source) {
      override fun getType(): JUnitTestDescriptor.Type = type
   }
}
