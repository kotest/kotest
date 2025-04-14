package io.kotest.engine.spec.interceptor

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicBoolean

@EnabledIf(LinuxOnlyGithubCondition::class)
class BeforeSpecRunsExactlyOnce : FunSpec() {

   companion object {
      private val beforeSpecRan = AtomicBoolean()
   }

   override suspend fun beforeSpec(spec: Spec) {
      if (beforeSpecRan.get()) error("boom")
      beforeSpecRan.set(true)
      delay(2)
   }

   init {
      test("test  0") { beforeSpecRan.get() shouldBe true; delay(10) }
      test("test  1") { beforeSpecRan.get() shouldBe true; delay(10) }
   }
}
