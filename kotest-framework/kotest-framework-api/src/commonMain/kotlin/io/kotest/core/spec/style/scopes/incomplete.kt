package io.kotest.core.spec.style.scopes

import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestContext

class IncompleteContainerException(val name: String) : Exception("Test '$name' requires at least one nested test")

class IncompleteContainerContext(private val delegate: TestContext) : TestContext by delegate {
   var hasNestedTest = false
   override suspend fun registerTestCase(nested: NestedTest) {
      hasNestedTest = true
      delegate.registerTestCase(nested)
   }
}

