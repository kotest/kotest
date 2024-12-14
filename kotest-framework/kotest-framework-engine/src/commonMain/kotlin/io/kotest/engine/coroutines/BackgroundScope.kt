package io.kotest.engine.coroutines

import io.kotest.common.KotestInternal
import io.kotest.core.test.TestScope
import kotlinx.coroutines.CoroutineScope

@OptIn(KotestInternal::class)
val TestScope.backgroundScope: CoroutineScope
   get() {
      val testScopeContainer: TestScopeElement = this.coroutineContext[TestScopeElement]
         ?: error("Test scope not available. You need to set coroutineTestScope = true to enable it")

      return testScopeContainer.testScope.backgroundScope
   }
