package com.sksamuel.kotest.show

import io.kotest.assertions.show.DefaultShow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DefaultShowTest : FunSpec({

   test("DefaultShow.boolean") {
      DefaultShow.show(true).value shouldBe "true"
      DefaultShow.show(false).value shouldBe "false"
   }

   test("DefaultShow.char") {
      DefaultShow.show('a').value shouldBe "'a'"
      DefaultShow.show('w').value shouldBe "'w'"
   }

})
