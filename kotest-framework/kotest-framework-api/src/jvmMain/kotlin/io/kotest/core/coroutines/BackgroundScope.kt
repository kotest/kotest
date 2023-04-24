package io.kotest.core.coroutines

import io.kotest.common.KotestInternal
import io.kotest.core.test.TestScope
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@OptIn(KotestInternal::class)
val TestScope.backgroundScope: CoroutineScope
   get() {
      val testScopeContainer: TestScopeContainer = this.coroutineContext[TestScopeContainer]
         ?: error("Test scope not available. You need to set coroutineTestScope = true to enable it")

      return testScopeContainer.testScope.backgroundScope
   }
