package io.kotest.core.coroutines

import io.kotest.common.KotestInternal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
@KotestInternal
class TestScopeContainer(val testScope: TestScope): CoroutineContext.Element {
   companion object: CoroutineContext.Key<TestScopeContainer>

   override val key: CoroutineContext.Key<*>
      get() = TestScopeContainer
}
