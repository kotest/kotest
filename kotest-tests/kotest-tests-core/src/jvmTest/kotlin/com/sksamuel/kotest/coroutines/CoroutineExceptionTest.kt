package com.sksamuel.kotest.coroutines

import com.sksamuel.kotest.expectFailureExtension
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@UseExperimental(ExperimentalTime::class)
class CoroutineExceptionTest : FunSpec({

   extension(expectFailureExtension)

   test("exception in coroutine") {
      launch {
         error("boom")
      }
   }
})
