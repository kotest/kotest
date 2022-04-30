package io.kotest.engine.test.scopes

import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestScope
import io.kotest.engine.test.names.DuplicateTestNameHandler

/**
 * Wraps a [TestScope] to add support for detecting duplicate test names in a scope, and handling
 * them via a [DuplicateTestNameHandler].
 */
class DuplicateNameHandlingTestScope(
   mode: DuplicateTestNameMode,
   private val delegate: TestScope
) : TestScope by delegate {

   private val handler = DuplicateTestNameHandler(mode)

   // in the single instance runner we execute each nested test as soon as they are registered
   override suspend fun registerTestCase(nested: NestedTest) {
      val withOverrideName = when (val uniqueName = handler.handle(nested.name)) {
         null -> nested
         else -> nested.copy(name = nested.name.copy(testName = uniqueName))
      }
      delegate.registerTestCase(withOverrideName)
   }
}
