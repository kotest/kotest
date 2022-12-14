package io.kotest.core.coroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class TestScopeContainer(val testScope: TestScope): CoroutineContext.Element {
   companion object: CoroutineContext.Key<TestScopeContainer>

   override val key: CoroutineContext.Key<*>
      get() = TestScopeContainer
}
