package io.kotest.engine.testcasename

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class StringSpecWithTagsInTest : StringSpec({
   "a dummy test with no tags" {
      this.testCase.displayName shouldBe "a dummy test with no tags"
   }
   "a dummy test with some tags".config(tags = setOf(Dummy, NoUse)) {
      this.testCase.displayName shouldBe "a dummy test with some tags[tags = Dummy, NoUse]"
   }
})
