//package io.kotest.engine.testcasename
//
//import io.kotest.core.annotation.Tags
//import io.kotest.core.spec.style.StringSpec
//import io.kotest.matchers.shouldBe
// todo move to display name formatter tests
//@Tags("DummyClass")
//class StringSpecWithClassLevelAndTestLevelTag : StringSpec({
//   "a dummy test with no tags" {
//      this.testCase.displayName shouldBe "a dummy test with no tags[tags = DummyClass]"
//   }
//   "a dummy test with some tags".config(tags = setOf(Dummy, NoUse)) {
//      this.testCase.displayName shouldBe "a dummy test with some tags[tags = Dummy, NoUse, DummyClass]"
//   }
//})
