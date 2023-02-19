package io.kotest.extensions.blockhound

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DoNotParallelize
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import reactor.blockhound.BlockingOperationError

private suspend fun blockInNonBlockingContext() {
   // Provokes a blocking situation which will be detected if
   // a) BlockHound has been successfully activated, and
   // b) the required 'kotlinx-coroutines-debug' dependency is present.
   withContext(Dispatchers.Default) { // Use a non-blocking dispatcher
      @Suppress("BlockingMethodInNonBlockingContext")
      Thread.sleep(2)
   }
}

@DoNotParallelize
class BlockHoundCaseTest : FunSpec({
   test("detects for test case").config(extensions = listOf(BlockHound())) {
      shouldThrow<BlockingOperationError> { blockInNonBlockingContext() }
   }

   test("prints for test case").config(extensions = listOf(BlockHound(BlockHoundMode.PRINT))) {
      shouldNotThrow<BlockingOperationError> { blockInNonBlockingContext() }
   }

   test("not enabled for test case") {
      shouldNotThrow<BlockingOperationError> { blockInNonBlockingContext() }
   }
})

@DoNotParallelize
class BlockHoundSpecTest : FunSpec({
   extension(BlockHound())

   test("detects for spec") {
      shouldThrow<BlockingOperationError> { blockInNonBlockingContext() }
   }

   test("does not complain for I/O threads") {
      shouldNotThrow<BlockingOperationError> {
         withContext(Dispatchers.IO) { // Use a blocking dispatcher
            Thread.sleep(2)
         }
      }
   }

   /*
      test("configuration failure").config(extensions = listOf(BlockHound(BlockHoundMode.PRINT))) {
         // Cannot register a BlockHound extension twice (spec plus test case).
      }
   */
})
