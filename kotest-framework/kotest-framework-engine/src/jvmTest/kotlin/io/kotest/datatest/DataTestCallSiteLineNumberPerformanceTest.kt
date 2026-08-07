package io.kotest.datatest

import io.kotest.common.KotestInternal
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeLessThan
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

/**
 * [getDataTestTagConfig] is called once per withXXX call site to tag generated tests
 * with their source line number (used by the IntelliJ plugin to run a single data test row).
 * On the JVM this resolves the call site via a stack walk, the same mechanism used by
 * [io.kotest.core.source.sourceRef] (see [com.sksamuel.kotest.engine.test.SourceRefTest] for that
 * counterpart test) - it must stay cheap for specs with many withData/withXXX call sites.
 */
@EnabledIf(LinuxOnlyGithubCondition::class)
class DataTestCallSiteLineNumberPerformanceTest : FunSpec() {
   init {
      test("data test call site line number lookup should be performant").config(timeout = 20.seconds) {
         @OptIn(KotestInternal::class)
         val duration = measureTime {
            repeat(5_000) {
               getDataTestTagConfig()
            }
         }
         // after PR https://github.com/kotest/kotest/pull/6203
         // old impl: with the old Class.forName + kotlin-reflect isSubclassOf
         // new impl: with the StackWalker + plain java.lang.Class

         // retune if flaky on CI
         duration.inWholeMilliseconds shouldBeLessThan 600L
      }
   }
}
