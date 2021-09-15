package io.kotest.engine.test.contexts

import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.engine.test.names.DuplicateTestNameHandler
import kotlin.coroutines.CoroutineContext

class DuplicateNameHandlingTestContext(
   mode: DuplicateTestNameMode,
   private val delegate: TestContext
) : TestContext {

   private val handler = DuplicateTestNameHandler(mode)

   override val testCase: TestCase = delegate.testCase
   override val coroutineContext: CoroutineContext = delegate.coroutineContext

   // in the single instance runner we execute each nested test as soon as they are registered
   override suspend fun registerTestCase(nested: NestedTest) {
      val withOverrideName = when (val uniqueName = handler.handle(nested.name)) {
         null -> nested
         else -> nested.copy(name = nested.name.copy(testName = uniqueName))
      }
      delegate.registerTestCase(withOverrideName)
   }
}
