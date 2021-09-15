//package io.kotest.engine.testcasename
//
//import io.kotest.core.annotation.Tags
//import io.kotest.core.spec.style.FreeSpec
//import io.kotest.matchers.shouldBe
// todo move to display name formatter tests
//@Tags("DummyClass")
//class FreeSpecWithClassLevelAndTestLevelTag : FreeSpec({
//   "a dummy top level free scope" - {
//      "a dummy middle level free scope" - {
//         "a dummy test" {
//            this.testCase.displayName shouldBe "a dummy test[tags = DummyClass]"
//         }
//      }
//   }
//   "a dummy top level free scope 2" - {
//      "a dummy middle level free scope 2" - {
//         "a dummy test".config(tags = setOf(Dummy, NoUse)) {
//            this.testCase.displayName shouldBe "a dummy test[tags = Dummy, NoUse, DummyClass]"
//         }
//      }
//   }
//})
