package io.kotest.engine.test.registration

import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.Registration
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestResult
import io.kotest.engine.test.names.DuplicateTestNameHandler

class DuplicateNameHandlingRegistration(
   mode: DuplicateTestNameMode,
   private val delegate: Registration,
) : Registration {

   private val handler = DuplicateTestNameHandler(mode)

   override suspend fun runNestedTestCase(nested: NestedTest): TestResult? {
      val withOverrideName = when (val uniqueName = handler.handle(nested.name)) {
         null -> nested
         else -> nested.copy(name = nested.name.copy(testName = uniqueName))
      }
      return delegate.runNestedTestCase(withOverrideName)
   }
}
