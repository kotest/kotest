package io.kotest.engine.test.registration

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.Registration
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.test.names.DuplicateTestNameHandler

class DuplicateNameHandlingRegistration(
   private val mode: DuplicateTestNameMode,
   private val delegate: Registration,
) : Registration {

   private val handlers = mutableMapOf<Descriptor.TestDescriptor, DuplicateTestNameHandler>()

   override suspend fun runNestedTestCase(parent: TestCase, nested: NestedTest): TestResult? {
      val handler = handlers.getOrPut(parent.descriptor) { DuplicateTestNameHandler(mode) }
      val withOverrideName = when (val uniqueName = handler.handle(nested.name)) {
         null -> nested
         else -> nested.copy(name = nested.name.copy(testName = uniqueName))
      }
      return delegate.runNestedTestCase(parent, withOverrideName)
   }
}
