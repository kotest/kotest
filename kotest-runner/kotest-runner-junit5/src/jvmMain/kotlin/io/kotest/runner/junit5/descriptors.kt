package io.kotest.runner.junit5

import io.kotest.core.spec.Spec
import io.kotest.core.spec.description
import io.kotest.core.test.Description
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.FilePosition
import org.junit.platform.engine.support.descriptor.FileSource
import java.io.File
import kotlin.reflect.KClass

fun UniqueId.appendSpec(description: Description) = this.append(Segments.spec, description.name)!!

object Segments {
   const val spec = "spec"
   const val test = "test"
}

/**
 * Creates a new spec-level [TestDescriptor] from the given class, appending it to the
 * parent [TestDescriptor]. The created descriptor will have segment type [Segments.spec].
 */
fun KClass<out Spec>.descriptor(parent: TestDescriptor): TestDescriptor {
   val source = ClassSource.from(java)
   return parent.append(description(), TestDescriptor.Type.CONTAINER_AND_TEST, source, Segments.spec)
}

/**
 * The created descriptor will have segment type [Segments.test].
 */
fun TestDescriptor.descriptor(testCase: TestCase): TestDescriptor {
   val pos = when {
      testCase.source.lineNumber < 1 -> null
      else -> FilePosition.from(testCase.source.lineNumber)
   }
   val source = FileSource.from(File(testCase.source.fileName), pos)
   // there is a bug in gradle 4.7+ whereby CONTAINER_AND_TEST breaks test reporting, as it is not handled
   // see https://github.com/gradle/gradle/issues/4912
   // so we can't use CONTAINER_AND_TEST for our test scopes, but simply container
   // update jan 2020: Seems we can use CONTAINER_AND_TEST now in intellij, and CONTAINER is invisible in output
   val type = when (testCase.type) {
      TestType.Container -> TestDescriptor.Type.CONTAINER_AND_TEST
      TestType.Test -> TestDescriptor.Type.TEST
   }
   return append(testCase.description, type, source, Segments.test)
}

/**
 * Creates a new [TestDescriptor] appended to the receiver and adds it as a child of the receiver.
 */
fun TestDescriptor.append(
   description: Description,
   type: TestDescriptor.Type,
   source: TestSource?,
   segment: String
): TestDescriptor {
   val descriptor =
      object : AbstractTestDescriptor(this.uniqueId.append(segment, description.name), description.name, source) {
         override fun getType(): TestDescriptor.Type = type
         override fun mayRegisterTests(): Boolean = type != TestDescriptor.Type.TEST
      }
   this.addChild(descriptor)
   return descriptor
}
