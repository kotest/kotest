//package io.kotest.engine.testcasename
//
//import io.kotest.core.spec.style.FreeSpec
//import io.kotest.matchers.shouldBe
// todo move to display name formatter tests
//class FreeSpecWithTagsInTest : FreeSpec({
//   "a dummy top level free scope" - {
//      "a dummy middle level free scope" - {
//         "a dummy test" {
//            this.testCase.displayName shouldBe "a dummy test"
//         }
//      }
//   }
//   "a dummy top level free scope 2" - {
//      "a dummy middle level free scope 2" - {
//         "a dummy test".config(tags = setOf(Dummy, NoUse)) {
//            this.testCase.displayName shouldBe "a dummy test[tags = Dummy, NoUse]"
//         }
//      }
//   }
//})
