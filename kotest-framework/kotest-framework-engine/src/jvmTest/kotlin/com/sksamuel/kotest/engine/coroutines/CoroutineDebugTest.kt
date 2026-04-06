package com.sksamuel.kotest.engine.coroutines

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Isolate
@EnabledIf(LinuxOnlyGithubCondition::class)
class CoroutineDebugTest : FunSpec() {
   init {
      test("coroutine debug should dump coroutine stacks on error") {

         val p = object : AbstractProjectConfig() {
            override val coroutineDebugProbes = true
         }

         val output = captureStandardOut {
            TestEngineLauncher()
               .withListener(NoopTestEngineListener)
               .withSpecRefs(SpecRef.Reference(Wibble::class))
               .withProjectConfig(p)
               .withoutEnvFilters()
               .execute()
               .errors.shouldBeEmpty()
         }
         output shouldContain "Coroutines dump"
      }

      // https://github.com/kotest/kotest/issues/4058
      // foreground coroutines should be awaited when coroutineTestScope=true and coroutineDebugProbes=false
      test("foreground coroutines should complete when coroutineTestScope enabled without debug probes") {
         val output = captureStandardOut {
            TestEngineLauncher()
               .withListener(NoopTestEngineListener)
               .withSpecRefs(SpecRef.Reference(ForegroundTestProbesDisabled::class))
               .withoutEnvFilters()
               .execute()
               .errors.shouldBeEmpty()
         }
         output shouldContain "In a foreground scope"
         output shouldNotContain "Coroutines dump"
      }

      // https://github.com/kotest/kotest/issues/4058
      // foreground coroutines should also be awaited when coroutineDebugProbes=true at the root spec level
      test("foreground coroutines should complete before debug dump when coroutineTestScope and coroutineDebugProbes both enabled at root spec level") {
         val output = captureStandardOut {
            TestEngineLauncher()
               .withListener(NoopTestEngineListener)
               .withSpecRefs(SpecRef.Reference(ForegroundTestProbesEnabled::class))
               .withoutEnvFilters()
               .execute()
               .errors.shouldBeEmpty()
         }
         output shouldContain "In a foreground scope.*Coroutines dump".toRegex(RegexOption.DOT_MATCHES_ALL)
      }

      // https://github.com/kotest/kotest/issues/4058
      // foreground coroutines should be awaited when coroutineTestScope=true and coroutineDebugProbes=true in a nested context
      test("foreground coroutines should complete before debug dump when coroutineTestScope and coroutineDebugProbes both enabled in nested context") {
         val output = captureStandardOut {
            TestEngineLauncher()
               .withListener(NoopTestEngineListener)
               .withSpecRefs(SpecRef.Reference(ForegroundTestProbesEnabledNested::class))
               .withoutEnvFilters()
               .execute()
               .errors.shouldBeEmpty()
         }
         output shouldContain "In a foreground scope.*Coroutines dump".toRegex(RegexOption.DOT_MATCHES_ALL)
      }
   }
}

private class Wibble : FunSpec() {
   init {
      coroutineDebugProbes = true
      test("a") {
         async { delay(1000) }
         error("qwe")
      }
   }
}

// https://github.com/kotest/kotest/issues/4058 - Example 1: works when debug probes disabled
class ForegroundTestProbesDisabled : FunSpec() {
   init {
      coroutineTestScope = true
      coroutineDebugProbes = false
      test("Start foreground task") {
         launch { delay(1000); println("In a foreground scope") }
         println("In the test body")
      }
   }
}

// https://github.com/kotest/kotest/issues/4058 - Example 2: bug when debug probes enabled at root spec level
private class ForegroundTestProbesEnabled : FunSpec() {
   init {
      coroutineTestScope = true
      coroutineDebugProbes = true
      test("Start foreground task") {
         launch { delay(1000); println("In a foreground scope") }
         println("In the test body")
      }
   }
}

// https://github.com/kotest/kotest/issues/4058 - Example 3: works when debug probes enabled in nested context
private class ForegroundTestProbesEnabledNested : FunSpec() {
   init {
      context("With debug probes") {
         coroutineTestScope = true
         coroutineDebugProbes = true
         test("Start foreground task") {
            launch { delay(1000); println("In a foreground scope") }
            println("In the test body")
         }
      }
   }
}
