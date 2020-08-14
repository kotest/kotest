package io.kotest.runner.junit.platform

import io.kotest.engine.config.Project
import io.kotest.engine.KotestEngineSystemProperties
import io.kotest.core.test.Description
import io.kotest.core.test.DescriptionType
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.core.test.format
import io.kotest.extensions.system.toDescription2
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.FilePosition
import org.junit.platform.engine.support.descriptor.FileSource
import org.junit.platform.engine.support.descriptor.MethodSource
import java.io.File
import kotlin.reflect.KClass

fun engineId(): UniqueId = UniqueId.forEngine("kotest")

/**
 * Returns a new [UniqueId] by appending this description to the receiver.
 */
fun UniqueId.append(description: Description): UniqueId {
   val segment = when (description.type) {
      DescriptionType.Spec -> Segment.Spec
      DescriptionType.Container -> Segment.Test
      DescriptionType.Test -> Segment.Test
   }
   return this.append(segment.value, description.name.format(Project.testNameCase(), Project.includeTestScopePrefixes()))
}

sealed class Segment {
   abstract val value: String

   object Spec : Segment() {
      override val value: String = "spec"
   }

   object Test : Segment() {
      override val value: String = "test"
   }
}

/**
 * Creates a new spec-level [TestDescriptor] from the given class, appending it to the
 * parent [TestDescriptor]. The created descriptor will have segment type [Segment.Spec].
 */
fun KClass<out Spec>.descriptor(parent: TestDescriptor): TestDescriptor {
   val source = ClassSource.from(java)
   return parent.append(toDescription2(), TestDescriptor.Type.CONTAINER, source, Segment.Spec)
}

/**
 * Creates a [TestDescriptor] for the given [TestCase] and attaches it to the receiver as a child.
 * The created descriptor will have segment type [Segment.Test].
 */
fun TestDescriptor.descriptor(testCase: TestCase): TestDescriptor {

   val pos = if (testCase.source.lineNumber <= 0) null else FilePosition.from(testCase.source.lineNumber)
   val source = FileSource.from(File(testCase.source.fileName), pos)
   // there is a bug in gradle 4.7+ whereby CONTAINER_AND_TEST breaks test reporting or hangs the build, as it is not handled
   // see https://github.com/gradle/gradle/issues/4912
   // so we can't use CONTAINER_AND_TEST for our test scopes, but simply container
   // update jan 2020: Seems we can use CONTAINER_AND_TEST now in gradle 6, and CONTAINER is invisible in output
   val type = when (testCase.type) {
      TestType.Container -> if (System.getProperty(KotestEngineSystemProperties.gradle5) == "true") TestDescriptor.Type.CONTAINER else TestDescriptor.Type.CONTAINER_AND_TEST
      TestType.Test -> TestDescriptor.Type.TEST
   }
   return append(testCase.description, type, source, Segment.Test)
}

/**
 * Creates a new [TestDescriptor] appended to the receiver and adds it as a child of the receiver.
 */
fun TestDescriptor.append(
   description: Description,
   type: TestDescriptor.Type,
   source: TestSource?,
   segment: Segment
): TestDescriptor = append(description.name.format(Project.testNameCase(), Project.includeTestScopePrefixes()), type, source, segment)

/**
 * Creates a new [TestDescriptor] appended to the receiver and adds it as a child of the receiver.
 */
fun TestDescriptor.append(
   name: String,
   type: TestDescriptor.Type,
   source: TestSource?,
   segment: Segment
): TestDescriptor {
   val descriptor =
      object : AbstractTestDescriptor(this.uniqueId.append(segment.value, name), name, source) {
         override fun getType(): TestDescriptor.Type = type
         override fun mayRegisterTests(): Boolean = type != TestDescriptor.Type.TEST
      }
   this.addChild(descriptor)
   return descriptor
}

/**
 * Returns a new [TestDescriptor] created from this [Description].
 * The [TestSource] is fudged since JUnit makes assumptions that tests are methods.
 */
fun Description.toTestDescriptor(root: UniqueId): TestDescriptor {

   val id = this.chain().fold(root) { acc, op -> acc.append(op) }

   val source = when (this.type) {
      DescriptionType.Spec -> ClassSource.from(this.specClass.java)
      DescriptionType.Container -> MethodSource.from(this.specClass.java.name, this.path().value)
      DescriptionType.Test -> MethodSource.from(this.specClass.java.name, this.path().value)
   }

   val type = when (this.type) {
      DescriptionType.Spec -> TestDescriptor.Type.CONTAINER
      DescriptionType.Container -> TestDescriptor.Type.CONTAINER
      DescriptionType.Test -> TestDescriptor.Type.TEST
   }

   return object : AbstractTestDescriptor(id, this.name.format(Project.testNameCase(), Project.includeTestScopePrefixes()), source) {
      override fun getType(): TestDescriptor.Type = type
   }
}
