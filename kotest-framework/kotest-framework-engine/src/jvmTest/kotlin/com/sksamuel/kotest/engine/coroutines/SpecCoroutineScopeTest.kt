package com.sksamuel.kotest.engine.coroutines

import io.kotest.core.annotation.Description
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Description("Tests that spec-level callbacks are executed in a spec-level coroutine that doesn't block tests")
@EnabledIf(LinuxOnlyGithubCondition::class)
class SpecCoroutineScopeTest : FunSpec() {

   init {

      var job1: Job? = null
      var job2: Job? = null
      var job3: Job? = null

      beforeSpec {
         // this job should not stop the beforeSpec from returning
         job1 = it.scope.launch {
            delay(1000000)
         }
      }

      test("a spec level coroutine should not stop a test from completing") {
         job2 = scope.launch {
            delay(1000000)
         }
      }

      afterSpec {
         job1?.cancel()
         job2?.cancel()
         job3 = it.scope.launch {
            delay(100)
         }
      }

      afterProject {
         // spec should have waited for the job to complete
         job3!!.isCompleted shouldBe true
      }
   }
}
