package io.kotest.property.internal

import io.kotest.property.PropertyContext

suspend fun runTest(
   context: PropertyContext,
   test: suspend PropertyContext.() -> Unit,
   handleFailureAndShrink: suspend (Throwable) -> Unit
) {
   try {
      context.test()
      context.markSuccess()
      // we track assertion errors and try to shrink them
   } catch (e: AssertionError) {
      handleFailureAndShrink(e)
      // any other non assertion error exception is an immediate fail without shrink
   } catch (e: Exception) {
      if (e::class.simpleName == "AssertionError"
         || e::class.simpleName == "AssertionFailedError"
         || e::class.simpleName == "ComparisonFailure"
      ) {
         handleFailureAndShrink(e)
      } else {
         context.markFailure()
         fail(e, context.attempts())
      }
   }
}
