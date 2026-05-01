package com.sksamuel.kotest.engine.spec.style

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

/**
 * Regression test: at the DescribeSpec root, `it("name").config(...) { ... }`,
 * `fit("name").config(...) { ... }`, and `xit("name").config(...) { ... }` were
 * always registered as DISABLED, because the addIt helper hardcoded
 * `xmethod = TestXMethod.DISABLED` instead of using its `xmethod` parameter.
 *
 * `it("name").config(...)` should run normally; `xit("name").config(...)` remains
 * disabled.
 */
@EnabledIf(LinuxOnlyGithubCondition::class)
class DescribeSpecRootItDisabledTest : DescribeSpec() {

   companion object {
      val itRanCount = AtomicInteger(0)
   }

   init {
      afterSpec {
         itRanCount.get() shouldBe 1
      }

      it("root it with config").config(enabled = true) {
         itRanCount.incrementAndGet()
      }

      xit("root xit with config").config(enabled = true) {
         error("xit must remain disabled")
      }
   }
}
