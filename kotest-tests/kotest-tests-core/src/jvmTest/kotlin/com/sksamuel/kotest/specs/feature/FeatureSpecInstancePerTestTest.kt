package com.sksamuel.kotest.specs.feature

import com.sksamuel.kotest.specs.feature.FeatureSpecInstancePerTestHolder.chars
import com.sksamuel.kotest.specs.feature.FeatureSpecInstancePerTestHolder.specs
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

object FeatureSpecInstancePerTestHolder {
   var chars = ""
   var specs = mutableSetOf<Int>()
}

class FeatureSpecInstancePerTestTest : FeatureSpec({

   afterProject {
      chars shouldBe "aabacacdaceacefaceghhihijhik"
      specs.size shouldBe 11
   }

   afterSpec {
      specs.add(it.hashCode())
   }

   isolation = IsolationMode.InstancePerTest

   feature("A") {
      chars += "a"
      scenario("B") {
         chars += "b"
      }
      feature("C") {
         chars += "c"
         scenario("D") {
            chars += "d"
         }
         feature("E") {
            chars += "e"
            scenario("F") {
               chars += "f"
            }
            scenario("G") {
               chars += "g"
            }
         }
      }
   }

   feature("H") {
      chars += "h"
      feature("I") {
         chars += "i"
         scenario("J") {
            chars += "j"
         }
         scenario("K") {
            chars += "k"
         }
      }
   }

})
