package com.sksamuel.multiplatform.tags

import io.kotest.core.NamedTag
import io.kotest.core.annotation.Description
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val A = NamedTag("A")
val B = NamedTag("B")

var invoked = 0

@Description("Tests that the KOTEST_TAGS env var is correctly applied on multiplatform")
class EnvTagTest : FreeSpec() {
   init {

      afterProject {
         invoked shouldBe 1
      }

      "A".config(tags = setOf(A)) {
         invoked++
      }

      "B".config(tags = setOf(B)) {
         invoked++
      }
   }
}
