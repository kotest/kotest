package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.email
import io.kotest.property.checkAll

class EmailArbTest : FunSpec({

   test("emails happy path") {
      checkAll(1, PropTestConfig(seed = 1289312), Arb.email()) { e ->
         e.shouldBe("xxkm@regk.nl")
      }
      checkAll(1, PropTestConfig(seed = 8934575), Arb.email()) { e ->
         e.shouldBe("ghnnq@dbagstxjkc.com.br")
      }
      checkAll(1, PropTestConfig(seed = 487643), Arb.email(usernameSize = 8..8)) { e ->
         e.shouldBe("thst.wycb@svpgz.gov")
      }
   }
})
