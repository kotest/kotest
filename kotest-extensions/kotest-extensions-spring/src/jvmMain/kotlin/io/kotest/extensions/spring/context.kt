@file:Suppress("MemberVisibilityCanBePrivate")

package io.kotest.extensions.spring

import org.springframework.test.context.TestContextManager
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class SpringTestContextCoroutineContextElement(val value: TestContextManager) : AbstractCoroutineContextElement(Key) {
   companion object Key : CoroutineContext.Key<SpringTestContextCoroutineContextElement>
}

/**
 * Determines how the spring test context lifecycle is mapped to test cases.
 *
 * [SpringTestLifecycleMode.Root] will setup and teardown the test context before and after root tests only.
 * [SpringTestLifecycleMode.Test] will setup and teardown the test context only at leaf tests.
 *
 */
enum class SpringTestLifecycleMode {
   Root, Test
}

/**
 * Returns the [TestContextManager] from a test or spec.
 */
suspend fun testContextManager(): TestContextManager =
   coroutineContext[SpringTestContextCoroutineContextElement]?.value
      ?: error("No TestContextManager defined in this coroutine context")
